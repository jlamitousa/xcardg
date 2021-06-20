using System.Collections;
using System.Collections.Generic;
using UnityEngine;

namespace Assets.Code.Scripts.Tests
{
    public class LoadImg : MonoBehaviour
    {
        //private int counter = 0;

        // Start is called before the first frame update
        IEnumerator Start()
        {
            Debug.Log("Start tryin : angel-of-despair.png ..... ");

            Debug.Log(Application.persistentDataPath);

            //WWW www = new WWW("file:///storage/emulated/0/Android/data/com.DefaultCompany.TestMobileGame/files/angel-of-despair.png");
            WWW www = new WWW("file:///C:/Users/Jean-Luc/Documents/travail/xcardg/client/src/main/resources/content/cards/angel-of-despair.png");

            while (!www.isDone)
                yield return null;

            Debug.Log("WWW finish: " + www.error);

            GameObject myAngel = GameObject.Find("AngelOfDespair");



            if (www != null)
            {
                Debug.Log("www=" + www);
                Debug.Log("www.texture=" + www.texture);
            }
            else
            {
                Debug.Log("www=" + www);
            }

            //Debug.Log("myAngel=" + myAngel);

            //Debug.Log("Texture Before=" + (myAngel.GetComponent<Renderer>().material.mainTexture));

            //myAngel.GetComponent<Renderer>().material.mainTexture = www.texture;

            SpriteRenderer renderer = (SpriteRenderer)myAngel.GetComponent<SpriteRenderer>();
            Texture2D currentTexture = renderer.sprite.texture;
            Sprite currSprite = renderer.sprite;
            //Vector2 pivot = new Vector2(www.texture.width / 2f, www.texture.height/2f);
            Vector2 pivot = new Vector2(0.5f, 0.5f);
            //Vector2 pivot = new Vector2(currentTexture.width /2f, currentTexture.height/2f);
            Debug.Log("pivot=" + pivot);
            Sprite NewSprite = Sprite.Create(www.texture, new Rect(0, 0, www.texture.width, www.texture.height), pivot, 100);
            Transform t = myAngel.GetComponent<Transform>();


            Debug.Log("currSprite.rect=" + currSprite.rect);
            Debug.Log("currSprite.pivot=" + currSprite.pivot);
            Debug.Log("t.localScale.x=" + t.localScale.x);
            Debug.Log("renderer.sprite.texture.width=" + renderer.sprite.texture.width);
            Debug.Log("www.texture.width=" + www.texture.width);

            float intermX = ((float)renderer.sprite.texture.width / (float)www.texture.width);
            float intermY = ((float)renderer.sprite.texture.height / (float)www.texture.height);
            float xScale = t.localScale.x * intermX;
            float yScale = t.localScale.y * intermY;

            renderer.sprite = NewSprite;

            Debug.Log("intermX=" + intermX);
            Debug.Log("intermY=" + intermY);
            Debug.Log("transform.localScale.x =" + t.localScale.x);
            Debug.Log("transform.localScale.y =" + t.localScale.y);
            Debug.Log("xScale=" + xScale);
            Debug.Log("yScale=" + yScale);
            Debug.Log("www.texture.width=" + www.texture.width);
            Debug.Log("www.texture.height=" + www.texture.height);
            Debug.Log("transform =" + t);
            Debug.Log("transform.localScale =" + t.localScale);
            Debug.Log("transform.localScale.x =" + t.localScale.x);
            Debug.Log("computed =" + new Vector3(xScale, yScale, 1f));

            t.localScale = new Vector3(xScale, yScale, 1f);

            //RectTransform rt = (RectTransform)myAngel.transform;
            //ushort unit = 39;
            //float width = rt.rect.width;
            //float height = rt.rect.height;

            //renderer.sprite = Sprite.Create(www.texture, new Rect(0, 0, width, height), new Vector2(0.5f, 0.5f), unit);

            Debug.Log("renderer.material.mainTexture(Before)=" + renderer.material.mainTexture);

            //renderer.material.SetTexture("_MainTex", www.texture);

            Debug.Log("renderer.material.mainTexture(After)=" + renderer.material.mainTexture);

            //Debug.Log("Texture After=" + (myAngel.GetComponent<Renderer>().material.mainTexture));

            //Debug.Log("Init a counter");

            //counter = 0;

        }


        // Update is called once per frame
        void Update()
        {
            //Debug.Log("Update start");
            /*
            counter++;

            if (counter % 160 == 0)
            {
                //Debug.Log("Got a log");
            }
             * */
        }
    }
}
