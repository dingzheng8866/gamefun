using GEngine.Pool;
using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Net.Sockets;

namespace GEngine.Net
{
    public enum ReadState
    {
        Header,
        Content,
    }

    public class NetRawData : ISimpleObjectPoolItem
    {
        public ReadState state = ReadState.Header;
        public int socketId = 0; // internal client unique id

        public int msgNameLen;
        public int total;   //total bytes to read
        public int read;    //already read
        public byte[] buffer;
        public MemoryStream stream = new MemoryStream();
        public string msgName = null;


        public NetRawData()
        {
            state = ReadState.Header;
            buffer = new byte[NetConst.SendBufferSize / 2];
            Reset();
        }

        public void Destroy()
        {
            Reset();
            buffer = null;
        }

        public void Reset()
        {
            state = ReadState.Header;
            socketId = 0;
            total = 8;
            read = 0;
            msgNameLen = 0;
            msgName = null;
            stream.SetLength(0);
        }

    }
}
