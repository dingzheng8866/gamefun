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
//using CC.Runtime.PB;
//using Games.Module.Sysmsgs;

namespace GEngine.Net
{
    public enum SocketId
    {
        Gate,
        Main,
        Battle,
        Max,
    }

    public interface IProtocolHandler
    {
        void Handle(Stream stream);
    }

    public class ProtocolHandler<T> : IProtocolHandler
    {
        public void Handle(Stream stream)
        {
            stream.Seek(0, SeekOrigin.Begin);
            T msg = Serializer.Deserialize<T>(stream);
            Debug.Log("deserialize: " + msg +", " + stream.Length);
            if (OnReceive != null)
            {
                OnReceive(msg);
            }
            else
            {
                Type s = typeof(T);
                Debug.LogWarning("No handler to handle msg type: " + s);
            }
        }
        public Action<T> OnReceive;
    }

    public class NetClientManager
    {
        #region singlton
        private static readonly NetClientManager _instance = new NetClientManager();

        private NetClientManager()
        {
            handler += Handle;
        }

        public static NetClientManager instance
        {
            get
            {
                return _instance;
            }
        }
        #endregion

        private NetClient[] clients;
        private List<NetRawData> messageList = new List<NetRawData>();

        private List<NetClientEvent> eventList = new List<NetClientEvent>();

        private Dictionary<string, IProtocolHandler> protocolHandlers = new Dictionary<string, IProtocolHandler>();

        public Action<NetRawData> handler;

        public Action<NetClientEvent> eventHandler;

        //private C_HeartBeat_0x110 heartBeatMessage = new C_HeartBeat_0x110();
        //public static int heartbeatCode = Convert.ToInt32("0x110", 16);

        public string GetMessageName<T>()
        {
            string s = typeof(T).Name;
            return s;
        }

        public void InitClients(int size)
        {
            clients = new NetClient[size];
        }

        public void AddCallback<T>(Action<T> proc)
        {
            string n = GetMessageName<T>();
            IProtocolHandler iph;
            if(!protocolHandlers.TryGetValue(n, out iph))
            {
                iph = new ProtocolHandler<T>();
                protocolHandlers[n] = iph;
            }
            ProtocolHandler<T> pht = iph as ProtocolHandler<T>;
            pht.OnReceive += proc;
        }

        public void RemoveCallback<T>(Action<T> proc)
        {
            string n = GetMessageName<T>();
            IProtocolHandler iph;
            if (protocolHandlers.TryGetValue(n, out iph))
            {
                ProtocolHandler<T> pht = iph as ProtocolHandler<T>;
                pht.OnReceive -= proc;
            }
        }

        public void ResendCachedSendMessages(int sid)
        {
            NetClient client = clients[sid];
            if (client != null)
            {
                client.ResendCachedMessage();
            }
            else
            {
                // this should not happen, it's internal bug
                Debug.LogError("ResendCachedSendMessages internal error, socket is not inited to socket id: " + sid);
            }
        }
    
        public void SendMessage<T>(int socketId, T m)
        {
            NetClient client = clients[socketId];
            if (client != null)
            {
                client.SendMessage<T>(m);
            }
            else
            {
                // this should not happen, it's internal bug
                Debug.LogError("SendMessage internal error, socket is not inited to message: " + typeof(T).FullName + "  socketId=" + socketId);
            }
        }

        public void ProcessMessages()
        {
            List<NetRawData> list = GetAllMessage();
            foreach(NetRawData data in list)
            {
                //Handle(data);
                if (handler!=null)
                {
                    handler(data);
                }
                DataObjectPool.Back<NetRawData>(data);
            }
        }


        public void ProcessNetEvents()
        {
            List<NetClientEvent> list = GetAllEvents();
            foreach (NetClientEvent data in list)
            {
                if(eventHandler!=null)
                {
                    eventHandler(data);
                }
                DataObjectPool.Back<NetClientEvent>(data);
            }
        }

        internal void Handle(NetRawData rdata)
        {
            //if (heartbeatCode == rdata.proto)
            //{
            //    return; // lua no need to handle heartbeat message
            //}

            if (NetConst.IsPrintLog) Debug.Log("network log receive : S->C msgid = " + rdata.msgName + ", socketId=" + rdata.socketId +", len: " + rdata.total);

            GEngine.Net.IProtocolHandler handler;
            if (protocolHandlers.TryGetValue(rdata.msgName, out handler))
            {
                try
                {
                    handler.Handle(rdata.stream);
                }
                catch (Exception e)
                {
                    Debug.LogErrorFormat("Error S->C msgid={0}, socketId={1}, error={2}", rdata.msgName, rdata.socketId, e);
                }
            }
        }

        public List<NetClientEvent> GetAllEvents()
        {
            eventList.Clear();
            if (clients != null)
            {
                foreach (NetClient client in clients)
                {
                    if (client != null) //&& client.sid!=0// 0 is login, login is special case
                    {
                        client.GetNetEvents(eventList);
                    }
                }
            }
            return eventList;
        }

        public List<NetRawData> GetAllMessage()
        {
            messageList.Clear();
            if (clients != null)
            {
                foreach (NetClient client in clients)
                {
                    if (client != null)
                    {
                        client.GetAllMessage(messageList);
                    }
                }
            }
            return messageList;
        }

        public void Stop(int sid)
        {
            if (clients != null)
            {
                int i = (int)sid;
                if (clients[i] != null)
                {
                    clients[i].Stop();
                }
            }
        }

        public void StopAll()
        {
            if (clients != null)
            {
                foreach (NetClient client in clients)
                {
                    if (client != null)
                    {
                        client.Stop();
                    }
                }
            }
        }

        public void Close(int sid)
        {
            if (clients != null)
            {
                int i = (int)sid;
                if (clients[i] != null)
                {
                    clients[i].Stop();
                    clients[i] = null;
                    Debug.Log("Closed socket: " + sid);
                }
            }
        }

        public void CloseAll()
        {
            if (clients != null)
            {
                for (int i = 0; i < clients.Length; i++)
                {
                    if (clients[i] != null)
                    {
                        clients[i].Stop();
                        clients[i] = null;
                        Debug.Log("Closed socket: " + i);
                    }
                }
            }
        }

        public bool HasNetWorkIssue()
        {
            if (clients != null)
            {
                for (int i = 0; i < clients.Length; i++)
                {
                    if (clients[i] != null && !clients[i].isSocketOK())
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        public bool IsClientRunning(int sid)
        {
            if (clients==null || clients[sid] == null)
            {
                return false;
            }
            return clients[sid].isRunning();
        }

        public bool IsClientRunningWell(int sid)
        {
            if (clients == null || clients[sid] == null)
            {
                return false;
            }
            return clients[sid].isSocketOK();
        }

        public void Connect(int sid, string server)
        {
            string[] arr = server.Split(':');
            string ip = arr[0];
            int port = arr.Length > 1 ? (string.IsNullOrEmpty(arr[1]) ? 80 : Convert.ToInt32(arr[1])) : 80;
            Connect(sid, ip, port);
        }

        internal void SendHeartBeatMessage(NetClient client)
        {
            //client.SendMessage<C_HeartBeat_0x110>(heartBeatMessage);
        }

        public void Connect(int sid, string url, int port)
        {
            Debug.Log(DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss.ffff") + " NetClinetManager.Connect sid=" + sid + " url=" + url + " port=" + port);
            int i = (int)sid;

            if (clients[i] == null)
            {
                if (string.IsNullOrEmpty(url)) //|| url.ToUpper() == OFFLINE
                {
                    //Offline(sid);
                    Debug.Log(DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss.ffff") + " NetClinetManager.Connect url is not valid to sid=" + sid);
                }
                else
                {
                    NetClient client = new NetClient(sid);
                    //client.AddIgnoreResendMessage(heartbeatCode);
                    if (sid!= (int)SocketId.Gate) // 0 is login, special case handling
                    {
                        client.SetSendHeartBeatAction(SendHeartBeatMessage);
                    }
                    clients[i] = client;
                    client.Start(url, port);
                }
            }
        }


//#if UNITY_EDITOR
        public void NotifyAppPlayingStatus(bool isPlaying)
        {
            if (clients != null)
            {
                for (int i = 0; i < clients.Length; i++)
                {
                    if (clients[i] != null)
                    {
                        clients[i].NotifyAppPlayingStatus(isPlaying);
                        //Debug.Log(DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss.ffff") + " NetClinetManager.fresh sid");
                    }
                }
            }
        }
		//#endif

		
		public void EnableNetStats(bool enabled)
		{
			if (enabled && !netStatsEnabled)
			{
				totalNetAmount = 0;
			}

			netStatsEnabled = enabled;
		}

		public bool netStatsEnabled = false;
		public int totalNetAmount = 0;

    }
}
