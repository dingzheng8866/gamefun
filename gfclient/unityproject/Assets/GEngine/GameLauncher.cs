using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;
using GEngine.Util;
using System.Collections;
using GEngine.Asset;
using GEngine.Avatar;
using GEngine.UI;

namespace GEngine
{
    public class GameLauncher : MonoBehaviour
    {
        private void Awake()
        {
            GameObject.DontDestroyOnLoad(this);

            //AssetManager am = AssetManager.Instance;

            //AssetBundleResourceLoader.PreLoadManifest();

            //AssetBundleResourceLoader.Load("ui/loadbar.prefab.assetbundle");

            StartCoroutine(Load());

        }

        private IEnumerator Load()
        {
            //yield return null;
            //AssetLoader.Load("builtin/builtin_materials/Sprites-Default.mat", (loadUrl, obj) => { if (obj != null) Debug.Log(loadUrl+"===========>"+obj.GetType()); });

            //builtin\builtin_shaders\defaultresourcesextra
            //AssetLoader.Load("builtin/builtin_shaders/defaultresourcesextra/sprites-default.shader", (loadUrl, obj) => { if (obj != null) Debug.Log(loadUrl + "===========>" + obj.GetType()); });


            //yield return new WaitForSeconds(1.0f);
            //AssetLoader.Load("ui/loadbar.prefab", CallBack1);

            //AssetLoader.Load("config/base.csv", CallBackConfig1);

            //yield return new WaitForSeconds(1.0f);

            AssetLoader.Load("config/locale/app_text_zh_cn.txt", (loadUrl, obj) => {
                if (obj != null)
                {
                    Debug.Log(loadUrl + "===========>" + obj.GetType());
                    TextAsset ta = obj as TextAsset;
                    GEngine.Language.LanguageTextConfParser.Parse(loadUrl, ta.text);
                }
            });

            AssetLoader.Load("config/locale/app_text_en.txt", (loadUrl, obj) => {
                if (obj != null)
                {
                    Debug.Log(loadUrl + "===========>" + obj.GetType());
                    TextAsset ta = obj as TextAsset;
                    GEngine.Language.LanguageTextConfParser.Parse(loadUrl, ta.text);
                }
            });

            AssetLoader.Load("config/locale/app_text_zh_tw.txt", (loadUrl, obj) => {
                if (obj != null)
                {
                    Debug.Log(loadUrl + "===========>" + obj.GetType());
                    TextAsset ta = obj as TextAsset;
                    GEngine.Language.LanguageTextConfParser.Parse(loadUrl, ta.text);
                }
            });

            //AssetLoader.Load("config/avatar/avatar_10001.xml", CallBackConfig1);

            //avatar\soldier\kuijiabing_1_1\walk\1
            System.DateTime time1 = DateTime.Now;
            AvatarLoader.Load(10001);
            AvatarLoader.Load(10001, 2);



            AssetLoader.Load("material/map/map_5.mat", (loadUrl, obj) => {
                if (obj != null)
                {
                    Debug.Log(loadUrl + "===========>" + obj.GetType());
                    GameObject mapGo = GameObject.Find("World/Terrains/Map");
                    if (mapGo != null)
                    {

                        AssetLoader.Load("map/texture/home_city.jpg", (loadUrlMap, objMap) => {
                            if (objMap != null)
                            {
                                Debug.Log(loadUrlMap + "===war_map_1========>" + objMap.GetType());
                                Material mat = obj as Material;
                                mat.mainTexture = objMap as Texture2D;
                                MeshRenderer sr = mapGo.GetComponent<MeshRenderer>();
                                sr.sharedMaterial = mat;


                                AssetLoader.RefreshMaterialsShadersForEditorEnv(mapGo);
                                AssetManager.LogTimeCost("Step map load: ", time1);
                            }

                        });


                    }
                }

            });

            //yield return new WaitForSeconds(1.0f);


            //AssetLoader.Load("prefabs/avatar/warsoldier2.prefab", CallBackTest2);

            /*
            ImageAnimation imgAnimation = new ImageAnimation();
            imgAnimation.AddFrameImage("avatar/soldier/kuijiabing_1_1/walk/1/0000.png");
            imgAnimation.AddFrameImage("avatar/soldier/kuijiabing_1_1/walk/1/0003.png");
            imgAnimation.AddFrameImage("avatar/soldier/kuijiabing_1_1/walk/1/0006.png");
            imgAnimation.AddFrameImage("avatar/soldier/kuijiabing_1_1/walk/1/0009.png");
            imgAnimation.AddFrameImage("avatar/soldier/kuijiabing_1_1/walk/1/0012.png");
            imgAnimation.AddFrameImage("avatar/soldier/kuijiabing_1_1/walk/1/0015.png");
            imgAnimation.AddFrameImage("avatar/soldier/kuijiabing_1_1/walk/1/0018.png");
            imgAnimation.AddFrameImage("avatar/soldier/kuijiabing_1_1/walk/1/0021.png");
            */
            var assetLoader = AssetLoader.Load("prefabs/avatar/warsoldier2.prefab");

            AvatarInfo ai = null;
            while (ai==null || !ai.IsFinishedLoadAssets())
            {
                if(ai==null)
                {
                    ai = AvatarInfoManager.Instance.GetAvatarInfo(AvatarInfo.GetKey(10001, 0));
                }
                yield return null;
            }
            AssetManager.LogTimeCost("Step1: ", time1);


            ai = null;
            while (ai == null || !ai.IsFinishedLoadAssets())
            {
                if (ai == null)
                {
                    ai = AvatarInfoManager.Instance.GetAvatarInfo(AvatarInfo.GetKey(10001, 2));
                }
                yield return null;
            }
            AssetManager.LogTimeCost("Step1-2: ", time1);


            while (!assetLoader.IsCompleted)
                yield return null;


            AssetManager.LogTimeCost("Step2: ", time1);


            GameObject go = assetLoader.Asset as GameObject;
            go.SetActive(false);
            SpriteAnimation sa = go.GetComponentInChildren<SpriteAnimation>();
            sa.frames = ai.GetAvatarActionImageAnimation("walk").animationSprites[0].list;

            CreateSoldier(go, "s1", Vector3.one);
            AssetManager.LogTimeCost("Step3: ", time1);

            CreateSoldier(go, "s2", new Vector3(10, 10, 10));
            AssetManager.LogTimeCost("Step4: ", time1);

            AssetLoader.Load("material/soldier_freeze.mat", (loadUrl, obj) => {
                if (obj != null)
                {
                    Debug.Log(loadUrl + "===========>" + obj.GetType());
                    GameObject soldier = GameObject.Find("Scene/Soldiers/s2");
                    if(soldier!=null)
                    {
                        SpriteRenderer sr = soldier.GetComponentInChildren<SpriteRenderer>();
                        sr.sharedMaterial = obj as Material;

                        AssetLoader.RefreshMaterialsShadersForEditorEnv(soldier);
                        AssetManager.LogTimeCost("Step5: ", time1);
                    }
                }

            });


            AssetLoader.Load("material/soldier_crazy.mat", (loadUrl, obj) => {
                if (obj != null)
                {
                    Debug.Log(loadUrl + "===========>" + obj.GetType());
                    GameObject soldier = GameObject.Find("Scene/Soldiers/s1");
                    if (soldier != null)
                    {
                        SpriteRenderer sr = soldier.GetComponentInChildren<SpriteRenderer>();
                        sr.sharedMaterial = obj as Material;

                        AssetLoader.RefreshMaterialsShadersForEditorEnv(soldier);
                        AssetManager.LogTimeCost("Step6: ", time1);
                    }
                }

            });

            AssetLoader.Load("prefabs/effect3d/e_skill_bingdong.prefab", (loadUrl, obj) => {
                if (obj != null)
                {
                    Debug.Log(loadUrl + "===========>" + obj.GetType());
                    GameObject pg = obj as GameObject;
                    pg.SetActive(false);

                    GameObject goObj = GameObject.Instantiate(obj as GameObject);
                    goObj.SetActive(false);
                    goObj.SetActive(true);
                   
                }

            });

            //yield return new WaitForSeconds(5);
            //GEngine.Language.LanguageTextManager.Instance.SetCurrentLocaleKey(GEngine.Language.LanguageTextManager.LOCALE_EN);

            //yield return new WaitForSeconds(5);
            //GEngine.Language.LanguageTextManager.Instance.SetCurrentLocaleKey(GEngine.Language.LanguageTextManager.LOCALE_ZH_TW);

            /*
            yield return new WaitForSeconds(1.0f);

            var assetLoader2 = AssetLoader.Load("avatar/soldier/kuijiabing_1_1/walk/1/0009.png");

            while (!assetLoader2.IsCompleted)
                yield return null;

            Debug.Log(assetLoader2.Asset.GetType());

            Sprite sp = CreateSprite(assetLoader2.Asset);
            SpriteRenderer sr = win.GetComponentInChildren<SpriteRenderer>();
            if (sr!=null && sp!=null)
            {
                Debug.Log("111111111111111");
                sr.sprite = sp;
            }
            */

        }

        private void CreateSoldier(GameObject go, string name, Vector3 pos)
        {
            GameObject win = GameObject.Instantiate(go);
            win.name = name;

            GameObject p = GameObject.Find("Scene/Soldiers");
            win.transform.position = pos;
            win.transform.SetParent(p.transform, false);
            win.SetActive(true);

            win.GetComponentInChildren<SpriteAnimation>().Play();
        }

        private void CallBackTest2(string url, object resultObject)
        {
            GameObject go = resultObject as GameObject;
            go.SetActive(false);
            GameObject win = GameObject.Instantiate(go);
            win.name = "soldier01";

            GameObject p = GameObject.Find("Scene/Soldiers");
            win.transform.SetParent(p.transform, false);
            win.SetActive(true);
        }

        private void CallBackTest1(string url, object resultObject)
        {
            if (resultObject != null)
            {
                Debug.Log(url + "-->" + resultObject.GetType());
                Sprite sprite = CreateSprite(resultObject);

                if (sprite!=null)
                {
                    GameObject p = GameObject.Find("Scene/Soldiers/soldier01");
                    if (p!=null)
                    {
                        SpriteRenderer render = p.GetComponentInChildren<SpriteRenderer>();
                        if (render!=null)
                        {
                            render.sprite = sprite;
                        }
                    }
                }

            }
        }


        private void CallBackWarSoldier(string url, object resultObject)
        {
            if (resultObject != null)
            {
                Debug.Log(url + "-->" + resultObject.GetType());

                if (resultObject.GetType() == typeof(GameObject))
                {

                    AssetLoader.Load("avatar/soldier/kuijiabing_1_1/walk/1/0000.png",
                        (loadUrl, obj) => {
                            Sprite sprite = CreateSprite(obj);
                            if(sprite!=null)
                            {
                                GameObject goPrefab = (GameObject)resultObject;
                                GameObject go = GameObject.Instantiate(goPrefab);
                                
                                SpriteRenderer render = go.GetComponentInChildren<SpriteRenderer>();
                                if (render==null)
                                {
                                    Transform tf = go.transform.Find("avatar");
                                    if(tf!=null)
                                    {
                                        render = tf.gameObject.AddComponent<SpriteRenderer>();
                                    }
                                }
                                if (render != null)
                                {
                                    render.sprite = sprite;

                                    //Transform tf = go.transform.Find("avatar");

                                    //((SpriteRenderer)tf.gameObject.GetComponent<Renderer>()).sprite = sprite;
                                }
                                GameObject parent = GameObject.Find("Scene/Soldiers");
                                if (parent != null)
                                {
                                    go.transform.SetParent(parent.transform, false);
                                }
                            }
                        });
                }

            }
        }

        private Sprite CreateSprite(object resultObject)
        {
            Sprite sprite = null;
            if (resultObject != null)
            {
                Debug.Log("object type:-->" + resultObject.GetType());
                if (resultObject.GetType() == typeof(Texture2D))
                {
                    Texture2D img = (Texture2D)resultObject;
                    sprite = Sprite.Create(img, new Rect(0f, 0f, img.width, img.height), new Vector2(img.width * 0.5F, img.height * 0.5F));
                    Debug.Log(img.name + "--> " + sprite.name);
                    sprite.name = img.name;
                }
                else if(resultObject.GetType() == typeof(Sprite))
                {
                    sprite = (Sprite)resultObject;
                }
            }
            return sprite;
        }

        private void CallBackCommon1(string url, object resultObject)
        {
            if (resultObject != null)
            {
                Debug.Log(url + "-->" + resultObject.GetType());

                if(resultObject.GetType() == typeof(Texture2D))
                {
                    Texture2D img = (Texture2D)resultObject;
                    Sprite sprite = Sprite.Create(img, new Rect(0f, 0f, img.width, img.height), new Vector2(img.width * 0.5F, img.height * 0.5F));
                    GameObject parent = GameObject.Find("Scene/Soldiers");
                    if (parent != null)
                    {
                        SpriteRenderer sr = parent.AddComponent<SpriteRenderer>();
                        sr.sprite = sprite;
                       
                    }
                }

            }
        }

        private void CallBackConfig1(string url, object resultObject)
        {
            if (resultObject!=null)
            {
                Debug.Log(url+"-->"+resultObject.GetType());
                if(url.EndsWith(".csv") && resultObject.GetType() == typeof(TextAsset))
                {
                    TextAsset ta = (TextAsset)resultObject;
                    
                    Debug.Log(ta.text.ToString());
                }
            }
        }

        
        private void CallBack1(string url, object resultObject)
        {
            if (resultObject!=null)
            {
                GameObject goPrefab = resultObject as GameObject;
                Debug.Log(goPrefab.name);
                GameObject go = GameObject.Instantiate(goPrefab);
                GameObject parent = GameObject.Find("UIRootWindow/UIAutoSizer/bottom");
                if (parent!=null)
                {
                    go.transform.SetParent(parent.transform, false);
                }

                CLoadBar bar = go.GetComponent<CLoadBar>();
                if(bar!=null)
                {
                    bar.SetProgress(0.8f);
                }
            }
        }
        

    }
}
