using GEngine.Language;
using GEngine.Util;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;
using UnityEngine.UI;

namespace GEngine.UI.Controller
{
    public class ExceptionPanelController : MonoBehaviour
    {
        public GameObject contentGO = null;

        private void Awake()
        {
            GameUtil.addListenerToCloseFrontendPanel(transform, "bgfullbutton");
        }

        public void SetException(string detail)
        {
            Text text = contentGO.GetComponent<Text>();
            if (text != null)
            {
                text.text = detail;
            }
        }

    }
}
