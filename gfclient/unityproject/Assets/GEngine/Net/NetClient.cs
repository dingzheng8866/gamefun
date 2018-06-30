using GEngine.Pool;
using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using UnityEngine;
using ProtoBuf;
using System.Text;
//using CC.Runtime.PB;

namespace GEngine.Net
{
    public enum SocketStatus
    {
        None,
        Ready,
        Ok,
        ReReady,
        Errored,
        Reconn,
        Reconning
    }

    public enum NetClientEventType
    {
        Unknown,
        Connected,
        DisConnected,
        ReConnected
    }

    public class NetClientEvent : ISimpleObjectPoolItem
    {
        public int socketId = 0; // internal client unique id
        public NetClientEventType eventType = NetClientEventType.Unknown;
        public int retCode = 0;

        public NetClientEvent()
        {
            Reset();
        }

        public void Destroy()
        {
        }

        public void Reset()
        {
            retCode = 0;
            socketId = 0;
            eventType = NetClientEventType.Unknown;
        }
    }

    public class NetClient
    {
        
        private IPEndPoint endp;
        private Socket socket;
        private SocketStatus status = SocketStatus.None;

        private Queue<NetRawData> dataAsyncPool = null;

        private NetRawData rdata;
        public int sid;

        private string serverURL;
        private int serverPort;

        private bool running = false;

        private long lastSendHeartBeatMessageTime = 0;

        private long lastHeartBeatActiveTime = 0;
        private float heartBeatTimeoutValue = 5.0f;
        public bool heartBeatSwitchOnFlag = false;

        private bool tempSocketConnectOKFlag = false;
        private bool needReconnectFlag = false;

        private Thread workerThread = null;

        private bool startToRealConnect = false;
        private long startToRealConnectTime = 0;

        //private Stream heartbeatMsg = null;
        //private C_HeartBeat_0x110 heartbeatMsg;

        //private System.Object sendLocker = new System.Object();

        private Queue<NetClientEvent> eventQueue = new Queue<NetClientEvent>();

        private bool hasConnectedBefore = false;

        public Action<NetClient> sendHeartBeatAction;

        private List<string> ignoreResendMessages = new List<string>();
        private Dictionary<string, Stream> cachedResendMessages = new Dictionary<string, Stream>();
        private List<string> cachedResendMessagesList = new List<string>();


        public NetClient AddIgnoreResendMessage(string id)
        {
            ignoreResendMessages.Add(id);
            return this;
        }

        private bool isMessageNeedToResend(string id)
        {
            return !ignoreResendMessages.Contains(id);
        }

        public NetClient(int internalSocketId)
        {
            this.sid = internalSocketId;
            dataAsyncPool = new Queue<NetRawData>();
        }

        public void SetSendHeartBeatAction(Action<NetClient> sendHeartBeatAction)
        {
            this.sendHeartBeatAction = sendHeartBeatAction;
            heartBeatSwitchOnFlag = sendHeartBeatAction == null ? false : true;
        }

        private void FireNetEvent(NetClientEventType eventType) // put it in my queue, cost it in unity main thread, because of unity frame limitation
        {
            NetClientEvent ne = DataObjectPool.Factory<NetClientEvent>();
            ne.socketId = sid;
            ne.eventType = eventType;
            lock(eventQueue)
            {
                Debug.Log("fireNetEvent: " + sid + " --> " + eventType);
                eventQueue.Enqueue(ne);
            }
        }

        public void GetNetEvents(List<NetClientEvent> list)
        {
            lock (eventQueue)
            {
                while(eventQueue.Count > 0)
                {
                    list.Add(eventQueue.Dequeue());
                }
            }
        }

        public void GetAllMessage(List<NetRawData> list)
        {
            lock (this)
            {
                while (dataAsyncPool.Count > 0)
                {
                    list.Add(dataAsyncPool.Dequeue());
                }
            }
        }

        private void UpdateLatestHeartBeatActiveTime()
        {
            lastHeartBeatActiveTime = DateTime.Now.Ticks;
            //Debug.Log(DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss.ffff") + " UpdateLatestHeartBeatActiveTime");
        }

        private void UpdateLastSendHeartBeatMessageTime()
        {
            lastSendHeartBeatMessageTime = DateTime.Now.Ticks;
        }

        public bool isRunning()
        {
            return running;
        }

        private bool ReadData()
        {
            bool gotData = false;
            try
            {
                lock(this)
                {
                    int avail = socket.Available;
                    while (avail > 0)
                    {
                        UpdateLatestHeartBeatActiveTime();
                        gotData = true;
                        //lastSendTime = -1;
                        //Debug.Log("ReadData: " + avail);
                        //if (NetConst.IsPrintLog) Debug.Log(DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss.ffff") + " ReadData available: " + avail);

                        if (rdata.total > NetConst.ReceiveBufferSize && Application.isEditor)
                        {
                            Debug.LogError("Extend net receive buffer size: " + NetConst.ReceiveBufferSize + ", total: " + rdata.total);
                        }

                        int _bufferLength = rdata.buffer.Length;
                        if (rdata.total >= _bufferLength)
                        {
                            Array.Resize<byte>(ref rdata.buffer, rdata.total + 100);
                        }

                        int byt = socket.Receive(rdata.buffer, rdata.read, rdata.total - rdata.read, SocketFlags.None);

                        avail -= byt;
                        rdata.read += byt;
                        if (rdata.read == rdata.total)
                        {
                            if (rdata.state == ReadState.Header)
                            {
                                int len = BitConverter.ToInt32(rdata.buffer, 0);
                                int msgNameLength = BitConverter.ToInt32(rdata.buffer, 4);
                                int msgContentLength = len - 8 - msgNameLength;
                                Debug.Log("len=" + len + ", msgNameLength=" + msgNameLength + ", msgContentLength=" + msgContentLength);

                                rdata.msgNameLen = msgNameLength;
                                rdata.read = 0;
                                rdata.total = len - 8;  // minus 8 bytes head length
                                rdata.state = ReadState.Content;
                                rdata.socketId = sid;
                            }
                            else
                            {
                                rdata.stream.Seek(0, SeekOrigin.Begin);
                                rdata.stream.SetLength(0);
                                string msgName = Encoding.UTF8.GetString(rdata.buffer, 0, rdata.msgNameLen); // BitConverter.ToString(rdata.buffer, 0, rdata.msgNameLen);
                                Debug.Log("msgName=" + msgName);

                                rdata.msgName = msgName;
                                if(rdata.total - rdata.msgNameLen > 0)
                                {
                                    rdata.stream.Write(rdata.buffer, rdata.msgNameLen, rdata.total- rdata.msgNameLen);
                                    rdata.stream.SetLength(rdata.total - rdata.msgNameLen);
                                }

                                Debug.Log("======================== " + (rdata.total - rdata.msgNameLen));
      
                                dataAsyncPool.Enqueue(rdata);

                                rdata = DataObjectPool.Factory<NetRawData>();
                                rdata.socketId = sid;
                            }
                        }

                    }
                }


            }
            catch (Exception ex)
            {
                Debug.LogError(ex);
                MarkReconnect();
            }
            return gotData;
        }

        private string BytesToString(byte[] bytes)
        {
            string s = "";
            string gap = ",";
            for (int i = 0; i < bytes.Length; i++)
            {
                s += bytes[i];
                if (i < bytes.Length - 1)
                {
                    s += gap;
                }
            }
            return s;
        }


        private bool FactorySocket()
        {
            bool flag = false;
            try
            {
                IPAddress[] addrs = Dns.GetHostAddresses(serverURL);
                if (addrs.Length > 0)
                {
                    if (endp == null)
                    {
                        endp = new IPEndPoint(addrs[0], serverPort);
                    }

#if UNITY_WEBPLAYER
                if( !Security.PrefetchSocketPolicy(addrs[0].ToString(),serverPort,3000) ){
                    Debugger.LogError("Get Socket Polily Failed");
                    return;
                }
#endif
                    if (addrs[0].AddressFamily == AddressFamily.InterNetworkV6)
                        socket = new Socket(AddressFamily.InterNetworkV6, SocketType.Stream, ProtocolType.Tcp);
                    else
                        socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);

                    flag = true;
                }
            }
            catch (Exception e)
            {
                Debug.LogError(DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss.ffff") + " FactorySocket fatal error: " + e.Message);
                //sFatalError.Dispatch(sid);
            }
            return flag;
        }

        private void SocketConnectAsyncCallBack(IAsyncResult asyncResult)
        {
            Debug.Log(DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss.ffff") + " SocketConnectAsyncCallBack start");
            try
            {
                Socket socket = asyncResult.AsyncState as Socket;
                if (socket != null)
                {
                    socket.EndConnect(asyncResult);
                    //Debug.Log(DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss.ffff") + " SocketConnectAsyncCallBack socket ok");
                    if (NetConst.IsPrintLog) Debug.Log(DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss.ffff") + " ConnectSocket ok ep=" + endp + "  socketId" + sid);
                    tempSocketConnectOKFlag = true;
                    needReconnectFlag = false;

                    status = SocketStatus.Ok;
                    rdata = DataObjectPool.Factory<NetRawData>();
                    UpdateLatestHeartBeatActiveTime();
                    UpdateLastSendHeartBeatMessageTime();
                    //sConnect.Dispatch(sid);

                    if(!hasConnectedBefore)
                    {
                        FireNetEvent(NetClientEventType.Connected);
                        hasConnectedBefore = true;
                    }
                    else
                    {
                        FireNetEvent(NetClientEventType.ReConnected);
                    }
                }
            }
            catch (Exception e)
            {
                Debug.Log(DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss.ffff") + " SocketConnectAsyncCallBack error: " + e);
            }
        }

        public void Start(string url, int port)
        {
            if(!running)
            {
                this.serverURL = url;
                this.serverPort = port;
                running = true;
                workerThread = new Thread(WorkerThreadRun);
                workerThread.Start();
            }
        }

        private void MarkReconnect()
        {
            lock(this)
            {
                needReconnectFlag = true;
                FireNetEvent(NetClientEventType.DisConnected);
                if (socket != null)
                {
                    try
                    {
                        socket.Close();
                    }
                    catch (Exception)
                    {

                    }
                    socket = null;
                }
                if (rdata != null) rdata.Reset();
            }

            Debug.LogError(DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss.ffff") + " Mark need to reconnect to ep=" + endp + "  socketId:" + sid);
            //sDisconnect.Dispatch(sid);
        }

        public bool isSocketOK()
        {
            lock(this)
            {
                return socket != null && status == SocketStatus.Ok && socket.Connected;
            }
        }

        private bool isNeedToSendHeartbeatMessage()
        {
            if(!isHeartBeatEnable())
            {
                return false;
            }
            long timeNow = DateTime.Now.Ticks;
            //long ticksDelta = timeNow - lastHeartBeatActiveTime;
            long tickDelta2 = timeNow - lastSendHeartBeatMessageTime;

            if (tickDelta2 >= NetConst.NetHeartBeatInterval * 10000000) //ticksDelta >= NetConst.NetHeartBeatInterval * 10000000 && 
            {
                return true;
            }
            return false;
        }

        private bool isHeartBeatEnable()
        {
            return heartBeatSwitchOnFlag;
        }

        private bool IsNetworkUnreachableByHeartBeat()
        {
            bool flag = false;
            if (isHeartBeatEnable())
            {
                long ticksDelta = DateTime.Now.Ticks - lastHeartBeatActiveTime;
                if (lastHeartBeatActiveTime != 0 && (ticksDelta >= heartBeatTimeoutValue * 10000000)) // 5s seems good value
                {
                    flag = true;
                    Debug.Log(DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss.ffff") + "NetworkUnreachableByHeartBeat to socket id: " + sid);
                }
            }
            return flag;
        }

        private void sendHeartbeatMessage()
        {
            //Debug.Log(DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss.ffff")+" sendHeartbeatMessage");
            UpdateLastSendHeartBeatMessageTime();

            if(sendHeartBeatAction==null)
            {
                Debug.LogError("Internal error: heartbeat msg could not be null");
                return;
            }

            sendHeartBeatAction(this);
        }

        public void SendMessage<T>(T m)
        {
            Stream str = new MemoryStream();
            try
            {
                Serializer.Serialize<T>(str, m);
            }
            catch (Exception e)
            {
                Debug.Log("SendMessage exception : " + e.Message);
            }
            string msgName = GetMessageName<T>();
            if (NetConst.IsPrintLog) Debug.Log("network log : C-->S : " + "socketId=" + sid +", req: "+ msgName+", " + m );

            SendProtoMessage(msgName, str);
        }

        public string GetMessageName<T>()
        {
            string s = typeof(T).Name;
            return s;
        }

        private void WorkerThreadRun()
        {
            while (running)
            {
                bool needSleep = true;
                //Debug.Log("Thread.running....");
                try
                {
//#if UNITY_EDITOR
                    //Debug.Log(UnityEditor.EditorApplication.isPaused);
                    if(isNeedToForceCloseSocketByAppPause())
                    {
                        if(isSocketOK())
                        {
                            socket.Close();
                            socket = null;
                            //status = SocketStatus.Errored;
                            workingFlag = false;
                            Debug.Log("Close socket for app pause");
                        }
                    }
                    else
                    {
                        workingFlag = true;
                    }
//#endif

                    if(!workingFlag)
                    {
                        Thread.Sleep(5);
                        continue;
                    }

                    if (isSocketOK())
                    {
                        bool gotDataFlag = ReadData();
                        if (gotDataFlag)
                        {
                            needSleep = false;
                        }
                        else
                        {
                            if(IsNetworkUnreachableByHeartBeat())
                            {
                                MarkReconnect();
                            }
                            else
                            {
                                if (isNeedToSendHeartbeatMessage())
                                {
                                    sendHeartbeatMessage();
                                }
                            }
                        }
                    }
                    else
                    {
                        //Debug.LogError("needReconnectFlag ---------- socket not ok");
                        needReconnectFlag = true;
                    }

                    if (needReconnectFlag)
                    {
                        Connect();
                    }
                }
                catch (Exception e)
                {
                    Debug.LogException(e);
                }
                if (needSleep)
                {
                    Thread.Sleep(5);
                }
            }
        }

        private void Connect()
        {
            //Socket sock, IPEndPoint ep
            //socket, endp
            if (socket == null)
            {
                startToRealConnect = false;
                FactorySocket();
            }

            if (socket != null)
            {
                if (!startToRealConnect)
                {
                    if (NetConst.IsPrintLog) Debug.Log(DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss.ffff") + " ConnectSocket ep=" + endp + "  socketId" + sid);
                    //yield return null;
                    socket.BeginConnect(endp, new AsyncCallback(SocketConnectAsyncCallBack), socket);
                    startToRealConnect = true;
                    startToRealConnectTime = DateTime.Now.Ticks;
                    tempSocketConnectOKFlag = false;
                }
                else
                {
                    if (!tempSocketConnectOKFlag)
                    {
                        long ticksDelta = DateTime.Now.Ticks - startToRealConnectTime;
                        if (ticksDelta >= NetConst.NetConnectTimeOut * 10000000)
                        {
                            Debug.Log("ConnectSocket timeout: " + NetConst.NetConnectTimeOut);
                            try
                            {
                                socket.Close();
                            }
                            catch (Exception)
                            {
                            }
                            socket = null;
                            startToRealConnect = false;
                            tempSocketConnectOKFlag = false;
                        }
                    }
                }
            }
        }

        public bool Send(byte[] buffer, int offset, int size)
        {
            lock (this)
            {
                if (isSocketOK())
                {
                    SocketError sr;
                    int i = socket.Send(buffer, offset, size, SocketFlags.None, out sr);

                    if (i == -1 || sr != SocketError.Success)
                    {
                        if (NetConst.IsPrintLog) Debug.Log(sr.ToString());
                        //Reconnect();
                        MarkReconnect();

                        return false;
                    }
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }

        protected bool SendProtoMessageImpl(string msgid, Stream stream)
        {
            if (NetConst.IsPrintLog) Debug.Log("SendProtoMessageImpl msg id:" + msgid + ", socketId=" + sid + ", proto req length: "+ stream.Length);
            //return de.send(Send, msgid, stream);

            // TODO: encrypt
            byte[] reqName = Encoding.UTF8.GetBytes(msgid);

            int totalLen = 4/*协议长度 int*/ + 4/*msgName length*/ + reqName.Length+ (int)stream.Length;
            if (totalLen > NetConst.ReceiveBufferSize && Application.isEditor) // max send buffer size
            {
                Debug.LogError("Exceed net max send size: " + totalLen);
            }

            byte[] outBuffer = new byte[totalLen];

            BitConverter.GetBytes(totalLen).CopyTo(outBuffer, 0);
            BitConverter.GetBytes(msgid.Length).CopyTo(outBuffer, 4);
            reqName.CopyTo(outBuffer, 8);

            if(stream.Length > 0)
            {
                byte[] content = new byte[stream.Length];
                stream.Read(content, 0, (int)stream.Length);
                content.CopyTo(outBuffer, 8 + reqName.Length);
            }

            return Send(outBuffer, 0, outBuffer.Length);
        }

        private void CacheResendMessage(string id, Stream stream)
        {
            if(isMessageNeedToResend(id))
            {
                if (!cachedResendMessages.ContainsKey(id))
                {
                    cachedResendMessages[id] = stream;
                    cachedResendMessagesList.Add(id);
                    if (NetConst.IsPrintLog) Debug.Log("CacheResendMessage msg id:" + id + ",  socketId=" + sid);
                }
            }
        }

        public void ResendCachedMessage()
        {
            //for(int i=0; i< cachedResendMessagesList.Count; i++)
            while(cachedResendMessagesList.Count > 0)
            {
                string id = cachedResendMessagesList[0];
                if(!isSocketOK())
                {
                    break;
                }
                else
                {
                    Stream stream = null;
                    cachedResendMessages.TryGetValue(id, out stream);
                    if (NetConst.IsPrintLog) Debug.Log("ResendCachedMessage msg id:" + id + ",  socketId=" + sid);
                    if (stream!=null)
                    {
                        SendProtoMessage(id, stream);
                    }
                }
            }
        }

        private void ReleaseSendMessageResource(string id, Stream stream = null)
        {
            Stream streamToRelease = null;
            if (cachedResendMessages.ContainsKey(id))
            {
                cachedResendMessages.TryGetValue(id, out streamToRelease);
            }

            if(streamToRelease == null)
            {
                streamToRelease = stream;
            }

            if(stream!=null)
            {
                stream.Dispose();
            }
            cachedResendMessages.Remove(id);
            cachedResendMessagesList.Remove(id);
        }

        public void SendProtoMessage(string msgName, Stream stream)
        {
            if (isSocketOK())
            {
                if(SendProtoMessageImpl(msgName, stream))
                {
                    ReleaseSendMessageResource(msgName, stream);
                }
                else
                {
                    CacheResendMessage(msgName, stream);
                }
            }
            else
            {
                CacheResendMessage(msgName, stream);
            }
        }

        private void DestroyAllCachedMessages()
        {
            foreach (Stream s in cachedResendMessages.Values)
            {
                if (s != null)
                {
                    s.Dispose();
                }
            }
            cachedResendMessages.Clear();
            cachedResendMessagesList.Clear();
        }

        public void Stop()
        {
            Debug.Log("About to stop socket: " + sid);
            lock(this)
            {
                running = false;
                status = SocketStatus.None;
                if (socket != null)
                {
                    socket.Close();
                    socket = null;
                }

                if (rdata != null) rdata.Reset();
                dataAsyncPool.Clear();
                eventQueue.Clear();

                lastHeartBeatActiveTime = 0;

                tempSocketConnectOKFlag = false;
                needReconnectFlag = false;
                heartBeatSwitchOnFlag = false;

                sendHeartBeatAction = null;

                workerThread = null;

                startToRealConnect = false;
                startToRealConnectTime = 0;
                ignoreResendMessages.Clear();
                DestroyAllCachedMessages();
            }

        }

        private bool workingFlag = true;

//#if UNITY_EDITOR
        private long lastUserActiveTime = 0;
        private int maxUserPauseTime = 10; // second

        public void NotifyAppPlayingStatus(bool isPlaying)
        {
            lastUserActiveTime = DateTime.Now.Ticks;
        }

        private bool isNeedToForceCloseSocketByAppPause()
        {
            if(lastUserActiveTime > 0 && (DateTime.Now.Ticks - lastUserActiveTime) > 10000000 * maxUserPauseTime)
            {
                return true;
            }
            return false;
        }

//#endif

        private void encode()
        {

        }


    }
}
