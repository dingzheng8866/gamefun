using GEngine.Domain;
using GEngine.Util;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;

namespace GEngine.Config
{
    public class ItemConfig : AbstractCsvParser
    {
        public static Dictionary<int, Item> items = new Dictionary<int, Item>(); 


        override public void ParseCsv(string[] csv)
        {
            Item obj = new Item();

            obj.itemId = (ItemId)Convert.ToInt32(getValue(csv, "id"));
            obj.name = getValue(csv, "name");
            obj.avatarId = getValue(csv, "avatarId");

            Debug.Log(obj.ToString());

            items[(int)obj.itemId] = obj;
        }

    }
}
