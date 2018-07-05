using GEngine.Net;
using GEngine.Net.Proto;
using GEngine.Service;
using GEngine.UI;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class TestMain : MonoBehaviour {

	// Use this for initialization
	void Start () {
        Debug.Log("main test" + (int)SocketId.Gate);

        GameService.Instance.init();

        PanelManager.openPanel(GEngine.GameConst.UI_Panel_Name_GamePreLoad, GEngine.GameConst.UI_Panel_Path_GamePreLoad);


    }
	


}
