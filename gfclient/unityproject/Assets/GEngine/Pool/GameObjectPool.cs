//using Games.Module.Wars;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;

namespace GEngine.Pool
{
    public class GameObjectPoolItem : ISimpleObjectPoolItem
    {
        public string prefab = null;
        //public GameObject prefabGO = null;
        public GameObject go = null;
        public SimpleObjectPool<GameObjectPoolItem> pool;

        public PooledEntityMonoBehaviour[] entityBehaviors = null;

        public void Reset()
        {
            if(go!=null)
            {
                if (entityBehaviors!=null)
                {
                    foreach (PooledEntityMonoBehaviour component in entityBehaviors)
                    {
                        component.OnBackToPool();
                    }
                }

                if(go.activeSelf)
                {
                    go.SetActive(false);
                }
            }
        }

        public void Destroy()
        {
            if(go!=null)
            {
                //Debug.Log("About to destroy obj from: " + prefab);
                GameObject.Destroy(go);
            }
            pool = null;
        }

    }

    public class GameObjectPool : MonoBehaviour
    {
        /*
        private static GameObjectPool instance = new GameObjectPool();
        public static GameObjectPool Instance
        {
            get
            {
                return instance;
            }
        }

        private GameObjectPool()
        {
        }
        */

        private static GameObject objectPoolRootNode = null;

        private static GameObjectPool _Instance;
        public static GameObjectPool Instance
        {
            get
            {
                if (_Instance == null)
                {
                    GameObject go = GameObject.Find("GameManagerObjectPool");
                    if (go == null) go = new GameObject("GameManagerObjectPool");
                    GameObject.DontDestroyOnLoad(go);
                    objectPoolRootNode = go;

                    _Instance = go.GetComponent<GameObjectPool>();
                    if (_Instance == null) _Instance = go.AddComponent<GameObjectPool>();
                    _Instance.enabled = true;
                }
                return _Instance;
            }
        }

        private Dictionary<string, SimpleObjectPool<GameObjectPoolItem>> poolGroup = new Dictionary<string, SimpleObjectPool<GameObjectPoolItem>>();


        private Dictionary<string, int> preInitSizeDict = new Dictionary<string, int>();

        private Dictionary<string, string> prefabNameMap = new Dictionary<string, string>();

        public void setPrefabNameMap(string k, string v)
        {
            prefabNameMap[k] = v;
        }

        public void SetPreInitGameObjectSize(string prefab, int size)
        {
            preInitSizeDict[prefab] = size;
            //Debug.Log("SetPreInitGameObjectSize: " + prefab + ", size: " + size);
        }

        public void Destroy(bool async = true)
        {
            //Debug.Log("Destroy GameObjectPool pool: " + async);
            if (async)
            {
                StartCoroutine(DoDestroyTask());
            }
            else
            {
                DoDestroyTaskSync();
            }
        }

        void DoDestroyTaskSync()
        {
            foreach (var pool in poolGroup)
            {
#if UNITY_EDITOR
                //Debug.Log("Before destroy game object pool: " + pool.Key + " --> " + pool.Value.ToString());
#endif
                pool.Value.Destroy();
            }
            poolGroup.Clear();
        }

        IEnumerator DoDestroyTask()
        {
            KeyValuePair<string, SimpleObjectPool<GameObjectPoolItem>>[] poolList = poolGroup.ToArray();
            for (int i = poolList.Length - 1; i >= 0; i--)
            {
#if UNITY_EDITOR
                //Debug.Log("Before destroy game object pool: " + pool.Key + " --> " + pool.Value.ToString());
#endif
                poolList[i].Value.Destroy();
                yield return null;
            }
            poolGroup.Clear();
            yield return null;
        }

        private int GetInitSize(string prefab)
        {
            int size = 1;
            if (!preInitSizeDict.TryGetValue(prefab, out size))
            {
                size = 1; // must set it
            }
            return size;
        }

        public void PreInitPooledGameObjects(GameObject templatePrefabGO, int count = 0)
        {
            PreInitPooledGameObjects(templatePrefabGO.name, templatePrefabGO, count);
        }

        public void PreInitPooledGameObjects(string prefab, GameObject templatePrefabGO = null, int count=0)
        {
            int size = GetInitSize(prefab);
            if (count > 0)
            {
                size = count;
            }
            SimpleObjectPool<GameObjectPoolItem> pool = null;
            if (!poolGroup.TryGetValue(prefab, out pool))
            {
                pool = new SimpleObjectPool<GameObjectPoolItem>();
                poolGroup[prefab] = pool;
            }
            //pool.Init(size);

            int totalNeedInitCount = size - pool.Size();
            //Debug.Log(prefab +" --> need init game object size: " + totalNeedInitCount);

            List<GameObject> list = new List<GameObject>();
            for (int i=0; i< totalNeedInitCount; i++)
            {
                if (templatePrefabGO == null)
                {
                    list.Add(GetPooledGameObject(prefab, false));
                }
                else
                {
                    list.Add(GetPooledGameObject(templatePrefabGO, false));
                }
            }
            foreach(GameObject go in list)
            {
                BackGameObject(go);
            }
            list.Clear();
        }

        public bool BackGameObject(GameObject go, bool forceDestroy = false)
        {
            if (forceDestroy)
            {
                Debug.Log("Pool force destroy: " + go.name);
                GameObject.Destroy(go);
            }
            else
            {
                GameObjectPoolItemTag tag = go.GetComponent<GameObjectPoolItemTag>(); // take much gc?
                if (tag != null && tag.poolItem != null && tag.poolItem.pool != null)
                {
                    //tag.poolItem.go = go;

                    if (objectPoolRootNode != null && tag.gameObject!=null)
                    {
                        tag.gameObject.transform.SetParent(objectPoolRootNode.transform, false);
                    }

                    tag.poolItem.pool.Put(tag.poolItem);
                    //Debug.Log("Returned back : " + go.name + ", "+tag.poolItem.pool.ToString());
                    return true;
                }
                else
                {
                    // destroy it ?
                    //Debug.Log("Destroy gameobject not in pool: " + go.name);
                    GameObject.Destroy(go);
                }
            }

            return false;
        }


        public GameObject GetPooledGameObject(string prefab, bool logEnable=true)
        {
            return FactoryPooledGameObject(prefab, null, logEnable);
        }

        /*
         * prefabGO is ready outside of this scope
         */
        public GameObject GetPooledGameObject(GameObject templatePrefabGO, bool logEnable = true)
        {
            string mappedName = null;
            if (!prefabNameMap.TryGetValue(templatePrefabGO.name, out mappedName))
            {
                mappedName = templatePrefabGO.name;
            }
            return FactoryPooledGameObject(mappedName, templatePrefabGO, logEnable);
        }
        

        private GameObject FactoryPooledGameObject(String prefab, GameObject templatePrefabGO, bool logEnable = true)
        {
            SimpleObjectPool<GameObjectPoolItem> pool = null;
            if (!poolGroup.TryGetValue(prefab, out pool))
            {
                Debug.Log("Please pre init this prefab pool before fighting: " + prefab);
                pool = new SimpleObjectPool<GameObjectPoolItem>();
                poolGroup[prefab] = pool;
            }

            GameObjectPoolItem item = pool.Get(logEnable);
            if (item.go == null)
            {
                if (logEnable)
                {
                    //Debug.Log(DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss.ffff") + " GameObject.Instantiate prefab:-->" + templatePrefabGO.name + " --> " + pool.ToString());
                }

                item.prefab = prefab;

                if (templatePrefabGO == null)
                {
                    //GameObject prefabGO = WarRes.GetPrefab(prefab.ToLower());
                    //item.go = GameObject.Instantiate<GameObject>(prefabGO);
                }
                else
                {
                    item.go = GameObject.Instantiate<GameObject>(templatePrefabGO);
                }
                if(objectPoolRootNode!=null)
                {
                    item.go.transform.SetParent(objectPoolRootNode.transform, false);
                }

                //这句代码会报很多警告，导致游戏帧率变低(开发模式下)
                //GameObject.DontDestroyOnLoad(item.go);

                item.pool = pool;

                item.go.AddComponent<GameObjectPoolItemTag>();
                GameObjectPoolItemTag tag = item.go.GetComponent<GameObjectPoolItemTag>();

                //item.go.SetActive(false); // default

                item.entityBehaviors = item.go.GetComponents<PooledEntityMonoBehaviour>();

                tag.poolItem = item;
            }
            else
            {
                item.go.SetActive(true);
            }

            foreach (PooledEntityMonoBehaviour component in item.entityBehaviors)
            {
                component.OnSpawnFromPool();
            }

            return item.go;
        }


    }
}
