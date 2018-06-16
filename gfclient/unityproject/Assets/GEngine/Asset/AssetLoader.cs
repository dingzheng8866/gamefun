using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;
using GEngine.Util;
using System.Collections;
using Object = UnityEngine.Object;
using System.IO;

namespace GEngine.Asset
{
    public class AssetLoader : AbstractResourceLoader
    {
        public Object Asset
        {
            get { return ResultObject as Object; }
        }

        //private bool IsLoadAssetBundle = false;

        private AssetBundleLoader _bundleLoader;

        public static AssetLoader Load(string path, LoaderDelgate callback=null)
        {
            return AutoNew<AssetLoader>(path, callback);
        }

        protected override void Init(string url, params object[] args)
        {
            base.Init(url, args);
            AssetManager.Instance.StartCoroutine(_Init(Url));
        }

        private IEnumerator _Init(string path)
        {
            Object getAsset = null;

            {
                // 添加扩展名
                string abPath = path;
                if (!abPath.EndsWith(GEngineDef.AssetBundleExt))
                {
                    abPath = abPath + GEngineDef.AssetBundleExt;
                }

                //if(abPath.StartsWith("avatar"))
                {
                    //abPath = "avatar" + GEngineDef.AssetBundleExt;
                }

                if (abPath.IndexOf("kuijiabing_1_1") > 0)
                {
                    abPath = "avatar/soldier/kuijiabing_1_1" + GEngineDef.AssetBundleExt;
                }
                else if (abPath.IndexOf("kuijiabing_1_2") > 0)
                {
                    abPath = "avatar/soldier/kuijiabing_1_2" + GEngineDef.AssetBundleExt;
                }

                _bundleLoader = AssetBundleLoader.Load(abPath);

                while (!_bundleLoader.IsCompleted)
                {
                    yield return null;
                }

                if (!_bundleLoader.IsSuccess)
                {
                    Debug.LogErrorFormat("[AssetLoader]Load BundleLoader Failed(Error) when Finished: {0}", path);
                    //_bundleLoader.Release();
                    OnFinish(null);
                    yield break;
                }

                var assetBundle = _bundleLoader.Bundle;

                DateTime beginTime = DateTime.Now;

                //var abAssetName = Path.GetFileNameWithoutExtension(Url).ToLower();
                var abAssetName = AssetManager.GetAssetRelativePath(path);

                //Debug.Log("Try to load asset name: " + abAssetName);

                foreach (string name in assetBundle.GetAllAssetNames())
                {
                    //Debug.Log("------------------------------------>: " + name);
                }

                if (!assetBundle.isStreamedSceneAssetBundle)
                {
                    AssetBundleRequest request = null;
                    if (abAssetName.EndsWith(".png"))
                    {
                        request = assetBundle.LoadAssetAsync(abAssetName, typeof(Sprite));
                    }
                    else
                    {
                        request = assetBundle.LoadAssetAsync(abAssetName);
                    }

                    //var request = assetBundle.LoadAssetAsync(abAssetName);
                    while (!request.isDone)
                    {
                        yield return null;
                    }
                    Debuger.Assert(getAsset = request.asset);
                    //_bundleLoader.PushLoadedAsset(getAsset); // TODO: finish me
                }
                else
                {
                    Debug.LogError("URL: " + Url + " --> isStreamedSceneAssetBundle");
                    // if it's a scene in asset bundle, did nothing
                    // but set a fault Object the result
                    //getAsset = KResourceModule.Instance;
                }


                ResourceModule.LogLoadTime("AssetLoader: load asset time: ", path, beginTime);

                if (getAsset == null)
                {
                    Debug.LogErrorFormat("Asset is NULL: {0}", path);
                }

            }

            
            if (Application.isEditor && getAsset!=null && getAsset is GameObject)
            {
                RefreshMaterialsShadersForEditorEnv(getAsset as GameObject);
                //if (getAsset != null)
                //    KResoourceLoadedAssetDebugger.Create(getAsset.GetType().Name, Url, getAsset as Object);
            }
            

            if (getAsset != null)
            {
                // 更名~ 注明来源asset bundle 带有类型
                //getAsset.name = String.Format("{0}~{1}", getAsset, Url);
            }
            OnFinish(getAsset);
        }

        public static void RefreshMaterialsShadersForEditorEnv(GameObject go)
        {
            if (Application.isEditor)
            {
                foreach (var r in go.GetComponentsInChildren<Renderer>(true))
                {
                    RefreshMaterialsShaders(r);
                }
            }
        }


        /// <summary>
        /// 编辑器模式下，对指定GameObject刷新一下Material
        /// </summary>
        public static void RefreshMaterialsShaders(Renderer renderer)
        {
            if (renderer.sharedMaterials != null)
            {
                foreach (var mat in renderer.sharedMaterials)
                {
                    if (mat != null && mat.shader != null)
                    {
                        mat.shader = Shader.Find(mat.shader.name);
                    }
                }
            }
        }

        /*
        protected override void DoDispose()
        {
            base.DoDispose();
            _bundleLoader.Release(IsBeenReleaseNow); // 释放Bundle(WebStream)
            //if (IsFinished)
            {
                if (!IsLoadAssetBundle)
                {
                    Resources.UnloadAsset(ResultObject as Object);
                }
                else
                {
                    //Object.DestroyObject(ResultObject as UnityEngine.Object);

                    // Destroying GameObjects immediately is not permitted during physics trigger/contact, animation event callbacks or OnValidate. You must use Destroy instead.
                    //                    Object.DestroyImmediate(ResultObject as Object, true);
                }

                //var bRemove = Caches.Remove(Url);
                //if (!bRemove)
                //{
                //    Log.Warning("[DisposeTheCache]Remove Fail(可能有两个未完成的，同时来到这) : {0}", Url);
                //}
            }
            //else
            //{
            //    // 交给加载后，进行检查并卸载资源
            //    // 可能情况TIPS：两个未完成的！会触发上面两次！
            //}
        }
        */

    }
}
