using GEngine.Util;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;

namespace GEngine.Config
{
    public class CsvConfigParser
    {
        private static readonly CsvConfigParser _instance = new CsvConfigParser();
        public static CsvConfigParser Instance { get { return _instance; } }

        private Dictionary<string, AbstractCsvParser> parsers = new Dictionary<string, AbstractCsvParser>();

        private CsvConfigParser()
        {
            parsers["config/item.csv"] = new ItemConfig();
        }


        public void ParseCsv(string fileUrl, string content)
        {
            AbstractCsvParser parser;
            if(parsers.TryGetValue(fileUrl, out parser))
            {
                List<string> list = GameUtil.convertToLineStringList(content);
                if (list.Count > 2)
                {
                    parser.ParseHeader(fileUrl, list[1].Split(';'));
                    for (int i = 2; i < list.Count; i++)
                    {
                        parser.ParseCsv(list[i].Split(';'));
                    }
                }
            }
            else
            {
                Debug.Log("No csv parser to file: " + fileUrl);
            }
        }


    }
}
