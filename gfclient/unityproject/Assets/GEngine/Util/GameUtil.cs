using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;

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
            if (instance == null) instance = go.AddComponent<T>();
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

    }
}
