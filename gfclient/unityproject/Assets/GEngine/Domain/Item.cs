using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace GEngine.Domain
{
    public class Item
    {
        public ItemId itemId;
        public String name;
        public String avatarId;
        public int level = 1;
        public string description;
        public bool isAccumulative = true;

        //public ItemCategory category = ItemCategory.Unknown;

        public Dictionary<string, string> props = new Dictionary<string, string>();

        public override string ToString()
        {
            return "item: " + itemId + ", name:" + name +", avatarId:" + avatarId + ", desc:" + description;
        }

    }

    public class LevelItem : Item
    {
        public string unlockCondition = "";
    }
}
