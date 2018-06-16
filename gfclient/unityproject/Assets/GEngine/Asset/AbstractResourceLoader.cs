using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;
using GEngine.Util;

namespace GEngine.Asset
{
    public abstract class AbstractResourceLoader
    {
        private static readonly Dictionary<Type, Dictionary<string, AbstractResourceLoader>> _loadersPool = new Dictionary<Type, Dictionary<string, AbstractResourceLoader>>();

        public delegate void LoaderDelgate(string url, object resultObject);

        private readonly List<LoaderDelgate> _afterFinishedCallbacks = new List<LoaderDelgate>();

        public object ResultObject;
        public bool IsCompleted;
        public string Url;

        [System.NonSerialized]
        public float InitTiming = -1;
        [System.NonSerialized]
        public float FinishTiming = -1;

        public float FinishUsedTime
        {
            get
            {
                if (!IsCompleted) return -1;
                return FinishTiming - InitTiming;
            }
        }

        public bool IsSuccess
        {
            get { return ResultObject != null; }
        }

        /// <summary>
        /// 统一的对象工厂
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="url"></param>
        /// <param name="callback"></param>
        /// <param name="forceCreateNew"></param>
        /// <returns></returns>
        protected static T AutoNew<T>(string url, LoaderDelgate callback = null, 
            params object[] initArgs) where T : AbstractResourceLoader, new()
        {
            Dictionary<string, AbstractResourceLoader> typesDict = GetTypeDict(typeof(T));
            AbstractResourceLoader loader;
            if (string.IsNullOrEmpty(url))
            {
                Debug.LogErrorFormat("[{0}:AutoNew]url为空", typeof(T));
                return null;
            }

            if (!typesDict.TryGetValue(url, out loader)) //forceCreateNew || 
            {
                loader = new T();
                //if (!forceCreateNew)
                {
                    typesDict[url] = loader;
                }

                //loader.IsForceNew = forceCreateNew;
                loader.Init(url, initArgs);

                //if (Application.isEditor)
                {
                   // KResourceLoaderDebugger.Create(typeof(T).Name, url, loader);
                }
            }
            else
            {
                //if (loader.RefCount < 0)
                //{
                    //loader.IsDisposed = false;  // 转死回生的可能
                //    Log.Error("Error RefCount!");
                //}
            }

            //loader.RefCount++;

            // RefCount++了，重新激活，在队列中准备清理的Loader
            //if (UnUsesLoaders.ContainsKey(loader))
            //{
            //    UnUsesLoaders.Remove(loader);
            //    loader.Revive();
            //}

            loader.AddCallback(callback);

            return loader as T;
        }

        protected static Dictionary<string, AbstractResourceLoader> GetTypeDict(Type type)
        {
            Dictionary<string, AbstractResourceLoader> typesDict;
            if (!_loadersPool.TryGetValue(type, out typesDict))
            {
                typesDict = _loadersPool[type] = new Dictionary<string, AbstractResourceLoader>();
            }
            return typesDict;
        }


        protected virtual void Init(string url, params object[] args)
        {
            InitTiming = Time.realtimeSinceStartup;
            ResultObject = null;
            IsCompleted = false;

            Url = url.Replace('\\', '/');
        }

        protected virtual void OnFinish(object resultObj)
        {
            Action doFinish = () =>
            {
                // 如果ReadyDispose，无效！不用传入最终结果！
                ResultObject = resultObj;

                // 如果ReadyDisposed, 依然会保存ResultObject, 但在回调时会失败~无回调对象
                //var callbackObject = !IsReadyDisposed ? ResultObject : null;

                FinishTiming = Time.realtimeSinceStartup;
                //Progress = 1;
                //IsError = callbackObject == null;

                IsCompleted = true;

                if (resultObj!=null)
                {
                    //Debug.Log("Load --> " + Url + " --> " + resultObj);
                }

                DoCallback(ResultObject);
            };

            doFinish();
        }


        /// <summary>
        /// 在IsFinisehd后悔执行的回调
        /// </summary>
        /// <param name="callback"></param>
        protected void AddCallback(LoaderDelgate callback)
        {
            if (callback != null)
            {
                if (IsCompleted)
                {
                    if (ResultObject == null)
                        Debug.LogWarningFormat("Null ResultAsset {0}", Url);
                    callback(Url, ResultObject);
                }
                else
                    _afterFinishedCallbacks.Add(callback);
            }
        }

        protected void DoCallback(object resultObj)
        {
            Action justDo = () =>
            {
                foreach (var callback in _afterFinishedCallbacks)
                    callback(Url, resultObj);
                _afterFinishedCallbacks.Clear();
            };

            {
                justDo();
            }
        }

    }
}
