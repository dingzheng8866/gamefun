using GEngine.UI;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;
using UnityEngine.UI;

namespace GEngine.Util
{
    public class GameUtil
    {

        public static T SingletonInstance<T>() where T : Component
        {
            GameObject go = GameObject.Find("SingletoGOManager");
            if (go == null) go = new GameObject("SingletoGOManager");
            //GameObject.DontDestroyOnLoad(go);

            T instance = go.GetComponent<T>();
            if (instance == null)
            {
                instance = go.AddComponent<T>();
                Debug.Log("SingletoGOManager AddComponent " + typeof(T).Name);
            }
            return instance;
        }


        public static string GetGameLoginDeviceID()
        {
            // TODO: apple use gamecenter id
            return GetDeviceID();
        }

        public static string GetDeviceID()
        {
            return SystemInfo.deviceUniqueIdentifier;
        }

        public static string GetOS()
        {
            return SystemInfo.operatingSystem;
        }

        public static string GetSystemInfo()
        {
            string info = ""
                + "deviceModel:" + "\"" + SystemInfo.deviceModel + "\", "
                + "device:" + "\"" + SystemInfo.deviceUniqueIdentifier + "\", "
                + "OS:" + "\"" + SystemInfo.operatingSystem + "\", "
                + "core:" + SystemInfo.processorCount + ", "
                + "Mem:" + SystemInfo.systemMemorySize + ", "
                + "GMem:" + SystemInfo.graphicsMemorySize + ","
                + "Mono:" + (UnityEngine.Profiling.Profiler.GetMonoUsedSize() / 1000000) + "M,"
                + "Allo:" + (UnityEngine.Profiling.Profiler.GetTotalAllocatedMemory() / 1000000) + "M,"
                + "Texure:" + SystemInfo.maxTextureSize + ""

                + "";
            return info;
        }

        public static void addListenerToCloseFrontendPanel(Transform transform, string buttonPath)
        {
            transform.Find(buttonPath).gameObject.GetComponent<Button>().onClick.AddListener(delegate () {
                PanelManager.closeFrontendPanel();
            });
        }

        public static List<string> convertToLineStringList(byte[] fileContent)
        {
            List<string> list = new List<string>();
            if (fileContent != null && fileContent.Length > 0)
            {
                string[] lines = System.Text.Encoding.UTF8.GetString(fileContent).Split('\n');
                if (lines != null && lines.Length > 0)
                {
                    foreach (string asset in lines)
                    {
                        if (asset != null && asset.Trim().Length > 0)
                        {
                            list.Add(asset.Replace('\r', ' ').Trim());
                        }
                    }
                }
            }
            return list;
        }


    }
}
