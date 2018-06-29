using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace GEngine.Pool
{
    //public interface IObjectPoolItem
    //{
    //    void Release();
    //}

    public abstract class AbstractObjectPool
    {

        public abstract void Init(int size);
        public abstract int Size();

        public abstract void Destroy();

        //public abstract IObjectPoolItem Get();

        //public abstract void Put(IObjectPoolItem item);

    }
}
