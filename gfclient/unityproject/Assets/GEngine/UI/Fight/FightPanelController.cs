using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using GEngine.Asset;

public class FightPanelController : MonoBehaviour {

    public RectTransform fightHeroContainer = null;
    public GameObject fightHeroButton = null;
	// Use this for initialization
	void Start () {
        StartCoroutine(Test1());
    }

    IEnumerator Test1()
    {
        yield return null;
        yield return new WaitForSeconds(5);
        GameObject g1 = GameObject.Instantiate(fightHeroButton);
        g1.transform.SetParent(fightHeroContainer.transform, false);
        Image img = g1.transform.Find("img").GetComponent<Image>();
        Debug.Log(img.sprite.name);
        AssetLoader.Load("texture/ui/charater/1285.0.png", (loadUrl, obj, arguments) => {
            if (obj != null)
            {
                Debug.Log(loadUrl + "===========>" + obj.GetType());
                Sprite sp = GEngine.Util.GameUtil.CreateSprite(obj);
                img.sprite = sp;
            }
        });
        g1 = GameObject.Instantiate(fightHeroButton);
        g1.transform.SetParent(fightHeroContainer.transform, false);

    }

    // Update is called once per frame
    void Update () {
		
	}
}
