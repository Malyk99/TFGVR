using UnityEngine;

public class ArcheryController : MonoBehaviour
{
    public static ArcheryController Instance { get; private set; }

    public GameObject Player;

    public GameObject SpawnPlatform;

    private bool IsPlaying = false;

    public float TurnTimer;
    public float InitialTurnTimer = 60f;

    public int Points = 0;

    private void Awake()
    {
        if (Instance == null)
        {
            Instance = this;
        }
    }

    void Start()
    {
        TurnTimer = InitialTurnTimer;
    }

    public void StartMinigame()
    {
        Player.transform.position = SpawnPlatform.transform.position;
        IsPlaying = true;
    }

    void Update()
    {
        if (IsPlaying)
        {
            ManageRoundTime();
        }
    }

    public void ManageRoundTime()
    {
        TurnTimer -= Time.deltaTime;
        TurnTimer = Mathf.Max(TurnTimer, 0);

        int minutes = Mathf.FloorToInt(TurnTimer / 60);
        int seconds = Mathf.FloorToInt(TurnTimer % 60);

        // TimerText.text = string.Format("Time Left: {0:00}:{1:00}", minutes, seconds);

        if (TurnTimer <= 0)
        {
            GameManager.Instance.OnTurnEnd(Points);

            Debug.Log("Minigame finished");

            OnTimerEnd();
        }
    }

    public void OnTimerEnd()
    {
        IsPlaying = false;
        Points = 0;
        TurnTimer = InitialTurnTimer;
    }
}
