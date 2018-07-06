using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using UnityEngine;
using GEngine.Util;
using System.Collections;
using GEngine.Asset;
//using GEngine.UI;

namespace GEngine.Avatar
{
    public class AvatarLoader
    {

        public static string GetAvatarConfigFile(int avatarId, int colorId = 0)
        {
            if (colorId > 0)
            {
                return "config/avatar/avatar_"+avatarId+"_"+colorId+".xml";
            }
            else
            {
                return "config/avatar/avatar_" + avatarId + ".xml";
            }
        }

        public static void Load(int avatarId, int colorId =0)
        {
            string key = AvatarInfo.GetKey(avatarId, colorId);
            if(AvatarInfoManager.Instance.GetAvatarInfo(key) == null)
            {
                string assetFile = GetAvatarConfigFile(avatarId, colorId);
                AssetLoader.Load(assetFile, LoadCallback);
            }
        }


        private static void LoadCallback(string url, object resultObject, object[] arguments = null)
        {
            if (resultObject != null)
            {
                Debug.Log(url + "-->" + resultObject.GetType());
                if (url.EndsWith(".xml") && resultObject.GetType() == typeof(TextAsset))
                {
                    TextAsset ta = (TextAsset)resultObject;

                    Debug.Log(ta.text.ToString());

                    XmlDocument xmlDoc = new XmlDocument();
                    xmlDoc.LoadXml(ta.text.ToString());

                    XmlElement root = xmlDoc.DocumentElement;
                    //Debuger.Assert(root);

                    string idString = root.GetAttribute("id");
                    string name = root.GetAttribute("name");
                    string colorIdString = root.GetAttribute("colorId");
                    int colorId = 0;
                    if(colorIdString!=null && colorIdString.Length >0)
                    {
                        colorId = int.Parse(colorIdString);
                    }

                    AvatarInfo ai = new AvatarInfo(int.Parse(idString), name, colorId);
                    ParseAvatarActions(ai, root);
                    AvatarInfoManager.Instance.AddAvatarInfo(ai);

                    ai.LoadAssetResources();
                }
            }
        }

        private static void ParseAvatarActions(AvatarInfo ai, XmlElement root)
        {
            XmlNodeList list = root.GetElementsByTagName("action");
            foreach (XmlElement node in list)
            {
                AvatarActionImageAnimation aia = ParseAvatarAction(node);
                ai.SetActionImageAnimation(aia);
            }
        }

        private static AvatarActionImageAnimation ParseAvatarAction(XmlElement node)
        {
            string actionName = node.GetAttribute("name");
            AvatarActionImageAnimation animationList = new AvatarActionImageAnimation(actionName);
            XmlNodeList list = node.GetElementsByTagName("animation");
            foreach (XmlElement animationNode in list)
            {
                ImageAnimation ia = ParseAnimation(animationNode);
                animationList.AddAvatarActionDirectionAnimation(ia);
            }
            return animationList;
        }

        private static ImageAnimation ParseAnimation(XmlElement node)
        {
            ImageAnimation ia = new ImageAnimation();
            string path = node.GetAttribute("path");
            XmlNodeList list = node.GetElementsByTagName("img");
            foreach (XmlElement element in list)
            {
                string name = element.GetAttribute("name");
                ia.AddFrameImage(path+name);
            }
            return ia;
        }

    }
}
