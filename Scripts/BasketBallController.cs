using UnityEngine;
using UnityEngine.UI;

public class BasketBallController : MonoBehaviour
{
    public static BasketBallController Instance { get; private set; }

    public Text TimerText, PointText;

    public GameObject Player;

    public GameObject Ball1, Ball2, Ball3;
    public GameObject SpawnPlatform;

    Rigidbody BallRb1, BallRb2, BallRb3;

    public Vector3 OriginalBallPosition1;
    public Vector3 OriginalBallPosition2;
    public Vector3 OriginalBallPosition3;

    public float InitialTurnTimer = 60f;

    public int Points = 0;

    public bool IsPlaying;

    // Timers
    public float TurnTimer;

    private void Awake()
    {
        if (Instance == null)
        {
            Instance = this;
        }
    }

    private void Start()
    {
        OriginalBallPosition1 = Ball1.transform.position;
        OriginalBallPosition2 = Ball2.transform.position;
        OriginalBallPosition3 = Ball3.transform.position;

        BallRb1 = Ball1.GetComponent<Rigidbody>();
        BallRb2 = Ball2.GetComponent<Rigidbody>();
        BallRb3 = Ball3.GetComponent<Rigidbody>();

        TurnTimer = InitialTurnTimer;
    }

    void Update()
    {
        if (IsPlaying)
        {
            ManageRoundTime();
            ManageBallPosition();
        }
    }

    public void StartMinigame()
    {
        Player.transform.position = SpawnPlatform.transform.position;
        IsPlaying = true;
    }

    public void ManageRoundTime()
    {
        TurnTimer -= Time.deltaTime;
        TurnTimer = Mathf.Max(TurnTimer, 0);

        int minutes = Mathf.FloorToInt(TurnTimer / 60);
        int seconds = Mathf.FloorToInt(TurnTimer % 60);

        TimerText.text = string.Format("Time Left: {0:00}:{1:00}", minutes, seconds);

        if (TurnTimer <= 0)
        {
            GameManager.Instance.OnTurnEnd(Points);

            Debug.Log("Minigame finished");

            OnTimerEnd();
        }
    }

    public void ManageBallPosition()
    {
        int threshold = -20;

        if (Ball1.transform.position.y < threshold)
        {
            ReturnToBallPosition("Ball1");
        }

        if (Ball2.transform.position.y < threshold)
        {
            ReturnToBallPosition("Ball2");
        }

        if (Ball3.transform.position.y < threshold)
        {
            ReturnToBallPosition("Ball3");
        }
    }

    public void ReturnToBallPosition(string Ball)
    {
        if (Ball == "Ball1")
        {
            Ball1.transform.position = OriginalBallPosition1;
            BallRb1.linearVelocity = Vector3.zero;
            BallRb1.angularVelocity = Vector3.zero;
        }

        if (Ball == "Ball2")
        {
            Ball2.transform.position = OriginalBallPosition2;
            BallRb2.linearVelocity = Vector3.zero;
            BallRb2.angularVelocity = Vector3.zero;
        }

        if (Ball == "Ball3")
        {
            Ball3.transform.position = OriginalBallPosition3;
            BallRb3.linearVelocity = Vector3.zero;
            BallRb3.angularVelocity = Vector3.zero;
        }
    }

    public void AddPoints()
    {
        if (IsPlaying)
        {
            Debug.Log("Adding Points!");
            Points++;
            PointText.text = "Points: " + Points;
        }
    }

    public void OnTimerEnd()
    {
        IsPlaying = false;
        Points = 0;
        TurnTimer = InitialTurnTimer;
    }
}
