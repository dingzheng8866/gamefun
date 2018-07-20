using GEngine.Net.Proto;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace GEngine.Domain
{
    public class Role
    {
        private string roleId;

        private Dictionary<string, OwnItem> items = new Dictionary<string, OwnItem>();

        public string GetRoleName()
        {
            return "";
        }




        public static Role toRole(S_RoleData rd)
        {
            Role role = new Role();
            role.roleId = rd.roleId;

            foreach (RoleOwnItem item in rd.item)
            {
                OwnItem oi = new OwnItem();
                oi.itemId = (ItemId)item.itemId;
                oi.level = item.level;
                oi.value = item.value;

                foreach(StringKeyParameter sp in item.parameter)
                {
                    oi.props[sp.key] = sp.value;
                }
            }

            return role;
        }


        public override string ToString()
        {
            string s = "Role: " + roleId;
            foreach (OwnItem item in items.Values)
            {
                s += "\n";
                s += item.ToString();
            }
            return s;
        }



    }
}
