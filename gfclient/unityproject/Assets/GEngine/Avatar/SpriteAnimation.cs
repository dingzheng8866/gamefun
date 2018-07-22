using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;
using GEngine.Util;
using System.Collections;
using GEngine.Asset;

namespace GEngine.Avatar
{
    public class SpriteAnimation : MonoBehaviour
    {
        public bool loop = true;
        public List<Sprite> frames;

        private SpriteRenderer renderer;

        public bool isPlaying = true;
        

        public static float defaultFrameTime = 0.083333f;

        public float frameTime = defaultFrameTime;
        private int currentFrame = -1;
        private float passTime = 0;

        private bool isComplete = false;


        void Awake()
        {
            renderer = GetComponent<SpriteRenderer>();
        }

        public void Play()
        {
            isPlaying = true;
            isComplete = false;
            currentFrame = -1;
            passTime = frameTime;
        }

        void Update()
        {
            if (frames == null || frames.Count == 0)
            {
                if (renderer!=null && renderer.sprite!=null)
                {
                    renderer.sprite = null;
                }
                return;
            }


            if (isPlaying)
            {
                passTime += Time.deltaTime;

                if (passTime >= frameTime)
                {
                    currentFrame++;
                    passTime = 0;//-= frameTime;

                    if (currentFrame < frames.Count)
                    {
                        renderer.sprite = frames[currentFrame];
                    }
                    else if (loop)
                    {
                        currentFrame = 0;
                        renderer.sprite = frames[0];
                    }
                    else
                    {
                        isPlaying = false;
                        isComplete = true;
                    }
                }
            }
        }

    }
}
