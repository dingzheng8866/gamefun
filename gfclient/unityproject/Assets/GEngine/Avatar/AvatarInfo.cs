using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;
using GEngine.Util;
using System.Collections;
using GEngine.Asset;
//using GEngine.UI;

namespace GEngine.Avatar
{
    public class AvatarInfo
    {
        public int id;
        public string name;
        public int colorId; // used to for different legion for battle

        public string key;

        public AvatarInfo(int id, string name, int colorId)
        {
            this.id = id;
            this.name = name;
            this.colorId = colorId;
            this.key = GetKey(id, colorId);
        }

        public static string GetKey(int avatarId, int colorId)
        {
            return avatarId + "_" + colorId; // TODO: opt
        }

        public Dictionary<string, AvatarActionImageAnimation> actions = new Dictionary<string, AvatarActionImageAnimation>();

        public void SetActionImageAnimation(AvatarActionImageAnimation actionAnimation)
        {
            actions[actionAnimation.action] = actionAnimation;
        }

        public AvatarActionImageAnimation GetAvatarActionImageAnimation(string action)
        {
            AvatarActionImageAnimation animation = null;
            if (!actions.TryGetValue(action, out animation))
            {
                animation = null;
            }
            Debuger.Assert(animation);
            return animation;
        }

        public void LoadAssetResources()
        {
            foreach (var entry in actions)
            {
                entry.Value.LoadAssetResources();
            }
        }

        public bool IsFinishedLoadAssets()
        {
            bool flag = true;
            foreach (var entry in actions)
            {
                if(!entry.Value.finishedLoadSprites)
                {
                    flag = false;
                    break;
                }
            }
            return flag;
        }

    }

    public class AvatarActionImageAnimation
    {
        public string action;
        public List<ImageAnimation> directionAnimations = new List<ImageAnimation>();

        public List<ImageAnimationSprites> animationSprites = new List<ImageAnimationSprites>();
        public bool finishedLoadSprites = false;

        public AvatarActionImageAnimation(string action)
        {
            this.action = action;
        }

        public void AddAvatarActionDirectionAnimation(ImageAnimation animation)
        {
            if (animation!=null)
            {
                directionAnimations.Add(animation);
            }
        }

        public int GetDirectionImageAnimationIndex(float angle)
        {
            //angle += offsetAngle;
            angle %= 360f;
            if (angle < 0) angle += 360f;

            float deltaAngle = 360/directionAnimations.Count;

            int index = deltaAngle == 0 ? 0 : Mathf.RoundToInt(angle / deltaAngle);
            if (index >= directionAnimations.Count)
                index = 0;

            return index;
        }

        public void LoadAssetResources()
        {
            AssetManager.Instance.StartCoroutine(InitAnimationSprites());
        }

        private IEnumerator InitAnimationSprites()
        {
            foreach (ImageAnimation ia in directionAnimations)
            {
                ImageAnimationSprites ias = new ImageAnimationSprites();
                ias.InitSprites(ia);

                while (!ias.finishLoading)
                {
                    yield return null;
                }
                animationSprites.Add(ias);
            }
            finishedLoadSprites = true;
        }

    }

    public class ImageAnimation
    {
        public List<string> frameImages = new List<string>();
        public void AddFrameImage(string img)
        {
            if (img!=null && !frameImages.Contains(img))
            {
                frameImages.Add(img);
            }
        }

    }

    public class ImageAnimationSprites
    {
        public List<Sprite> list = new List<Sprite>();
        private bool hasInit = false;
        public bool finishLoading = false;

        public void InitSprites(ImageAnimation imgAnimation)
        {
            if(!hasInit)
            {
                hasInit = true;
                AssetManager.Instance.StartCoroutine(LoadSprites(imgAnimation));
            }
        }

        private IEnumerator LoadSprites(ImageAnimation imgAnimation)
        {
            foreach (string img in imgAnimation.frameImages)
            {
                yield return LoadSprite(img);
            }
            finishLoading = true;
        }

        private IEnumerator LoadSprite(string img)
        {
            AssetLoader loader = AssetLoader.Load(img);
            while (!loader.IsCompleted)
                yield return null;

            if (loader.Asset!=null)
            {
                list.Add((Sprite)loader.Asset);
            }
        }

    }

}
