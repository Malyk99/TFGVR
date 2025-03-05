using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class MoveLeftRight : MonoBehaviour
{
    public float speed = 1.0f;
    public float radius = 1f;

    private Vector3 startPos;


    // Start is called before the first frame update
    void Start()
    {
        //This position is the middle 
        startPos = this.transform.position;
    }

    // Update is called once per frame
    void Update()
    {
        Vector3 endPos = startPos;
        //Sin/Cos goes from 1 to -1
        endPos.x += Mathf.Sin(Time.time * speed) * radius;
        transform.position = endPos;



    }
}

