using UnityEngine;
using System.Collections;


namespace GEngine.Util
{
    public class DelayDestory : MonoBehaviour
    {

        public bool ForceDestroyFlag = true;

        public delegate void EventHandler();
        public event EventHandler OnBeforeDestroy;


        public float delayTime = 4.0F;
        private float _time = 0F;
        private float begionTime = 0f;


        void OnEnable()
        {
            begionTime = Time.time;
            _time = begionTime + delayTime;
        }

        void Update()
        {
            if (Time.time > _time)
            {
                //if (!WarPool.instance.Despawn(gameObject))
                {
                    Debug.Log("About to force DelayDestory gameobject：" + gameObject.name);
                    if (OnBeforeDestroy != null) OnBeforeDestroy();
                    Destroy(gameObject);
                }
            }
        }

        public float DelayTime
        {
            get
            {
                return delayTime;
            }

            set
            {
                delayTime = value;
                _time = begionTime + delayTime;
            }
        }
    }
}
