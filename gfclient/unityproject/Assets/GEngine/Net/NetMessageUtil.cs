using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using GEngine.Net.Proto;
using GEngine.Util;

namespace GEngine.Net
{
    public class NetMessageUtil
    {

        public static C_RoleLogin buildReqOfficalC_RoleLogin()
        {
            return buildReqC_RoleLogin(GameUtil.GetGameLoginDeviceID(), GameUtil.GetDeviceID(), GameUtil.GetOS(), 0,"","",GameUtil.GetSystemInfo(), "");
        }

        public static C_RoleLogin buildReqC_RoleLogin(string loginAcctId, string deviceId, string platform, int channel, string acct, string token, string deviceInfo, string sdkInfo)
        {
            C_RoleLogin req = new C_RoleLogin();
            req.channel = channel;
            req.device_id = deviceId;
            req.device_info = deviceInfo;
            req.login_account_id = loginAcctId;
            req.platform = platform;
            req.sdk_info = sdkInfo;
            req.account = acct;
            req.token = token;
            return req;
        }

    }
}
