using GEngine.Domain;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;

namespace GEngine.Config
{
    public class ItemAttrConfig : AbstractCsvParser
    {
        public static Dictionary<int, LevelItem> items = new Dictionary<int, LevelItem>();

        override public void ParseCsv(string[] csv)
        {
            LevelItem obj = new LevelItem();

            obj.itemId = (ItemId)Convert.ToInt32(getValue(csv, "id"));
            obj.level = Convert.ToInt32(getValue(csv, "level"));
            obj.avatarId = getValue(csv, "avatarId");
            obj.unlockCondition = getValue(csv, "unlockCondition");

            int index = getKeyIndex("unlockCondition") + 1;

            for(; index < csv.Length; index++)
            {

            }

            Debug.Log(obj.ToString());

            items[(int)obj.itemId] = obj;
        }

    }
}
