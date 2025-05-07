using UnityEngine;

public class PongBallController : MonoBehaviour
{
    public static PongBallController Instance;

    [SerializeField] private float Speed = 0;
    private Vector3 moveDirection;

    private void Awake()
    {
        if (Instance == null)
        {
            Instance = this;
        }
    }

    private void Start()
    {
        moveDirection = new Vector3(0, 0, 1).normalized;
    }

    private void Update()
    {
        transform.position += moveDirection * Speed * Time.deltaTime;
    }

    private void OnCollisionEnter(Collision collision)
    {
        Vector3 reflectDir = Vector3.Reflect(moveDirection, collision.contacts[0].normal);
        reflectDir.y = 0;
        moveDirection = reflectDir.normalized;

        if (collision.gameObject.CompareTag("PongPointer"))
        {
            PongController.Instance.AddPoints();
        }

        if (collision.gameObject.CompareTag("PongMinusPointer"))
        {
            PongController.Instance.SubtractPoints();
        }
    }

    public void SetDirection(Vector3 direction)
    {
        direction.y = 0;
        moveDirection = direction.normalized;
    }

    public void SetSpeed(float speed)
    {
        Speed = speed;
    }
}
