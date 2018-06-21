using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;

namespace GEngine.Util
{
    class CameraSimpleController : MonoBehaviour
    {

        Vector3 lastMousePosition = Vector3.zero;

        bool isDragging = false;

        public float mapWidth = 82;
        public float mapHeight = 66;

        public float halfMapWidth;
        public float halfMapHeight;

        public float limitHeightDelta = 1f;
        public float cameraZoomRange = 10f;
        public float cameraMaxSize = 0;
        public float cameraMinSize = 0;

        public float cameraMoveMaxX = 0;
        public float cameraMoveMaxY = 0;
        public float cameraMoveMinX = 0;
        public float cameraMoveMinY = 0;

        public float cameraMoveRangeX = 0;
        public float cameraMoveRangeY = 0;

        public float cameraMoveSpeed = 0.2f;
        public float cameraZoomSpeed = 0.1f;

        public float cameraZoom = 10;

        public Vector3 cameraPosition = Vector3.zero;

        // Use this for initialization  
        void Start()
        {

            halfMapWidth = mapWidth / 2;
            halfMapHeight = mapHeight / 2;


            float tempHeight = halfMapWidth / Camera.main.aspect;
            if (halfMapHeight > tempHeight)
            {
                halfMapHeight = tempHeight;
            }

            Debug.Log("Half map: " + halfMapWidth + "," + halfMapHeight + ", " + limitHeightDelta);
            halfMapHeight = halfMapHeight - limitHeightDelta;
            halfMapWidth = Camera.main.aspect * halfMapHeight;
            Debug.Log("Half map: " + halfMapWidth + "," + halfMapHeight);

            cameraMaxSize = halfMapHeight;
            cameraMinSize = cameraMaxSize - cameraZoomRange;
            Debug.Log("camera zoom: " + cameraMinSize + "," + cameraMaxSize + ", " + cameraZoomRange);

            cameraZoom = Camera.main.orthographicSize;

            adjustCameraMoveRange();
        }


        private void setCameraSize(float newSize)
        {
            if (newSize > cameraMaxSize)
            {
                newSize = cameraMaxSize;
            }
            else if (newSize < cameraMinSize)
            {
                newSize = cameraMinSize;
            }
            Camera.main.orthographicSize = newSize;
            adjustCameraMoveRange();
        }

        private void adjustCameraMoveRange()
        {
            cameraMoveRangeY = halfMapHeight - Camera.main.orthographicSize;
            cameraMoveRangeX = halfMapWidth - Camera.main.aspect * Camera.main.orthographicSize;
            Debug.Log("camera move range: " + cameraMoveRangeX + "," + cameraMoveRangeY + ", zoom: " + Camera.main.orthographicSize);
        }

        // Update is called once per frame  
        void Update()
        {
            //bool needMoveScene = false;
            if (Input.GetMouseButtonDown(0))
            {
                Debug.Log("GetMouseButtonDown:" + Input.mousePosition + ", " + DateTime.Now);
                isDragging = true;
                lastMousePosition = Input.mousePosition;
            }
            else if (Input.GetMouseButtonUp(0))
            {
                Debug.Log("GetMouseButtonUp:" + Input.mousePosition + ", " + DateTime.Now);
                isDragging = false;
            }
            else if (isDragging)
            {
                Vector3 currentPosition = Input.mousePosition;
                Debug.Log("Dragging:" + Input.mousePosition + ", " + DateTime.Now);

                Vector3 delta = Input.mousePosition - lastMousePosition;
                bool changeFlag1 = changeCameraZoom(delta);
                bool changeFlag2 = changeCameraMovePosition(delta);
                if (changeFlag1 || changeFlag2)
                {
                    lastMousePosition = Input.mousePosition;
                }

            }

        }

        private bool changeCameraZoom(Vector3 delta)
        {
            if (delta.y > 0)
            {
                cameraZoom += delta.y * Time.deltaTime * cameraZoomSpeed;
                setCameraSize(cameraZoom);
                return true;
            }
            return false;
        }

        private bool changeCameraMovePosition(Vector3 delta)
        {
            if (delta.x == 0 && delta.y == 0)
            {
                return false;
            }
            cameraPosition += new Vector3(-delta.x * Time.deltaTime * cameraMoveSpeed, -delta.y * Time.deltaTime * cameraMoveSpeed, 0);

            if (cameraPosition.x > cameraMoveRangeX)
            {
                cameraPosition.x = cameraMoveRangeX;
            }
            if (cameraPosition.x < -cameraMoveRangeX)
            {
                cameraPosition.x = -cameraMoveRangeX;
            }
            if (cameraPosition.y > cameraMoveRangeY)
            {
                cameraPosition.y = cameraMoveRangeY;
            }
            if (cameraPosition.y < -cameraMoveRangeY)
            {
                cameraPosition.y = -cameraMoveRangeY;
            }

            cameraPosition.z = 0;
            Camera.main.transform.localPosition = cameraPosition;
            return true;
        }


    }
}
