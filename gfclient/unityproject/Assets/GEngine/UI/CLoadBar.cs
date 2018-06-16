using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;
using UnityEngine.UI;

namespace GEngine.UI
{
    public class CLoadBar : MonoBehaviour
    {
        private Slider totalSlider;
        private Text totalText;

        private float totalProgress;

        public void SetProgress(float v)
        {
            if(v > 1)
            {
                v = 1;
            }
            totalProgress = v;
            Debug.Log("Set " + v);
        }

        void Awake()
        {
            totalSlider = transform.Find("Slider").GetComponent<Slider>();
            totalText = transform.Find("Text").GetComponent<Text>();
        }

        void Update()
        {
            if (totalProgress < totalSlider.value)
            {
                totalSlider.value = totalProgress;
            }
            else if (totalProgress != totalSlider.value)
            {
                totalSlider.value = Mathf.Lerp(totalSlider.value, totalProgress, Time.deltaTime * 10F);
            }

            totalText.text = Mathf.RoundToInt(totalSlider.value * 100) + "%";
        }

    }
}
