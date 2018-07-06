using GEngine.Language;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;
using UnityEngine.UI;

namespace GEngine.UI.Controller
{
    public class ErrorPanelController : MonoBehaviour
    {
        public GameObject titleGO = null;
        public GameObject descGO = null;
        public GameObject buttonGO = null;

        public int titleId = 0;
        public int descId = 0;
        public int buttonId = 0;

        private LanguageText ltTitle;
        private LanguageText ltDesc;
        private LanguageText ltButton;

        private void Awake()
        {
            ltTitle = titleGO.GetComponent<LanguageText>();
            ltDesc = descGO.GetComponent<LanguageText>();
            ltButton = buttonGO.GetComponent<LanguageText>();

            transform.Find("bg").gameObject.GetComponent<Button>().onClick.AddListener(delegate () {
                //this.gameObject.SetActive(false);
                Debug.Log("PanelManager.closeFrontendPanel 33");
                PanelManager.closeFrontendPanel();
            });

            transform.Find("content/button").gameObject.GetComponent<Button>().onClick.AddListener(delegate () {
                //this.gameObject.SetActive(false);
                Debug.Log("PanelManager.closeFrontendPanel 22");
                PanelManager.closeFrontendPanel();
            });

        }

        private void Update()
        {
            if(titleId > 0)
            {
                string key = "error_title_" + titleId;
                if (ltTitle != null && !ltTitle.languageKey.Equals(key))
                {
                    ltTitle.languageKey = key;
                }
            }

            if (descId > 0)
            {
                string key = "error_desc_" + descId;
                if (ltDesc != null && !ltDesc.languageKey.Equals(key))
                {
                    ltDesc.languageKey = key;
                }
            }

            if (buttonId > 0)
            {
                string key = "button_id_" + buttonId;
                if (ltButton != null && !ltButton.languageKey.Equals(key))
                {
                    ltButton.languageKey = key;
                }
            }
        }

    }
}
