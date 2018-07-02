using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.EventSystems;

namespace GEngine.Language
{
    public class LanguageText : UIBehaviour
    {

        public string languageKey = "";

        private Text _text = null;

        private Text text
        {
            get
            {
                if(_text==null)
                {
                    _text = GetComponent<Text>();
                }
                return _text;
            }
        }

        protected override void OnEnable()
        {
            //base.OnEnable();
            //Debug.Log("------------------------------dfadfafddsf");
        }

        private void Update()
        {
            if (languageKey!=null && languageKey.Length >0 && text!=null)
            {
                string value = LanguageTextManager.Instance.GetText(languageKey);
                if(!string.IsNullOrEmpty(value) && text.text != value)
                {
                    text.text = value;
                }
            }

        }

    }
}
