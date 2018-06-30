using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Net.Sockets;
using UnityEngine;

namespace GEngine.Net
{
    public class SocketManager : MonoBehaviour
    {

        private static SocketManager _Instance;
        public static SocketManager Instance
        {
            get
            {
                if (_Instance == null)
                {
                    GameObject go = GameObject.Find("GameManagers");
                    if (go == null) go = new GameObject("GameManagers");
                    GameObject.DontDestroyOnLoad(go);

                    _Instance = go.GetComponent<SocketManager>();
                    if (_Instance == null) _Instance = go.AddComponent<SocketManager>();
                }
                return _Instance;
            }
        }


        public bool hasEnterGameServerBefore = false;

        private Dictionary<SocketId, Action<SocketId>> connectCallbackDict = new Dictionary<SocketId, Action<SocketId>>();
        private Dictionary<SocketId, Action<SocketId>> disconnectCallbackDict = new Dictionary<SocketId, Action<SocketId>>();
        private Dictionary<SocketId, Action<SocketId, int>> reconnectCallbackDict = new Dictionary<SocketId, Action<SocketId, int>>();

        private List<SocketId> reconnectingList = new List<SocketId>();

        public void Awake()
        {
            _Instance = this;
            GEngine.Net.NetClientManager.instance.InitClients((int)SocketId.Max);
            GEngine.Pool.DataObjectPool.CreatePool(typeof(GEngine.Net.NetRawData), new GEngine.Pool.SimpleObjectPool<GEngine.Net.NetRawData>(), 50);
            GEngine.Pool.DataObjectPool.CreatePool(typeof(GEngine.Net.NetClientEvent), new GEngine.Pool.SimpleObjectPool<GEngine.Net.NetClientEvent>(), 20);

            GEngine.Net.NetClientManager.instance.eventHandler += HandleNetEvent;
        }

        //void Send_C_ReConnect_0x101(SocketId socketId)
        //{
        //    C_ReConnect_0x101 msg = new C_ReConnect_0x101();
        //    LuaTable user = LuaClient.Instance.GetLuaTable("User");
        //    msg.session_id = user["session_id"] as string;
        //
        //    GEngine.Net.NetClientManager.instance.SendMessage<C_ReConnect_0x101>((int)socketId, msg);
        //}

        public void Stop(SocketId sid)
        {
            GEngine.Net.NetClientManager.instance.Stop((int)sid);
        }

        public void StopAll()
        {
            GEngine.Net.NetClientManager.instance.StopAll();
        }

        public void Close(SocketId sid)
        {
            GEngine.Net.NetClientManager.instance.Close((int)sid);
        }

        public void CloseAll()
        {
            GEngine.Net.NetClientManager.instance.CloseAll();
        }

        private bool IsSocketRunning(SocketId sid)
        {
            return GEngine.Net.NetClientManager.instance.IsClientRunning((int)sid);
        }

        public bool IsBattleSocketRunning()
        {
            return IsSocketRunning(SocketId.Battle); // || IsSocketRunning(SocketId.BattleWatch)
        }

        public void Connect(SocketId sid, string server, int port)
        {
            GEngine.Net.NetClientManager.instance.Connect((int)sid, server, port);
        }

        public void AddConnectedCallback(SocketId socketId, Action<SocketId> callback)
        {
            Action<SocketId> hc;
            if (!connectCallbackDict.TryGetValue(socketId, out hc))
            {
                hc = callback;
                connectCallbackDict[socketId] = hc;
            }
            else
            {
                hc += callback;
            }
        }

        public void AddDisconnectCallback(SocketId socketId, Action<SocketId> callback)
        {
            Action<SocketId> hc;
            if (!disconnectCallbackDict.TryGetValue(socketId, out hc))
            {
                hc = callback;
                disconnectCallbackDict[socketId] = hc;
            }
            else
            {
                hc += callback;
            }
        }

        public void AddReconnectCallback(SocketId socketId, Action<SocketId, int> callback)
        {
            Action<SocketId, int> hc;
            if (!reconnectCallbackDict.TryGetValue(socketId, out hc))
            {
                hc = callback;
                reconnectCallbackDict[socketId] = hc;
            }
            else
            {
                hc += callback;
            }
        }

        public void RemoveDisconnectCallback(SocketId socketId, Action<SocketId> callback)
        {
            Action<SocketId> hc;
            if (disconnectCallbackDict.TryGetValue(socketId, out hc))
            {
                hc -= callback;
            }
        }

        public void RemoveReconnectCallback(SocketId socketId, Action<SocketId, int> callback)
        {
            Action<SocketId, int> hc;
            if (reconnectCallbackDict.TryGetValue(socketId, out hc))
            {
                hc -= callback;
            }
        }

        public void RemoveConnectCallback(SocketId socketId, Action<SocketId> callback)
        {
            Action<SocketId> hc;
            if (connectCallbackDict.TryGetValue(socketId, out hc))
            {
                hc -= callback;
            }
        }

        public void Update()
        {
            GEngine.Net.NetClientManager.instance.ProcessNetEvents();
            GEngine.Net.NetClientManager.instance.ProcessMessages();

            UpdateAppPlayingStatus();
        }


        internal void HandleNetEvent(GEngine.Net.NetClientEvent data)
        {
            if (data.eventType == GEngine.Net.NetClientEventType.Connected)
            {
                if ((SocketId)data.socketId == SocketId.Main)
                {
                    hasEnterGameServerBefore = true;
                }
                Debug.Log("HandleNetEvent --> " + data.eventType + " : " + data.socketId);

                Action<SocketId> hc;
                if (connectCallbackDict.TryGetValue((SocketId)data.socketId, out hc))
                {
                    hc((SocketId)data.socketId);
                }
            }
            else if (data.eventType == GEngine.Net.NetClientEventType.DisConnected)
            {
                //Debug.LogError("data.eventType == GEngine.Net.NetClientEventType.DisConnected=================================" + ((SocketId)data.socketId).ToString());

                Debug.Log("HandleNetEvent --> " + data.eventType + " : " + data.socketId);
                Action<SocketId> hc;
                if (disconnectCallbackDict.TryGetValue((SocketId)data.socketId, out hc))
                {
                    hc((SocketId)data.socketId);
                }
            }
            else if (data.eventType == GEngine.Net.NetClientEventType.ReConnected)
            {
                Debug.Log("HandleNetEvent --> " + data.eventType + " : " + data.socketId);
                //Send_C_ReConnect_0x101((SocketId)data.socketId);
                //reconnectingList.Add((SocketId)data.socketId); // 
            }
        }

        /*
        public void NotifyReconnect(S_ReConnect_0x101 msg) // TODO: better to add socket id here
        {
            foreach (SocketId sid in reconnectingList)
            {
                GEngine.Net.NetClientManager.instance.ResendCachedSendMessages((int)sid);

                Action<SocketId, int> hc;
                if (reconnectCallbackDict.TryGetValue((SocketId)sid, out hc))
                {
                    hc((SocketId)sid, msg.retcode);
                }
            }
            reconnectingList.Clear();
        }
        */

        void OnApplicationQuit()
        {
            //GEngine.Pool.DataObjectPool.Destroy();
            //Debug.LogError("OnApplicationQuit ---------- socket manager");
            CloseAll();
            GEngine.Pool.DataObjectPool.DestroyPool<GEngine.Net.NetRawData>();
            GEngine.Pool.DataObjectPool.DestroyPool<GEngine.Net.NetClientEvent>();
        }


        //#if UNITY_EDITOR
        void UpdateAppPlayingStatus()
        {
            GEngine.Net.NetClientManager.instance.NotifyAppPlayingStatus(Application.isPlaying);
        }
        //#endif
    }
}
