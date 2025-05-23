using UnityEngine;

public class GoodTarget : MonoBehaviour
{
    private void OnCollisionEnter(Collision collision)
    {
        if (collision.gameObject.CompareTag("Arrow"))
        {
            ArcheryController.Instance.AddPoints();
            Debug.Log("Good target hit!");
            Destroy(gameObject); 
        }
    }
}
