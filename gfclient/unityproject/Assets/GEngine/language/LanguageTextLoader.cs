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
            StartCoroutine(Load());
        }

        private void loadAppLanguageText(string textFile)
        {
            AssetLoader.Load("config/locale/"+ textFile, (loadUrl, obj, arguments) =>
            {
                if (obj != null)
                {
                    Debug.Log(loadUrl + "===========>" + obj.GetType());
                    TextAsset ta = obj as TextAsset;
                    GEngine.Language.LanguageTextConfParser.Parse(loadUrl, ta.text);
                }
            });
        }

        private IEnumerator Load()
        {
            loadAppLanguageText("app_text_zh_cn.txt");
            loadAppLanguageText("app_text_en.txt");
            loadAppLanguageText("app_text_zh_tw.txt");
            yield return null;
        }
    }
}
