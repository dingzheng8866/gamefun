using GEngine.Language;
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

        public GameObject closeButtonGO = null;

        private void Awake()
        {
            closeButtonGO.GetComponent<Button>().onClick.AddListener(delegate () {
                PanelManager.closeFrontendPanel();
            });
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
