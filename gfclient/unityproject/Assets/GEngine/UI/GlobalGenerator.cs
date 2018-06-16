using UnityEngine;
using System.Collections;

namespace GEngine
{
    public class GlobalGenerator : MonoBehaviour
    {
        void Awake()
        {
            InitGameMangager();
        }

        public void InitGameMangager()
        {
            string name = "GameManagers";
            GameObject manager = GameObject.Find(name);
            if (manager == null)
            {
                manager = new GameObject(name);
                manager.name = name;
                manager.AddComponent<WindowManager>();
                //manager.AddComponent<GameLaunch>();
            }
        }
    }
}