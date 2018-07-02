using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;
using UnityEngine.UI;

namespace GEngine.UI.Controller
{
    public class GameLoadPanelController : MonoBehaviour
    {
        public GameObject logoGameObject = null;

        private float loadProgress = 0;
        private bool enterFlag = false;

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

            if(loadProgress>=2 && !enterFlag)
            {
                //iTween.FadeTo(logoGameObject, 1, 2.0f);
                enterFlag = true;
                iTween.FadeTo(logoGameObject, iTween.Hash("delay", 0.0f, "time", 0.3f, "alpha", 0));

                GEngine.Language.LanguageTextManager.Instance.SetCurrentLocaleKey(GEngine.Language.LanguageTextManager.LOCALE_EN);
            }

        }

    }
}
