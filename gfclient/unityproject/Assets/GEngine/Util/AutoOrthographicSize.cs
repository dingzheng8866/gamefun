using UnityEngine;
using System.Collections;
using GEngine.UI;

public class AutoOrthographicSize : MonoBehaviour
{
    public enum Mode
    {
        ExpandWidth,
        ExpandHeight,
        ExpandAll,
        ShrinkAll,

    }

    //	public float orthographicSize;
    //	public int pixelWidth;
    //	public int pixelHeight;
    //	public Rect pixelRect;
    //	public Rect rect;
    //	public Vector3 velocity;
    //	
    //	public float newOrthographicSize;
    //	
    //	public int sceneWidth;
    //	public int sceneHeight;
    //	private float width = 9.6f;
    //	private float height;

    public Camera mcamera;
    /** 屏幕的纵横比 */
    private float aspect;
    private float devAspect;

    public float devWidth = 9.6f;
    public float devHeight = 6.4f;
    public Mode mode;
    public float scale = 1f;

    public float INIT_SCALE_VALUE = 6.8f;

    [SerializeField]
    private bool isSetScaleStart = false;

    public static float devToScreenAspect = 1f;

    [SerializeField]
    private float screenAspectMinValue = 1.4f;

    void Start()
    {
        //Debug.LogError(this.gameObject.name);

        if (mcamera == null) mcamera = GetComponent<Camera>();

        if (isSetScaleStart)
        {
            float _devAspect = (float)ScreenUtil.DEV_WIDTH / ScreenUtil.DEV_HEIGHT;
            float _screenAspect = (float)Screen.width / Screen.height;

            devToScreenAspect = (float)(_devAspect / _screenAspect);

            if (devToScreenAspect > 1)
            {
                devToScreenAspect = 1;
            }

            if (_screenAspect < screenAspectMinValue)
            {
                devToScreenAspect = devToScreenAspect / 1.2f;
            }

            scale = (float)devToScreenAspect * INIT_SCALE_VALUE;

            //Debug.LogError("devToScreenAspect=========================================================" + devToScreenAspect);

            //if (_devToScreenAspect < 1)
            //{
            //    scale = scale * 1.03f;
            //}
            //else if (_devToScreenAspect > 1)
            //{
            //    scale = scale * 0.95f;
            //    scale = Mathf.Min(scale, 6f);
            //}
        }

        devAspect = devWidth / devHeight;
    }

    void Update()
    {
        aspect = mcamera.aspect;
        //		orthographicSize = mcamera.orthographicSize;
        //		pixelWidth = mcamera.pixelWidth;
        //		pixelHeight = mcamera.pixelHeight;
        //		pixelRect = mcamera.pixelRect;
        //		rect = mcamera.rect;
        //		velocity = mcamera.velocity;
        //
        //		sceneWidth = Screen.width;
        //		sceneHeight = Screen.height;
        //		
        //		height = orthographicSize * 2f;
        //		width = height * aspect;
        //
        //		
        //		newOrthographicSize = devWidth / (2f * aspect);

        if (mode == Mode.ExpandWidth)
        {
            mcamera.orthographicSize = scale * devWidth / (2f * aspect);
        }
        else if (mode == Mode.ExpandHeight)
        {
            mcamera.orthographicSize = scale * devHeight / 2F;
        }
        else if (mode == Mode.ExpandAll)
        {
            if (devAspect <= aspect)
            {
                mcamera.orthographicSize = scale * devHeight / 2F;
            }
            else
            {
                mcamera.orthographicSize = scale * devWidth / (2f * aspect);
            }
        }
        else if (mode == Mode.ShrinkAll)
        {
            if (devAspect >= aspect)
            {
                mcamera.orthographicSize = scale * devHeight / 2F;
            }
            else
            {
                mcamera.orthographicSize = scale * devWidth / (2f * aspect);
            }
        }


    }
}
