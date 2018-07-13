using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;
using GEngine.Util;
using System.Collections;
using GEngine.Asset;
using GEngine.Avatar;
using GEngine.UI;

namespace GEngine.Language
{
    public class LanguageTextLoader : MonoBehaviour
    {
        private static LanguageTextLoader _Instance;
        public static LanguageTextLoader Instance {get { if (_Instance == null) { _Instance = GameUtil.SingletonInstance<LanguageTextLoader>(); } return _Instance; } }

        private void Awake()
        {
            _Instance = this;
            //StartCoroutine(Load());
        }

        public void LoadAppLanguageText(string textFile) //"config/locale/"+ textFile
        {
            AssetLoader.Load(textFile, (loadUrl, obj, arguments) =>
            {
                if (obj != null)
                {
                    Debug.Log(loadUrl + "===========>" + obj.GetType());
                    TextAsset ta = obj as TextAsset;
                    GEngine.Language.LanguageTextConfParser.Parse(loadUrl, ta.text);
                }
            });
        }

        /*
        private IEnumerator Load()
        {
            LoadAppLanguageText("app_text_zh_cn.txt");
            LoadAppLanguageText("app_text_en.txt");
            LoadAppLanguageText("app_text_zh_tw.txt");
            yield return null;
        }
        */
    }
}
