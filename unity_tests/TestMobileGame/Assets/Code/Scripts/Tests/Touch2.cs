using UnityEngine;
using System.Collections;

namespace Assets.Code.Scripts.Tests
{
    public class Touch2 : MonoBehaviour
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
                Debug.Log("Touch from Touch2");
            }

            if (Input.GetMouseButtonDown(0))
                Debug.Log("Pressed primary button. Touch2");

            if (Input.GetMouseButtonDown(1))
                Debug.Log("Pressed secondary button. Touch2");

            if (Input.GetMouseButtonDown(2))
                Debug.Log("Pressed middle click. Touch2");
        }
    }
}