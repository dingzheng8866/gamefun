using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;

namespace GEngine.Util
{
    public class SingletonHelper
    {

        public static T SingletonInstance<T>() where T : MonoBehaviour
        {
            T instance = null;

            string name = typeof(T).Name;
            GameObject go = GameObject.Find(name);
            if (go == null)
            {
                go = new GameObject(name);
                GameObject.DontDestroyOnLoad(go);
            }

            instance = go.GetComponent<T>();
            if (instance == null) instance = go.AddComponent<T>();
            //instance.enabled = true;

            return instance;
        }
    }
}
