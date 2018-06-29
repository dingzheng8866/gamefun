using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace GEngine.Pool
{
    public interface ISimpleObjectPoolItem
    {
        void Reset();
        void Destroy();
    }

    public class SimpleObjectPool<T> : AbstractObjectPool //where T : ISimpleObjectPoolItem
    {
        private readonly object sync = new object();

        private int totalCreate = 0;
        private int totalBack = 0;
        private int totalGet = 0;

        private Stack<T> pool;
        private Func<T> factory;

        private List<T> createdAuditList;

        public SimpleObjectPool()
        {
            pool = new Stack<T>();
            createdAuditList = new List<T>();
        }

        public override void Init(int size)
        {
            lock (this)
            {
                for (int i = 0; i < size; i++)
                {
                    T t = CreateObj();
                    pool.Push(t);
                }
                
            }
        }

        public override void Destroy()
        {
            lock(this)
            {
                foreach (T t in createdAuditList) // pool
                {
                    if (typeof(ISimpleObjectPoolItem).IsAssignableFrom(typeof(T)))
                    {
                        ((ISimpleObjectPoolItem)t).Destroy();
                    }
                }

                createdAuditList.Clear();
                pool.Clear();
            }
        }
        //TODO:这里的东西，createdAuditList 与 pool 为何总是没有对应的关系呢？
        public T Get(bool logEnable=false)
        {
            lock (this)
            {
                totalGet++;
                if (pool.Count > 0)
                {
                    return pool.Pop();
                }
                else
                {
                    //t.Pool = this;
                    return CreateObj(logEnable);
                }
            }
        }

        private T CreateObj(bool logEnable = false)
        {
            T t = default(T);
            if (logEnable)
            {
                //UnityEngine.Debug.Log(DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss.ffff") + " SimpleObjectPool: CreateObj-->" + ToString());
            }
            
            if (factory == null)
            {
                t = Activator.CreateInstance<T>();
            }
            else
            {
                t = factory();
            }

            createdAuditList.Add(t); // audit this, used for destroy to avoid memory leak

            totalCreate++;
            return t;
        }

        //public int GetTotalCreateNumber()
        //{
            //return totalCreate;
        //}

        public void Put(T item)
        {
            lock (this)
            {
                if(typeof(ISimpleObjectPoolItem).IsAssignableFrom(typeof(T)))
                {
                    ((ISimpleObjectPoolItem) item).Reset();
                }
                
                pool.Push(item);
                totalBack++;
                //UnityEngine.Debug.Log("SimpleObjectPool Put: " + (typeof(T)) + ", pool size: " + Size() + ", total create: " + totalCreate);
            }
        }

        public override String ToString()
        {
            return "Pool "+typeof(T)+" size: " + Size() + ", total create: " + totalCreate +", total get: " + totalGet + ", total back: " + totalBack;
        }

        public override int Size()
        {
            lock (this)
            {
                return pool.Count;
            }
        }

        public Func<T> Factory
        {
            get
            {
                return factory;
            }
            set
            {
                factory = value;
            }
        }

    }
}
