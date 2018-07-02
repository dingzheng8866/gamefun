using GEngine.Net;
using GEngine.Net.Proto;
using GEngine.Service;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class TestMain : MonoBehaviour {

	// Use this for initialization
	void Start () {
        Debug.Log("main test" + (int)SocketId.Gate);

        GameService.Instance.login();
        
	}
	


}
