using GEngine.Net;
using GEngine.Net.Proto;
using GEngine.Service;
using GEngine.UI;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.IO;
using GEngine.Asset;
using GEngine.Language;
using GEngine.Util;
using System;

public class TestMain : MonoBehaviour {


    IEnumerator LoadAssetMetaDataAsync()
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
            if(args.Length > 1)
            {
                AssetManager.Instance.SetAssetLoadBundleInfo(args[0], args[1]);
            }
        }

        PanelManager.openPanel(GEngine.GameConst.UI_Panel_Name_GamePreLoad, GEngine.GameConst.UI_Panel_Path_GamePreLoad);

        Debug.Log("Start to load preload assets");
        foreach(string asset in AssetPreLoadList)
        {
            OnGoingAssetPreLoadList[asset] = true;
            AssetLoader.Load(asset, AssetLoadCallback);
        }

        while(OnGoingAssetPreLoadList.Count > 0)
        {
            yield return null;
        }
        AssetManager.LogTimeCost("Step load preload asset files all time: ", beginTime);
        Debug.Log("Finished preload asset, total: " + AssetPreLoadList.Count);


        //PanelManager.openPanel(GEngine.GameConst.UI_Panel_Name_GameLoad, GEngine.GameConst.UI_Panel_Path_GameLoad);
        PanelManager.openPanel(GEngine.GameConst.UI_Panel_Name_Exception, GEngine.GameConst.UI_Panel_Path_Exception);
        //GameService.Instance.init();
    }

    private Dictionary<string, bool> OnGoingAssetPreLoadList = new Dictionary<string, bool>();

    private List<string> AssetPreLoadList = null;
    private List<string> AssetBundleLoadMapData = null;



    // Use this for initialization
    void Start () {
        Debug.Log(AssetManager.GetAssetBundleAbsoluteURL(AssetManager.AssetBundlePreLoadFileName));

        StartCoroutine(LoadAssetMetaDataAsync());
    }


    private void AssetLoadCallback(string url, object resultObject, object[] arguments = null)
    {
        OnGoingAssetPreLoadList.Remove(url);
        if (resultObject != null)
        {
            Debug.Log(url + "-->" + resultObject.GetType());

            if (resultObject.GetType() == typeof(TextAsset))
            {
                TextAsset ta = (TextAsset)resultObject;

                //Debug.Log(ta.text.ToString());

                if(url.StartsWith("config/"))
                {
                    if (url.StartsWith("config/locale"))
                    {
                        GEngine.Language.LanguageTextConfParser.Parse(url, ta.text);
                    }
                    else if(url.EndsWith(".csv"))
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
