﻿using GEngine.Asset;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;

namespace GEngine.UI
{
    public class PanelManager
    {
        public delegate void openPanelCallback(GameObject panelObject, params object[] initArgs);

        public static void CloseAllPopPanel()
        {
            GameObject rootPanel = GameObject.Find("UICanvasContainer/AutoSizer");
            if (rootPanel != null)
            {
                int count = rootPanel.transform.childCount;
                if (count > 1)
                {
                    for(int i=1; i<=count; i++)
                    {
                        Transform ct = rootPanel.transform.GetChild(i);
                        Debug.Log("CloseAllPopPanel: " + ct.name);
                        ct.gameObject.SetActive(false);
                        GameObject.Destroy(ct.gameObject);
                    }
                }
            }

        }

        public static void closePanel(string panelName)
        {
            GameObject rootPanel = GameObject.Find("UICanvasContainer/AutoSizer");
            if(rootPanel!=null)
            {
                Transform tf = rootPanel.transform.Find(panelName);
                if (tf!=null)
                {
                    tf.gameObject.SetActive(false);
                    GameObject.Destroy(tf.gameObject);
                    Debug.Log("closePanel: " + panelName);
                }
            }
            
        }

        public static void closeFrontendPanel()
        {
            GameObject rootPanel = GameObject.Find("UICanvasContainer/AutoSizer");
            if (rootPanel != null)
            {
                int count = rootPanel.transform.childCount;
                if (count > 0)
                {
                    Transform ct = rootPanel.transform.GetChild(count - 1);
                    Debug.Log("closeFrontendPanel: " + ct.name);
                    ct.gameObject.SetActive(false);
                    GameObject.Destroy(ct.gameObject);
                }
            }
        }

        public static void openPanel(string panelName, string panelAssetKey, openPanelCallback callback=null, params object[] initArgs)
        {
            // TODO: check it in pool or not
            AssetLoader.Load(panelAssetKey, (loadUrl, obj, arguments) =>
            {
                if (obj != null)
                {
                    Debug.Log(loadUrl + "===========>" + obj.GetType());
                    GameObject pg = obj as GameObject;
                    pg.SetActive(false);

                    GameObject goObj = GameObject.Instantiate(obj as GameObject);
                    goObj.SetActive(false);
                    goObj.name = panelName;

                    GameObject rootPanel = GameObject.Find("UICanvasContainer/AutoSizer");
                    if(rootPanel!=null)
                    {
                        goObj.transform.SetParent(rootPanel.transform, false);
                        //goObj.transform.SetAsLastSibling();
                    }

                    if(panelName.Equals(GameConst.UI_Panel_Name_GamePreLoad))
                    {
                        goObj.transform.SetAsFirstSibling();
                    }
                    else if (panelName.Equals(GameConst.UI_Panel_Name_GameLoad))
                    {
                        goObj.transform.SetSiblingIndex(2);
                    }
                    else if (panelName.Equals(GameConst.UI_Panel_Name_Exception))
                    {
                        goObj.transform.SetSiblingIndex(999999);
                    }
                    else if (panelName.Equals(GameConst.UI_Panel_Name_Error))
                    {
                        goObj.transform.SetSiblingIndex(999998);
                    }

                    goObj.SetActive(true);
                    Debug.Log("opened panel: " +panelName+" ==> " + panelAssetKey);
                    if(callback!=null)
                    {
                        callback(goObj, arguments);
                    }
                }
            }, initArgs);
        }


    }
}
