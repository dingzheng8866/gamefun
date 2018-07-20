﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace GEngine.Config
{
    public abstract class AbstractCsvParser
    {
        private string fileUrl = "";
        private Dictionary<string, int> itemNamesMap = new Dictionary<string, int>();

        public void ParseHeader(string fileUrl, string[] header)
        {
            this.fileUrl = fileUrl;
            for(int i=0; i<header.Length; i++)
            {
                itemNamesMap[header[i]] = i;
            }
        }

        virtual public void ParseCsv(string[] csv)
        {
        }

        protected int getKeyIndex(string key)
        {
            int idx = 0;
            if (itemNamesMap.ContainsKey(key))
            {
                idx = itemNamesMap[key];
            }
            else
            {
                throw new Exception("Not found key: " + key + " in file: " + fileUrl);
            }
            return idx;
        }

        protected string getValue(string[] csv, string key)
        {
            int idx = getKeyIndex(key);
            try
            {
                string value = csv[idx];
            }
            catch (Exception e)
            {
                throw e;
            }
            string v = csv[idx];

            if (v == null || v.Trim().Length < 1)
            {
                v = "";
            }
            else
            {
                v = v.Trim();
            }
            return v;
        }

    }
}