//using CC.Runtime;
//using Games.Module.Wars;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;

namespace GEngine.Pool
{

    /*
     * To the game object in the pool, each behavior script may hold each state,
     * when put it in the pool to reuse it, must reset the state.
     */
    public class PooledEntityMonoBehaviour : MonoBehaviour
    {
        //---------------------------------------------- Start: Need to overwrite methods if needed
        // manager component quick access relationship
        protected virtual void OnAwake()
        {
            //enableUpdate = false;
        }

        // reset behavior stateful info, when reuse it from pool
        protected virtual void OnReset()
        {
            //OnRelease();
        }

        // will be invoked before OnUpdate
        protected virtual void OnInit()
        {
        }

        protected virtual void OnUpdate()
        {
        }

        // Destory any resource, note: to be invoked when pool destroy or GameObject.Destroy
        protected virtual void OnRelease()
        {
            hasInit = false;
        }
        //----------------------------------------------   End: Need to overwrite methods if needed

        
        public bool enableUpdate = true; // when war start, mark it true, war end, mark it false
        private bool hasInit = false;

        protected virtual void Awake()
        {
            OnAwake();
        }

        protected virtual void Start()
        {
            OnStart();
        }

        protected virtual void Update()
        {
            if (isEnableUpdate())
            {
                if (!hasInit)
                {
                    hasInit = true;
                    OnInit();
                }
                OnUpdate();
            }
        }

        private bool isEnableUpdate()
        {
            return enableUpdate;
        }

        protected virtual void OnStart()
        {
        }

        public void OnSpawnFromPool()
        {
            enableUpdate = true;
            //OnReset();
        }

        public void OnBackToPool()
        {
            enableUpdate = false;
            OnReset();
        }

        protected virtual void OnEnable() // active true
        {
        }

        protected virtual void OnDisable() // active false
        {
        }

    }
}
