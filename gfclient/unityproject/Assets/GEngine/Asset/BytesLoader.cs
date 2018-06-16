using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;
using GEngine.Util;
using System.Collections;

namespace GEngine.Asset
{
    public class BytesLoader : AbstractResourceLoader
    {
        public byte[] Bytes { get; private set; }

        public static BytesLoader Load(string path, LoaderDelgate callback=null)
        {
            var newLoader = AutoNew<BytesLoader>(path, callback);
            return newLoader;
        }

        protected override void Init(string url, params object[] args)
        {
            base.Init(url, args);
            //Debug.Log("BytesLoader: url --> " + url);
            AssetManager.Instance.StartCoroutine(CoLoad(url));
        }

        private IEnumerator CoLoad(string url)
        {
            //var getResPathType = ResourceModule.GetResourceFullPath(url, out _fullUrl);

            string _fullUrl = url;

            WWWLoader _wwwLoader = WWWLoader.Load(_fullUrl);
                while (!_wwwLoader.IsCompleted)
                {
                    //Progress = _wwwLoader.Progress;
                    yield return null;
                }

                if (!_wwwLoader.IsSuccess)
                {
                    //if (AssetBundlerLoaderErrorEvent != null)
                    //{
                    //    AssetBundlerLoaderErrorEvent(this);
                    //}
                    Debug.LogErrorFormat("[HotBytesLoader]Error Load WWW: {0}", url);
                    OnFinish(null);
                    yield break;
                }

                Bytes = _wwwLoader.Www.bytes;

            OnFinish(Bytes);

            // TODO: release 
        }

        //protected override void DoDispose()
        //{
        //    base.DoDispose();
        //    if (_wwwLoader != null)
        //    {
        //        _wwwLoader.Release(IsBeenReleaseNow);
        //    }
        //}


    }
}
