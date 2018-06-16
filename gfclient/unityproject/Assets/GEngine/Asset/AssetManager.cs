using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;
using GEngine.Util;

namespace GEngine.Asset
{
    public class AssetManager : MonoBehaviour
    {

        public static AssetManager _Instance = null; //SingletonHelper.SingletonInstance<AssetManager>();
        public static AssetManager Instance { get { if (_Instance == null) { _Instance = SingletonHelper.SingletonInstance<AssetManager>();} return _Instance; }}


        public static void LogTimeCost(string action, System.DateTime begin)
        {
            //if (LogLevel < (int)LoadingLogLevel.ShowTime)
            //    return;

            Debug.LogFormat("[Action] {0}, {1}ms", action, (System.DateTime.Now - begin).TotalMilliseconds);
        }


        public static string RootPathStreaming
        {
            get
            {
                return Application.streamingAssetsPath + "/";
            }
        }

        public static string RootPathPersistent
        {
            get
            {
#if UNITY_STANDALONE
                switch (Application.platform)
                {
                    case RuntimePlatform.WindowsPlayer:
                        return Application.dataPath + "/../res/";
                    case RuntimePlatform.OSXPlayer:
                        return Application.dataPath + "/res/";
                }
#endif
                return Application.persistentDataPath + "/";
            }
        }

        public static string RootUrlStreaming
        {
            get
            {
                if (Application.platform == RuntimePlatform.Android)
                {
                    return RootPathStreaming;
                }
                else
                {
                    return "file:///" + RootPathStreaming;
                }
            }
        }

        public static string RootUrlPersistent
        {
            get
            {
                return "file:///" + RootPathPersistent;
            }
        }

        /** 获取绝对URL
        * path = "Platform/IOS/config.assetbundle"
        * return "file:///xxxx/Platform/IOS/config.assetbundle";
        */
        public static string GetAbsoluteURL(string path)
        {
            //if (persistentAssetFileList.Has(path))
            //{
            //    return RootUrlPersistent + path;
            //}
            //else
            {
                return RootUrlStreaming + path;
            }
        }

        public static string GetAssetBundleAbsoluteURL(string assetBundleName)
        {
#if UNITY_IPHONE
			return GetAbsoluteURL(GEngineDef.StreamingBundlesFolderName + "/" + AssetManager.BuildPlatformName + "/" + assetBundleName).Replace(" ", "%20");
#else
            return GetAbsoluteURL(GEngineDef.StreamingBundlesFolderName + "/" + AssetManager.BuildPlatformName + "/" + assetBundleName);
#endif
        }

        public static string GetAssetRelativePath(string path)
        {
            return ("assets/"+ GEngineDef.ResourcesBuildDir+"/" + path).ToLower(); 
            //return ("assets/bundleresources/" + path).ToLower();
        }

        private void Awake()
        {
            if (Debug.isDebugBuild)
            {
                Debug.LogFormat("================================================================================");
                Debug.LogFormat("AssetManager RootPathStreaming: {0}", RootPathStreaming);
                Debug.LogFormat("AssetManager RootPathPersistent: {0}", RootPathPersistent);
                Debug.LogFormat("AssetManager RootUrlStreaming: {0}", RootUrlStreaming);
                Debug.LogFormat("AssetManager RootUrlPersistent: {0}", RootUrlPersistent);
                Debug.LogFormat("================================================================================");
            }
        }
        

        private static string _unityEditorEditorUserBuildSettingsActiveBuildTarget;
        public static string UnityEditor_EditorUserBuildSettings_activeBuildTarget
        {
            get
            {
                if (Application.isPlaying && !string.IsNullOrEmpty(_unityEditorEditorUserBuildSettingsActiveBuildTarget))
                {
                    return _unityEditorEditorUserBuildSettingsActiveBuildTarget;
                }
                var assemblies = System.AppDomain.CurrentDomain.GetAssemblies();
                foreach (var a in assemblies)
                {
                    if (a.GetName().Name == "UnityEditor")
                    {
                        Type lockType = a.GetType("UnityEditor.EditorUserBuildSettings");
                        //var retObj = lockType.GetMethod(staticMethodName,
                        //    System.Reflection.BindingFlags.NonPublic | System.Reflection.BindingFlags.Static | System.Reflection.BindingFlags.Public)
                        //    .Invoke(null, args);
                        //return retObj;
                        var p = lockType.GetProperty("activeBuildTarget");

                        var em = p.GetGetMethod().Invoke(null, new object[] { }).ToString();
                        _unityEditorEditorUserBuildSettingsActiveBuildTarget = em;
                        return em;
                    }
                }
                return null;
            }
        }

        public static string BuildPlatformName
        {
            get { return GetBuildPlatformName(); }
        } // ex: IOS, Android, AndroidLD

        public static string GetBuildPlatformName()
        {
            string buildPlatformName = "Windows"; // default

            if (Application.isEditor)
            {
                var buildTarget = UnityEditor_EditorUserBuildSettings_activeBuildTarget;
                //UnityEditor.EditorUserBuildSettings.activeBuildTarget;
                switch (buildTarget)
                {
                    case "StandaloneOSXIntel":
                    case "StandaloneOSXIntel64":
                    case "StandaloneOSXUniversal":
                        buildPlatformName = "MacOS";
                        break;
                    case "StandaloneWindows": // UnityEditor.BuildTarget.StandaloneWindows:
                    case "StandaloneWindows64": // UnityEditor.BuildTarget.StandaloneWindows64:
                        buildPlatformName = "Windows";
                        break;
                    case "Android": // UnityEditor.BuildTarget.Android:
                        buildPlatformName = "Android";
                        break;
                    case "iPhone": // UnityEditor.BuildTarget.iPhone:
                    case "iOS":
                        buildPlatformName = "iOS";
                        break;
                    default:
                        Debuger.Assert(false);
                        break;
                }
            }
            else
            {
                switch (Application.platform)
                {
                    case RuntimePlatform.OSXPlayer:
                        buildPlatformName = "MacOS";
                        break;
                    case RuntimePlatform.Android:
                        buildPlatformName = "Android";
                        break;
                    case RuntimePlatform.IPhonePlayer:
                        buildPlatformName = "iOS";
                        break;
                    case RuntimePlatform.WindowsPlayer:
#if !UNITY_5_4_OR_NEWER
                    case RuntimePlatform.WindowsWebPlayer:
#endif
                        buildPlatformName = "Windows";
                        break;
                    default:
                        Debuger.Assert(false);
                        break;
                }
            }

            //if (Quality != KResourceQuality.Sd) // SD no need add
            //    buildPlatformName += Quality.ToString().ToUpper();
            return buildPlatformName;
        }
    }
}
