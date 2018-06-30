using GEngine.Net;
using GEngine.Net.Proto;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;

namespace GEngine.Service
{
    public class GameService : MonoBehaviour
    {
        private static GameService _Instance;
        public static GameService Instance
        {
            get
            {
                if (_Instance == null)
                {
                    GameObject go = GameObject.Find("GameManagers");
                    if (go == null) go = new GameObject("GameManagers");
                    GameObject.DontDestroyOnLoad(go);

                    _Instance = go.GetComponent<GameService>();
                    if (_Instance == null) _Instance = go.AddComponent<GameService>();
                }
                return _Instance;
            }
        }

        public void Awake()
        {
            _Instance = this;
            Debug.Log("GameService init");
            NetClientManager.instance.AddCallback<S_Exception>(S_Exception);
            SocketManager.Instance.AddConnectedCallback((int)SocketId.Gate, OnSocketConnectToGateServer);
        }

        private void S_Exception(S_Exception msg)
        {
            //Debug.Log("S_Exception");
            Debug.Log(msg.code + "," + msg.description + " ==>" + msg.trace);
        }

        public void login()
        {
            Debug.Log("Login...");
            NetClientManager.instance.Connect((int)SocketId.Gate, "127.0.0.1", 17981);
        }

        private void OnSocketConnectToGateServer(SocketId socketId)
        {
            if (socketId == SocketId.Gate)
            {
                Debug.Log("Connected to gate server");
                C_GetLoginServerInfo getLoginServerReq = new C_GetLoginServerInfo();
                NetClientManager.instance.SendMessage<C_GetLoginServerInfo>((int)SocketId.Gate, getLoginServerReq);
            }
        }

    }
}
