using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;
using GEngine.Util;
using System.Collections;
using GEngine.Asset;
//using GEngine.UI;

namespace GEngine.Avatar
{
    public class AvatarInfoManager
    {
        public static AvatarInfoManager Instance = new AvatarInfoManager();

        private Dictionary<string, AvatarInfo> avatarInfoDict = new Dictionary<string, AvatarInfo>();

        public void AddAvatarInfo(AvatarInfo ai)
        {
            if(ai!=null)
            {
                avatarInfoDict[ai.key] = ai;
                Debug.Log("[AvatarInfoManager] --> Add: " + ai.id + "-->" + ai.colorId);
            }
        }

        public AvatarInfo GetAvatarInfo(string avatarKey)
        {
            AvatarInfo ai = null;
            if(!avatarInfoDict.TryGetValue(avatarKey, out ai))
            {
                ai = null;
            }
            return ai;
        }

    }
}
