using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;
using GEngine.Util;
using System.Collections;

namespace GEngine.Asset
{
    public class WWWLoader : AbstractResourceLoader
    {
        // 前几项用于监控器
        private static IEnumerator CachedWWWLoaderMonitorCoroutine; // 专门监控WWW的协程
        private const int MAX_WWW_COUNT = 15; // 同时进行的最大Www加载个数，超过的排队等待
        private static int WWWLoadingCount = 0; // 有多少个WWW正在运作, 有上限的

        private static readonly Stack<WWWLoader> WWWLoadersStack = new Stack<WWWLoader>();
        // WWWLoader的加载是后进先出! 有一个协程全局自我管理. 后来涌入的优先加载！

        public float BeginLoadTime;
        public float FinishLoadTime;
        public WWW Www;


        public static WWWLoader Load(string url, LoaderDelgate callback = null)
        {
            var wwwLoader = AutoNew<WWWLoader>(url, callback);
            return wwwLoader;
        }

        protected override void Init(string url, params object[] args)
        {
            base.Init(url, args);
            WWWLoadersStack.Push(this); // 不执行开始加载，由www监控器协程控制

            if (CachedWWWLoaderMonitorCoroutine == null)
            {
                CachedWWWLoaderMonitorCoroutine = WWWLoaderMonitorCoroutine();
                //Debug.Log("AssetManager.Instance.StartCoroutine(CachedWWWLoaderMonitorCoroutine)");
                AssetManager.Instance.StartCoroutine(CachedWWWLoaderMonitorCoroutine);
            }
        }

        protected void StartLoad()
        {
            AssetManager.Instance.StartCoroutine(CoLoad(Url)); //开启协程加载Assetbundle，执行Callback
        }

        /// <summary>
        /// 协和加载Assetbundle，加载完后执行callback
        /// </summary>
        /// <param name="url">资源的url</param>
        /// <param name="callback"></param>
        /// <param name="callbackArgs"></param>
        /// <returns></returns>
        private IEnumerator CoLoad(string url)
        {
            //Debug.Log("WWW load: "+url);
            System.DateTime beginTime = System.DateTime.Now;

            // 潜规则：不用LoadFromCache~它只能用在.assetBundle
            Www = new WWW(url);
            BeginLoadTime = Time.time;
            WWWLoadingCount++;

            //设置AssetBundle解压缩线程的优先级
            Www.threadPriority = Application.backgroundLoadingPriority; // 取用全局的加载优先速度
            while (!Www.isDone)
            {
                //Progress = Www.progress;
                yield return null;
            }

            yield return Www;
            WWWLoadingCount--;
            //Progress = 1;

            if (!string.IsNullOrEmpty(Www.error))
            {
                if (Application.platform == RuntimePlatform.Android)
                {
                    // TODO: Android下的错误可能是因为文件不存在!
                }

                //string fileProtocol = KResourceModule.GetFileProtocol();
                //if (url.StartsWith(fileProtocol))
                //{
                //    string fileRealPath = url.Replace(fileProtocol, "");
                //    Log.Error("File {0} Exist State: {1}", fileRealPath, System.IO.File.Exists(fileRealPath));
                //}
                Debug.LogErrorFormat("[WWWLoader:Error]{0} {1}", Www.error, url);

                OnFinish(null);
                yield break;
            }
            else
            {
                //KResourceModule.LogLoadTime("WWW", url, beginTime);
                //if (WWWFinishCallback != null)
                //    WWWFinishCallback(url);

                //Desc = string.Format("{0}K", Www.bytes.Length / 1024f);
                OnFinish(Www);
            }

        }

        protected override void OnFinish(object resultObj)
        {
            FinishLoadTime = Time.time;
            base.OnFinish(resultObj);
            //if (Www!=null)
            //{
            //    Www.Dispose();
            //    Www = null;
            //}
        }

        /// <summary>
        /// 监视器协程
        /// 超过最大WWWLoader时，挂起~
        /// 后来的新loader会被优先加载
        /// </summary>
        /// <returns></returns>
        protected static IEnumerator WWWLoaderMonitorCoroutine()
        {
            //yield return new WaitForEndOfFrame(); // 第一次等待本帧结束
            yield return null;

            while (WWWLoadersStack.Count > 0)
            {
                while (WWWLoadingCount >= MAX_WWW_COUNT)
                {
                    yield return null;
                }

                var wwwLoader = WWWLoadersStack.Pop();
                wwwLoader.StartLoad();
            }

            if(CachedWWWLoaderMonitorCoroutine!=null)
            {
                //Debug.Log("AssetManager.Instance.StopCoroutine(CachedWWWLoaderMonitorCoroutine)");
                AssetManager.Instance.StopCoroutine(CachedWWWLoaderMonitorCoroutine);
                CachedWWWLoaderMonitorCoroutine = null;
            }
        }
    }
}
