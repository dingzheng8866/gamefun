using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;

namespace GEngine.Language
{
    public class LanguageTextConfParser
    {

        public static void Parse(string url, string content)
        {
            if(content!=null && content.Length >0)
            {
                string[] lines = content.Split('\n');
                Dictionary<string, string> dict = new Dictionary<string, string>();
                foreach(string line in lines)
                {
                    Debug.Log("------------------------------->"+line);
                    string[] strs = line.Split('=');
                    if(strs.Length==2)
                    {
                        dict[strs[0].Trim()] = strs[1].Replace('\r',' ').Trim();
                    }
                }

                string locale = null;
                if (!dict.TryGetValue("Locale", out locale))
                {
                    Debug.LogError("Not found Locale config in file: " + url);
                    return;
                }
                LanguageTextManager.Instance.AddLocaleLanguageConf(locale, dict);
            }
           

        }

    }
}
