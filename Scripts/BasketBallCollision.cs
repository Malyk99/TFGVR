using UnityEngine;

public class BasketBallCollision : MonoBehaviour
{
    private string ObjectName;
    public AudioSource SoundSource;

    private void Start()
    {
        ObjectName = gameObject.name;
    }

    private void OnTriggerEnter(Collider other)
    {
        if (other.gameObject.CompareTag("Pointer"))
        {
            BasketBallController.Instance.AddPoints();
            SoundSource.PlayOneShot(SoundSource.clip);
            BasketBallController.Instance.ReturnToBallPosition(ObjectName);
        }
    }

    private void OnCollisionEnter(Collision other)
    {
        if (other.gameObject.CompareTag("Floor"))
        {
            BasketBallController.Instance.ReturnToBallPosition(ObjectName);
        }
    }
}
