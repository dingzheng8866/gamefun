using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace GEngine.Net
{
    public class NetConst
    {
        public static bool IsPrintLog = true;

        public static int ReceiveBufferSize = 1000000;
        public static int SendBufferSize = 65535;

        public static int NetConnectTimeOut = 5;
        public static int NetHeartBeatInterval = 2;

    }
}
