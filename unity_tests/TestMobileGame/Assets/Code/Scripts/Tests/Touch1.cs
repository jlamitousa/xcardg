using UnityEngine;
using System.Collections;

namespace Assets.Code.Scripts.Tests
{
    public class Touch1 : MonoBehaviour
    {

        // Use this for initialization
        void Start()
        {

        }

        // Update is called once per frame
        void Update()
        {
            foreach (Touch touch in Input.touches)
            {
                Debug.Log("Touch from Touch1");
            }

            if (Input.GetMouseButtonDown(0))
            {
                Debug.Log("Pressed primary button. Touch1");
                Debug.Log("Mouse is At (" + Input.mousePosition.x + ", " + Input.mousePosition.y + ")");
            }
                

            if (Input.GetMouseButtonDown(1))
                Debug.Log("Pressed secondary button. Touch1");

            if (Input.GetMouseButtonDown(2))
                Debug.Log("Pressed middle click. Touch1");
        }
    }
}