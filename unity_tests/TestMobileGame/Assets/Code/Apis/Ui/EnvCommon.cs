using UnityEngine;
using UnityEditor;

namespace Assets.Code.Apis.Ui
{
    public class EnvCommon : IEnvironnement
    {
        private GameObject gameObject;

        public EnvCommon(GameObject gameObject, IEnvironnement parentEnv)
        {
            this.gameObject = gameObject;
        }
    }
}