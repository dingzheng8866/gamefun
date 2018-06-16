using UnityEngine;
using System.Collections;

namespace GEngine.UI
{

    public class UIScreenInfo
    {
        public float screenWidth;
        public float screenHeight;
        public float sideMargin;
        public float bottomMargin;
        public float scale = -1;
    }

    public static class ScreenUtil
    {

        public static float DEV_WIDTH = 1920f; // 1920
        public static float DEV_HEIGHT = 1080f; //1080

        public static UIScreenInfo uiScreenInfo
        {
            get
            {
                TryRefresh();
                return _uiScreenInfo;
            }
        }

        public static void TryRefresh()
        {
#if UNITY_EDITOR || UNITY_STANDALONE
            RefreshScreen();
#else
		if(_uiScreenInfo.scale == -1)
		{
			RefreshScreen();
		}
#endif
        }

        public static bool IsScreenChange()
        {
            return prevScreenWidth != Screen.width || prevScreenHeight != Screen.height;
        }

        public static void RefreshScreen()
        {
            if (!IsScreenChange())
            {
                return;
            }

            prevScreenWidth = Screen.width;
            prevScreenHeight = Screen.height;

            _uiScreenInfo.screenWidth = Screen.width;
            _uiScreenInfo.screenHeight = Screen.height;

            _uiScreenInfo.sideMargin = 0.0f;
            _uiScreenInfo.bottomMargin = 0.0f;

            if (_uiScreenInfo.screenWidth > 2.1 * _uiScreenInfo.screenHeight) // TODO 暂时这么判定iPhoneX
            {
                _uiScreenInfo.sideMargin = _uiScreenInfo.screenWidth / 2 - _uiScreenInfo.screenHeight;
                _uiScreenInfo.bottomMargin = 24;
                _uiScreenInfo.screenWidth = 2 * _uiScreenInfo.screenHeight;
                _uiScreenInfo.screenHeight -= 24;
                Debug.Log("Device could be iphonex");
            }

            Debug.Log("_uiScreenInfo.screenWidth===========" + _uiScreenInfo.screenWidth);
            Debug.Log("_uiScreenInfo.screenHeight===========" + _uiScreenInfo.screenHeight);

            float _widthScale = (float)_uiScreenInfo.screenWidth / DEV_WIDTH;
            float _heightScale = (float)_uiScreenInfo.screenHeight / DEV_HEIGHT;

            //Debug.Log("_uiScreenInfo._widthScale===========" + _widthScale);
            //Debug.Log("_uiScreenInfo._heightScale===========" + _heightScale);

            _uiScreenInfo.scale = Mathf.Min(_widthScale, _heightScale);

            //Debug.Log("_uiScreenInfo.scale===========" + _uiScreenInfo.scale);
        }

        #region 由于在某些分辨率有特殊处理 获取坐标的偏移值
        public static float GetsOffsetPosX()
        {
            return (Screen.width - ScreenUtil.uiScreenInfo.screenWidth) / 2;
        }

        public static float GetsOffsetPosY()
        {
            return (Screen.height - ScreenUtil.uiScreenInfo.screenHeight) / 2;
        }
        #endregion

        public static Vector2 WorldPosToAnchorPos(this Vector3 pos)
        {
            return WorldPosToAnchorPos(pos, Camera.main);
        }

        public static Vector2 WorldPosToAnchorPos(this Vector3 pos, Camera worldCamera)
        {
            Vector3 pt = worldCamera.WorldToScreenPoint(pos);
            pt.x -= (prevScreenWidth - uiScreenInfo.screenWidth) / 2;
            pt.z = 0;
            pt = pt / uiScreenInfo.scale;
            return pt;
        }

        public static Vector3 AnchorPosToWorldPos(this Vector3 pos)
        {
            return Camera.main.ScreenToWorldPoint(pos * uiScreenInfo.scale);
        }

        private static UIScreenInfo _uiScreenInfo = new UIScreenInfo();

        private static int prevScreenWidth = 0;
        private static int prevScreenHeight = 0;
    }
}