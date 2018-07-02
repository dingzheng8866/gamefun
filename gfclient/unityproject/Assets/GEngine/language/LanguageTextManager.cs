using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;

namespace GEngine.Language
{
    public class LanguageTextManager
    {
        public const string LOCALE_ZH_CN = "zh_cn";
        //public const string LOCALE_ZH_TW = "zh_tw";
        public const string LOCALE_EN = "en";

        private List<string> supportedLocales = new List<string>();
        private LanguageTextManager()
        {
            LanguageTextLoader instance = LanguageTextLoader.Instance; // make sure to load resources
            //supportedLocales.Add(LOCALE_EN);
            //supportedLocales.Add(LOCALE_ZH_CN);
            //supportedLocales.Add(LOCALE_ZH_TW);
        }


        public static LanguageTextManager Instance = new LanguageTextManager();

        private string currentLocaleKey = LOCALE_ZH_CN;

        private Dictionary<string, Dictionary<string, string>> localeDict = new Dictionary<string, Dictionary<string, string>>();

        public void AddLocaleLanguageConf(string locale, Dictionary<string, string> conf)
        {
            Debug.Log("AddLocaleLanguageConf: " +locale);
            supportedLocales.Add(locale);
            //Debuger.Assert(supportedLocales.Contains(locale));
            localeDict[locale] = conf;
        }


        public void SetCurrentLocaleKey(string locale)
        {
            Debuger.Assert(supportedLocales.Contains(locale));
            currentLocaleKey = locale;
            _currentLocaleTextMap = null;
        }

        private Dictionary<string, string> _currentLocaleTextMap = null;

        private Dictionary<string, string> currentLocaleTextMap
        {
            get
            {
                if(_currentLocaleTextMap==null)
                {
                    if (currentLocaleKey == null || currentLocaleKey.Length < 1)
                    {
                        if (localeDict.Count == 1)
                        {
                            _currentLocaleTextMap = localeDict.ElementAt(0).Value;
                        }
                    }
                    else
                    {
                        localeDict.TryGetValue(currentLocaleKey, out _currentLocaleTextMap);
                    }
                }
                return _currentLocaleTextMap;
            }
        }

        public string GetText(string key)
        {
            string value = string.Empty;

            Dictionary<string, string> map = currentLocaleTextMap;
            if(map!=null)
            {
                map.TryGetValue(key, out value);
            }
            else
            {
                value = string.Empty;
            }
            //value = "抵制不良游戏，拒绝盗版游戏。注意自我保护，谨防受骗上当。\n适度游戏益脑，沉迷游戏伤身。合理安排时间，享受健康生活。";

            return value;
        }

    }
}
