using GEngine.Pool;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;

namespace GEngine.Pool
{
    public class DataObjectPool
    {
        /*
        private static DataObjectPool instance = new DataObjectPool();
        public static DataObjectPool Instance
        {
            get
            {
                return instance;
            }
        }

        private DataObjectPool()
        {
        }
        */
        //SimpleObjectPool<ISimpleObjectPoolItem>

        private static Dictionary<Type, AbstractObjectPool> pools = new Dictionary<Type, AbstractObjectPool>();

        //public static void CreatePool(Type t, AbstractObjectPool pool, int size=20)
        //{
        //    pools[t] = pool;
        //    pool.Init(size);
        //}

        public static void DestroyPool<T>()
        {
            AbstractObjectPool pool;
            if (pools.TryGetValue(typeof(T), out pool))
            {
#if UNITY_EDITOR
                Debug.Log("Before destroy data object pool: " + typeof(T) + pool.ToString());
#endif
                pool.Destroy();
            }
            pools.Remove(typeof(T));
        }

        public static void CreatePool<T>(Type t, SimpleObjectPool<T> pool, int size = 20)
        {
            Debug.Log("Create pool: " + typeof(T));
            pools[t] = pool;
            pool.Init(size);
        }

        //pools[typeof(T)] = pool;
        public static T Factory<T> ()
        {
            AbstractObjectPool pool;
            if (!pools.TryGetValue(typeof(T), out pool))
            {
                Debug.LogError("DataObjectPool internal error: not init before: " + typeof(T));
                return default(T);
            }
            SimpleObjectPool<T> op = (SimpleObjectPool<T>)pool;
            return op.Get();
        }

        public static void Back<T>(T obj)
        {
            AbstractObjectPool pool;
            if (!pools.TryGetValue(obj.GetType(), out pool))
            {
                Debug.LogError("DataObjectPool internal error: not init before: " + typeof(T));
                return ;
            }
            SimpleObjectPool<T> op = (SimpleObjectPool<T>)pool;
            op.Put(obj);
        }

    }
}
