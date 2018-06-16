using UnityEngine;
using System.Collections;

namespace GEngine
{
    public class WindowManager : MonoBehaviour
    {
        private static WindowManager _Instance;
        public static WindowManager Instance
        {
            get
            {
                if (_Instance == null)
                {
                    GameObject go = GameObject.Find("GameManagers");
                    if (go == null) go = new GameObject("GameManagers");

                    _Instance = go.GetComponent<WindowManager>();
                    if (_Instance == null) _Instance = go.AddComponent<WindowManager>();
                }
                return _Instance;
            }
        }
        // Use this for initialization
        void Start()
        {

        }

        // Update is called once per frame
        void Update()
        {

        }
    }
}

