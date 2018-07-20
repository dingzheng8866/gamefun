using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace GEngine.Domain
{
    public class OwnItem
    {
        public ItemId itemId;
        public int level = 1;
        public int value;
        public Dictionary<string, string> props = new Dictionary<string, string>();

        public string getKey()
        {
            return (int)itemId + "-" + level;
        }

        public override string ToString()
        {
            string s = "Item: " + itemId + ", level: " + level +", value: " + value;
            if(props.Count > 0)
            {
                s += ", props:";
            }
            foreach (var prop in props)
            {
                s += " " + prop.Key + "=>" + prop.Value;
            }
            return s;
        }

    }
}
