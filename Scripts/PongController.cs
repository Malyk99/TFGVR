using UnityEngine;
using UnityEngine.UI;

public class PongController : MonoBehaviour
{
    public static PongController Instance { get; private set; }

    public Text TimerText, PointText;

    public GameObject Player;

    public GameObject SpawnPlatform;

    public GameObject PongBall;
    public Vector3 StartPongBallPos;

    public GameObject Racket;
    public Vector3 StartPongRacketPos;

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
        StartPongBallPos = PongBall.transform.position;
        StartPongRacketPos = Racket.transform.position;

        Racket.transform.position = StartPongRacketPos;
    }

    void Update()
    {
        if (IsPlaying)
        {
            ManageRoundTime();
        }
    }

    public void StartMinigame()
    {
        Player.transform.position = SpawnPlatform.transform.position;
        IsPlaying = true;

        PongBallController.Instance.SetSpeed(10);

        TurnTimer = InitialTurnTimer;
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

    public void AddPoints()
    {
        if (IsPlaying)
        {
            Points++;
            PointText.text = "Points: " + Points;
        }
    }

    public void SubtractPoints()
    {
        if (IsPlaying)
        {
            Points--;
            PointText.text = "Points: " + Points;
        }
    }

    public void OnTimerEnd()
    {
        IsPlaying = false;
        Points = 0;
        TurnTimer = InitialTurnTimer;

        PongBallController.Instance.SetSpeed(0);

        PongBall.transform.position = StartPongBallPos;
        PointText.text = "Points: 0";
    }
}
