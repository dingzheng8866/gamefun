using GEngine.Net;
using GEngine.Net.Proto;
using GEngine.UI;
using GEngine.UI.Controller;
using GEngine.Util;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;

namespace GEngine.Service
{
    public enum RoleStatus
    {
        Unknown,
        ConnectingToGateServer,
        ConnectingToGameServer,
        Online,
        ConnectingToFightServer,
        Fighting,
    }

    public class GameService : MonoBehaviour
    {
        private static GameService _Instance;
        public static GameService Instance { get { if (_Instance == null) { _Instance = GameUtil.SingletonInstance<GameService>(); } return _Instance; } }

        public RoleStatus roleStatus = RoleStatus.Unknown;
        public float lastActionTime = 0;
        public float accumulateDeltaTime = 0;

        public void Awake()
        {
            _Instance = this;
            Debug.Log("GameService awake");
            NetClientManager.instance.AddCallback<S_Exception>(HandleS_Exception);
            SocketManager.Instance.AddConnectedCallback((int)SocketId.Gate, OnSocketConnectToGateServer);
        }

        private void Update()
        {
            if(roleStatus!=RoleStatus.Unknown)
            {
                accumulateDeltaTime += Time.deltaTime;
            }

            if (roleStatus==RoleStatus.ConnectingToGateServer)
            {
                if(Time.realtimeSinceStartup - lastActionTime>=2)
                {
                    Debug.LogError("Failed to connect to gate server: " + GetGateServerIp());
                    NetClientManager.instance.Close((int)SocketId.Gate);
                    // notify user
                    roleStatus = RoleStatus.Unknown;
                    Debug.Log("error: failed to connect to gate server");
                    // TODO: open error panel
                    PanelManager.closePanel(GEngine.GameConst.UI_Panel_Name_GamePreLoad);

                    popExceptionPanel("error: failed to connect to gate server");
                    //PanelManager.openPanel(GEngine.GameConst.UI_Panel_Name_Error, GEngine.GameConst.UI_Panel_Path_Error, SetErrorPanelInfo);
                }
            }

        }

        //public delegate void openPanelCallback(GameObject panelObject);
        private void SetErrorPanelInfo(GameObject panelObject)
        {
            Debug.Log("SetErrorPanelInfo");
            ErrorPanelController ctl = panelObject.GetComponent<ErrorPanelController>();
            if(ctl!=null)
            {
                ctl.titleId = 100004;
                ctl.descId = 100004;
            }
        }

        private void openExceptionPanelCallback(GameObject panelObject, params object[] arguments)
        {
            Debug.Log("openExceptionPanelCallback");
            ExceptionPanelController ctl = panelObject.GetComponent<ExceptionPanelController>();
            if (ctl != null)
            {
                if (arguments!=null && arguments.Length > 0)
                {
                    Debug.Log("openExceptionPanelCallback call back arguments: " + arguments.Length);
                    ctl.SetException((string)arguments[0]);
                }
            }
        }

        private void popExceptionPanel(string detail)
        {
            PanelManager.openPanel(GEngine.GameConst.UI_Panel_Name_Exception, GEngine.GameConst.UI_Panel_Path_Exception, openExceptionPanelCallback, detail);
        }

        private void HandleS_Exception(S_Exception msg)
        {
            //Debug.Log("S_Exception");
            Debug.Log(msg.code + "," + msg.description + " ==>" + msg.trace);
            string detail = "Error: " + msg.code + "\n"+msg.description+"\n"+msg.trace;
            popExceptionPanel(detail);
        }

        private string GetGateServerIp()
        {
            return "127.0.0.1";
        }

        public void init()
        {
            Debug.Log("GameService init...");
            roleStatus = RoleStatus.ConnectingToGateServer;
            lastActionTime = Time.realtimeSinceStartup;
            NetClientManager.instance.Connect((int)SocketId.Gate, GetGateServerIp(), 17981);
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
