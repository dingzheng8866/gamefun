using System.Collections;
using System.Collections.Generic;
using UnityEngine;

namespace GEngine.UI
{ 
    public class UIAutoSizer : MonoBehaviour
    {
        // Use this for initialization
        void Start()
        {
            SetScaleValue();
        }

        private void SetScaleValue()
        {
            UIScreenInfo uiScreenInfo = ScreenUtil.uiScreenInfo;
            RectTransform rectTransform = GetComponent<RectTransform>();

            rectTransform.sizeDelta = new Vector2(uiScreenInfo.screenWidth / uiScreenInfo.scale, uiScreenInfo.screenHeight / uiScreenInfo.scale);
            rectTransform.localScale = new Vector3(uiScreenInfo.scale, uiScreenInfo.scale, uiScreenInfo.scale);

            //canvasScaler.referenceResolution = new Vector2(screenWidth * _value, screenHeight * _value);
        }

        // Update is called once per frame
        void Update()
        {
            if (Application.isEditor && ScreenUtil.IsScreenChange())
            {
                Debug.Log("UIAutoSizer SetScaleValue");
                SetScaleValue();
            }
        }
    }
}