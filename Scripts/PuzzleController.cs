using UnityEngine;
using UnityEngine.UI;

public class PuzzleController : MonoBehaviour
{
    public static PuzzleController Instance { get; private set; }

    public GameObject Player;

    public GameObject SpawnPlatform;

    public AudioSource FitSound;

    public Text TimerText;

    public GameObject po1, po2, po3, po4, po5, po6;
    private Vector3 pt1, pt2, pt3, pt4, pt5, pt6;

    private bool IsPlaying;

    public float TurnTimer;
    public float InitialTurnTimer = 300f;

    public int Points = 0;

    public int CorrectPCount = 0;
    public int SoundCountdown = 12;

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

        pt1 = po1.transform.position;
        pt2 = po2.transform.position;
        pt3 = po3.transform.position;
        pt4 = po4.transform.position;
        pt5 = po5.transform.position;
        pt6 = po6.transform.position;
    }

    private void Update()
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
            Points = (int)Mathf.Floor(TurnTimer);
            GameManager.Instance.OnTurnEnd(Points);

            Debug.Log("Minigame finished");

            OnTimerEnd();
        }
    }

    public void OnTimerEnd()
    {
        IsPlaying = false;
        Points = 0;
        CorrectPCount = 0;
        TurnTimer = InitialTurnTimer;

        ReturnPiecePosition();
    }

    public void AddCorrectPiece()
    {
        if (CorrectPCount < 12)
        {
            CorrectPCount++;
            SoundCountdown--;
        }

        if (SoundCountdown % 2 == 0)
        {
            FitSound.PlayOneShot(FitSound.clip);
        }

        if (CorrectPCount == 12)
        {
            TurnTimer = 0;
        }
    }

    public void SubCorrectPiece()
    {
        if (CorrectPCount > 0)
        {
            CorrectPCount--;
            SoundCountdown++;
        }
    }

    public void ReturnPiecePosition()
    {
        po1.transform.position = pt1;
        po2.transform.position = pt2;
        po3.transform.position = pt3;
        po4.transform.position = pt4;
        po5.transform.position = pt5;
        po6.transform.position = pt6;
    }
}
