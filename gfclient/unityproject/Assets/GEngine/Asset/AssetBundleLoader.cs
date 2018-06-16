using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;
using GEngine.Util;
using System.Collections;

namespace GEngine.Asset
{
    public class AssetBundleLoader : AbstractResourceLoader
    {
        private string RelativeResourceUrl;

        public AssetBundle Bundle
        {
            get { return ResultObject as AssetBundle; }
        }

        public static AssetBundleLoader Load(string url, LoaderDelgate callback = null)
        {
            url = url.ToLower();
            LoaderDelgate newCallback = null;
            if (callback != null)
            {
                newCallback = (useUrl, obj) => callback(useUrl, obj as AssetBundle);
            }
            var newLoader = AutoNew<AssetBundleLoader>(url, newCallback);

            return newLoader;
        }

        private static bool _hasPreloadAssetBundleManifest = false;
        private static AssetBundle _mainAssetBundle;
        private static AssetBundleManifest _assetBundleManifest;
        private static bool _assetBundleManifestLoadFailure = false;


        public static void PreLoadManifest()
        {
            if (_hasPreloadAssetBundleManifest)
                return;

            _hasPreloadAssetBundleManifest = true;
            //            var mainAssetBundlePath = string.Format("{0}/{1}/{1}", KResourceModule.BundlesDirName,KResourceModule.BuildPlatformName);
            string abManifestUrl = AssetManager.GetAssetBundleAbsoluteURL(AssetManager.BuildPlatformName);
            //Debug.Log(abManifestUrl);
            BytesLoader bytesLoader = BytesLoader.Load(abManifestUrl, AssetBundleManifestLoadCallback);
   
        }

        private static void AssetBundleManifestLoadCallback(string url, object resultObject)
        {
            Debug.Log("AssetBundleManifestLoadCallback: call back " + (resultObject == null ? "object null":" object ready"));
            if (resultObject!=null)
            {
                _mainAssetBundle = AssetBundle.LoadFromMemory(resultObject as byte[]);
                _assetBundleManifest = _mainAssetBundle.LoadAsset("AssetBundleManifest") as AssetBundleManifest;

                if (_assetBundleManifest == null)
                {
                    _assetBundleManifestLoadFailure = true;
                }

                Debuger.Assert(_mainAssetBundle);
                Debuger.Assert(_assetBundleManifest);

                //Debug.Log("Total ab: " + _assetBundleManifest.GetAllAssetBundles().Length);
                foreach (string ab in _assetBundleManifest.GetAllAssetBundles())
                {
                    //Debug.Log(ab);
                }
            }
            else
            {
                _assetBundleManifestLoadFailure = true;
            }
        }

        protected override void Init(string url, params object[] args)
        {
            RelativeResourceUrl = url;
            Debug.Log("===> Try to load AssetBundle:" + RelativeResourceUrl);

            base.Init(url);
            PreLoadManifest();

            AssetManager.Instance.StartCoroutine(LoadAssetBundle(url));
        }

        private IEnumerator LoadAssetBundle(string relativeUrl)
        {
            while (_assetBundleManifest == null)
            {
                if(_assetBundleManifestLoadFailure)
                {
                    yield break;
                }
                yield return null;
            }

            if (_assetBundleManifestLoadFailure)
            {
                yield break;
            }

            var abPath = relativeUrl.ToLower();
            var deps = _assetBundleManifest.GetAllDependencies(abPath);
            AssetBundleLoader[] _depLoaders = new AssetBundleLoader[deps.Length];
            for (var d = 0; d < deps.Length; d++)
            {
                var dep = deps[d];
                _depLoaders[d] = AssetBundleLoader.Load(dep, null);
            }
            for (var l = 0; l < _depLoaders.Length; l++)
            {
                var loader = _depLoaders[l];
                while (!loader.IsCompleted)
                {
                    yield return null;
                }

                // after bundle load finished, load asset


            }

            relativeUrl = relativeUrl.ToLower();

            var bytesLoader = BytesLoader.Load(AssetManager.GetAssetBundleAbsoluteURL(relativeUrl));
            while (!bytesLoader.IsCompleted)
            {
                yield return null;
            }
            if (!bytesLoader.IsSuccess)
            {
                Debug.LogErrorFormat("[AssetBundleLoader]Error Load AssetBundle: {0}", relativeUrl);
                OnFinish(null);
                yield break;
            }

            byte[] bundleBytes = bytesLoader.Bytes;
            //Progress = 1 / 2f;
            //bytesLoader.Release(); // 字节用完就释放

            AssetBundleParser bundleParser = new AssetBundleParser(RelativeResourceUrl, bundleBytes);
            while (!bundleParser.IsFinished)
            {
                yield return null;
            }

            //Progress = 1f;
            var assetBundle = bundleParser.Bundle;
            if (assetBundle == null)
            {
                Debug.LogErrorFormat("WWW.assetBundle is NULL: {0}", RelativeResourceUrl);
            }
            else
            {
                //yield return LoadAllAssetsAsync(assetBundle);
            }

            OnFinish(assetBundle);

            //Debug.Log("Loaded ab: " + assetBundle.name + ", assets --> " +assetBundle.GetAllAssetNames().Length);
            foreach(string str in assetBundle.GetAllAssetNames())
            {
               // Debug.Log("Loaded ab: " + assetBundle.name + ", asset --> " + str);
            }
            //Array.Clear(cloneBytes, 0, cloneBytes.Length);  // 手工释放内存

            //GC.Collect(0);// 手工释放内存
        }


        private IEnumerator LoadAllAssetsAsync(AssetBundle assetBundle)
        {
            AssetBundleRequest req = assetBundle.LoadAllAssetsAsync();
            while(!req.isDone)
            {
                yield return null;
            }
        }

    }


    public class AssetBundleParser
    {
        public bool IsFinished;
        public AssetBundle Bundle;

        public static Func<string, byte[], byte[]> BundleBytesFilter = null; // 可以放置資源加密函數

        private static int _autoPriority = 1;

        private readonly AssetBundleCreateRequest CreateRequest;

        public float Progress
        {
            get { return CreateRequest.progress; }
        }

        public string RelativePath;
        private readonly float _startTime = 0;

        public AssetBundleParser(string relativePath, byte[] bytes)
        {
            if (Debug.isDebugBuild)
            {
                _startTime = Time.time;
            }

            RelativePath = relativePath;

            var func = BundleBytesFilter ?? DefaultParseAb;
            var abBytes = func(relativePath, bytes);

            CreateRequest = AssetBundle.LoadFromMemoryAsync(abBytes);

            //CreateRequest.priority = _autoPriority++; // 后进先出, 一个一个来
            AssetManager.Instance.StartCoroutine(WaitCreateAssetBundle(CreateRequest));
        }

        private IEnumerator WaitCreateAssetBundle(AssetBundleCreateRequest req)
        {
            while (!req.isDone)
            {
                yield return null;
            }
            OnFinish(req.assetBundle);
        }


        private void OnFinish(AssetBundle bundle)
        {
            IsFinished = true;
            Bundle = bundle;

            if (Application.isEditor)
            {
                const float timeout = 5f;
                if (Time.time - _startTime > timeout)
                {
                    Debug.LogWarningFormat("[CAssetBundlerParser]{0} 解压/读取Asset太久了! 花了{1}秒, 超过 {2}秒", RelativePath,
                        Time.time - _startTime, timeout);
                }
            }
        }


        private static byte[] DefaultParseAb(string relativePath, byte[] bytes)
        {
            return bytes;
        }

    }


}
