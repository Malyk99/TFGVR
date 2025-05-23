using UnityEngine;

public class BadTarget : MonoBehaviour
{
    private void OnCollisionEnter(Collision collision)
    {
        if (collision.gameObject.CompareTag("Arrow"))
        {
            ArcheryController.Instance.SubPoints();
            Debug.Log("Bad target hit!");
            Destroy(gameObject); 
        }
    }
}
