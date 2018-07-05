using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;
using UnityEngine.UI;

namespace GEngine.UI.Controller
{
    public class GamePreLoadPanelController : MonoBehaviour
    {
        public GameObject logoGameObject = null;
        public GameObject infoText = null;

        public float loadProgress = 0;
        public bool hasShowLogoAnimation = false;
        public bool hasShowInfoTextAnimation = false;

        private void Start()
        {
            //Image img = logoGameObject.GetComponent<Image>();
            //Debug.Log(img.color.a);
            //iTween.FadeTo(logoGameObject, 0, 2.0f);
            iTween.FadeTo(logoGameObject, iTween.Hash("delay", 0.2f, "time", 0.8f, "alpha", 1));
        }

        private void Update()
        {
            loadProgress += Time.deltaTime;

            //iTween.FadeTo(logoGameObject, iTween.Hash("delay", 0.2f, "time", 0.8f, "alpha", 1));
            //iTween.FadeTo(logoGameObject, iTween.Hash("delay", 0.2f, "time", 1.8f, "alpha", 0, "loopType", iTween.LoopType.none));
            //iTween.FadeFrom(logoGameObject, 0.7f, .5f);

            if(loadProgress>=2 && !hasShowLogoAnimation)
            {
                //iTween.FadeTo(logoGameObject, 1, 2.0f);
                hasShowLogoAnimation = true;
                iTween.FadeTo(logoGameObject, iTween.Hash("delay", 0.0f, "time", 0.3f, "alpha", 0));

                //GEngine.Language.LanguageTextManager.Instance.SetCurrentLocaleKey(GEngine.Language.LanguageTextManager.LOCALE_EN);
            }

            if(hasShowInfoTextAnimation == false)
            {
                Text text = infoText.GetComponent<Text>();
                if (text.text != null && text.text.Length > 0)
                {
                    hasShowInfoTextAnimation = true;
                    text.color = new Color(255, 255, 255, 0.2f);
                }
            }

        }

    }
}
