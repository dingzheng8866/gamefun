using GEngine.Asset;
using GEngine.UI;
using System;
using System.Collections;
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
            return convertToLineStringList(System.Text.Encoding.UTF8.GetString(fileContent));
        }

        public static List<string> convertToLineStringList(string fileContent)
        {
            List<string> list = new List<string>();
            if (fileContent != null && fileContent.Length > 0)
            {
                string[] lines = fileContent.Split('\n');
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

        public static Sprite CreateSprite(object resultObject)
        {
            Sprite sprite = null;
            if (resultObject != null)
            {
                Debug.Log("object type:-->" + resultObject.GetType());
                if (resultObject.GetType() == typeof(Texture2D))
                {
                    Texture2D img = (Texture2D)resultObject;
                    sprite = Sprite.Create(img, new Rect(0f, 0f, img.width, img.height), new Vector2(img.width * 0.5F, img.height * 0.5F));
                    Debug.Log(img.name + "--> " + sprite.name);
                    sprite.name = img.name;
                }
                else if (resultObject.GetType() == typeof(Sprite))
                {
                    sprite = (Sprite)resultObject;
                }
            }
            return sprite;
        }





        private static Dictionary<string, bool> OnGoingAssetPreLoadList = new Dictionary<string, bool>();

        private static List<string> AssetPreLoadList = null;
        private static List<string> AssetBundleLoadMapData = null;

        static IEnumerator LoadAssetMetaDataAsync()
        {
            System.DateTime beginTime = DateTime.Now;

            BytesLoader.Load(AssetManager.GetAssetBundleAbsoluteURL(AssetManager.AssetBundleLoadMapFileName), (loadUrl, obj, arguments) =>
            {
                if (obj != null)
                {
                    AssetBundleLoadMapData = GameUtil.convertToLineStringList((byte[])obj);
                }
            });

            BytesLoader.Load(AssetManager.GetAssetBundleAbsoluteURL(AssetManager.AssetBundlePreLoadFileName), (loadUrl, obj, arguments) =>
            {
                if (obj != null)
                {
                    AssetPreLoadList = GameUtil.convertToLineStringList((byte[])obj);
                }
            });

            while (AssetPreLoadList == null || AssetBundleLoadMapData == null)
            {
                yield return null;
            }

            AssetManager.LogTimeCost("Step load preload meta data files time: ", beginTime);

            foreach (string s in AssetBundleLoadMapData)
            {
                Debug.Log(s);
                string[] args = s.Split(',');
                if (args.Length > 1)
                {
                    AssetManager.Instance.SetAssetLoadBundleInfo(args[0], args[1]);
                }
            }

            PanelManager.openPanel(GEngine.GameConst.UI_Panel_Name_GamePreLoad, GEngine.GameConst.UI_Panel_Path_GamePreLoad);

            Debug.Log("Start to load preload assets");
            foreach (string asset in AssetPreLoadList)
            {
                OnGoingAssetPreLoadList[asset] = true;
                AssetLoader.Load(asset, AssetLoadCallback);
            }

            while (OnGoingAssetPreLoadList.Count > 0)
            {
                yield return null;
            }
            AssetManager.LogTimeCost("Step load preload asset files all time: ", beginTime);
            Debug.Log("Finished preload asset, total: " + AssetPreLoadList.Count);


            //PanelManager.openPanel(GEngine.GameConst.UI_Panel_Name_GameLoad, GEngine.GameConst.UI_Panel_Path_GameLoad);
            PanelManager.openPanel(GEngine.GameConst.UI_Panel_Name_Exception, GEngine.GameConst.UI_Panel_Path_Exception);
            //GameService.Instance.init();
        }

        private static void AssetLoadCallback(string url, object resultObject, object[] arguments = null)
        {
            OnGoingAssetPreLoadList.Remove(url);
            if (resultObject != null)
            {
                Debug.Log(url + "-->" + resultObject.GetType());

                if (resultObject.GetType() == typeof(TextAsset))
                {
                    TextAsset ta = (TextAsset)resultObject;

                    //Debug.Log(ta.text.ToString());

                    if (url.StartsWith("config/"))
                    {
                        if (url.StartsWith("config/locale"))
                        {
                            GEngine.Language.LanguageTextConfParser.Parse(url, ta.text);
                        }
                        else if (url.EndsWith(".csv"))
                        {
                            GEngine.Config.CsvConfigParser.Instance.ParseCsv(url, ta.text);
                        }
                    }

                }
            }
            else
            {
                Debug.LogError("Failed to load asset, result is null: " + url);
            }

        }
    }
}
