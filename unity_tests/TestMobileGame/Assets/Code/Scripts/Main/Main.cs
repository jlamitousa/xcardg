using Assets.Code.Apis.Ui;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

namespace Assets.Code.Scrptis.Main
{
    public class Main : MonoBehaviour
    {

        private GraphicInterface gi;

        // Start is called before the first frame update
        void Start()
        {
            GraphicInterface gi = new GraphicInterface(null, null);
        }

        // Update is called once per frame
        void Update()
        {

        }
    }
}
