using System.Collections.Generic;
using System.Linq;
using UnityEngine;

public class GameManager : MonoBehaviour
{
    public static GameManager Instance {  get; private set; }

    private Dictionary<int, string> ShuffledMinigames = new();

    private Dictionary<string, int> EndResults = new();

    public GameObject Player;

    public List<int> ScoreList;

    public List<string> PlayerNameList = new();

    public int MaxRounds;
    public int Turns;

    public int TurnCount = 0;
    public int Round;

    private bool SingleLaunch;

    private bool Launched = false;

    private int MaxScore = 0;
    private string MaxPlayer;

    private string Selectedminigame;

    private int P1RoundsWon = 0;
    private int P2RoundsWon = 0;
    private int P3RoundsWon = 0;
    private int P4RoundsWon = 0;
    private int P5RoundsWon = 0;

    private void Awake()
    {
        if (Instance == null)
        {
            Instance = this;
        }
    }

    private void Start()
    {
        Round = -1;
    }

    public void StartPartyGame()
    {
        ScoreList = new List<int>(Turns);
        SetTurns();
        CreatePlayerList();

        SingleLaunch = false;
        Round++;

        ShuffleAndReindex(UiController.Instance.GetMinigameDictionary());
    }

    public void StartSingleMinigame()
    {
        ScoreList = new List<int>(Turns);
        SetTurns();
        CreatePlayerList();

        SingleLaunch = true;

        Debug.Log("Starting single minigame. Round = " + Round);
    }

    public void OnSelectedMinigame()
    {
        UiController.Instance.SetSelectedMinigame(Selectedminigame);
        UiController.Instance.ToSingleMinigameSettingsScreen("MinigameSelectionScreen");
    }

    public void StartMinigame() // Llamado desde el inspector
    {
        string Minigame = UiController.Instance.GetSelectedMinigame();

        if (Minigame == "Basketball")
        {
            BasketBallController.Instance.StartMinigame();
        }

        if (Minigame == "Pong")
        {
            PongController.Instance.StartMinigame();
        }

        if (Minigame == "Archery")
        {
            ArcheryController.Instance.StartMinigame();
        }

        if (Minigame == "Puzzle")
        {
            PuzzleController.Instance.StartMinigame();
        }
    }

    public void StartMinigame(string Minigame) // Llamado desde Continueplaying();
    {
        if (Minigame == "Basketball")
        {
            BasketBallController.Instance.StartMinigame();
        }

        if (Minigame == "Pong")
        {
            PongController.Instance.StartMinigame();
        }

        if (Minigame == "Archery")
        {
            ArcheryController.Instance.StartMinigame();
        }

        if (Minigame == "Puzzle")
        {
            PuzzleController.Instance.StartMinigame();
        }
    }

    public void ContinuePlaying()
    {
        if (TurnCount == Turns)
        {
            UiController.Instance.DisableRoundResultsScreen();
        }

        StartMinigame(ShuffledMinigames[Round]);
    }

    public void OnTurnEnd(int Score)
    {
        ScoreList.Add(Score);

        if (Score > MaxScore) // No considera empates, el primero gana
        {
            MaxScore = Score;
            MaxPlayer = PlayerNameList[TurnCount];
        }

        if (Round == -1)
        {
            EndResults.Add(PlayerNameList[TurnCount], Score);
        }

        TurnCount++;

        if (TurnCount == Turns)
        {
            OnRoundEnd(EndResults, MaxPlayer);
        }
        else
        {
            UiController.Instance.ToSwapVrScreen(PlayerNameList[TurnCount], Launched);

            if (!Launched)
            {
                Launched = true;
            }
        }

        Player.transform.position = Vector3.zero;
    }

    public void OnRoundEnd(Dictionary<string, int> Results, string MVP)
    {
        TurnCount = 0;
        Launched = false;

        Results.OrderBy(pair => pair.Value);

        if (SingleLaunch)
        {
            UiController.Instance.ToEndResultsScreen(Results, Turns);
            OnGeneralGameEnd();
        }
        else
        {
            Round++;

            AddRoundWin(MVP);

            if (Round == MaxRounds)
            {
                OnPartyGameEnd(false);
            }
            else
            {
                UiController.Instance.ToRoundResultsScreen(Results, Turns);
            }
        }
        ScoreList.Clear();
    }

    public void OnPartyGameEnd(bool BackFromExplanation)
    {
        Round = -1;
        ShuffledMinigames.Clear();

        P1RoundsWon = 0;
        P2RoundsWon = 0;
        P3RoundsWon = 0;
        P4RoundsWon = 0;
        P5RoundsWon = 0;

        OnGeneralGameEnd();

        if (!BackFromExplanation)
        {
            UiController.Instance.ToEndResultsScreen(GetWinnerOrderedList(), Turns);
        }
    }

    public void OnGeneralGameEnd()
    {
        PlayerNameList.Clear();
        EndResults.Clear();
    }

    public int GetRound()
    {
        return Round;
    }

    public void SetTurns() // Llamada por los botones de continue de ambas escenas (Settings pre game)
    {
        Turns = UiController.Instance.GetPlayerCount();
        Debug.Log("Turns: " + Turns);
    }

    public void SetMaxRounds(int MaxRounds)
    {
        this.MaxRounds = MaxRounds;
    }

    private void AddRoundWin(string MVP)
    {
        if (MVP == "Player 1")
        {
            P1RoundsWon += 1;
        }
        else if (MVP == "Player 2")
        {
            P2RoundsWon += 1;
        }
        else if (MVP == "Player 3")
        {
            P3RoundsWon += 1;
        }
        else if (MVP == "Player 4")
        {
            P4RoundsWon += 1;
        }
        else if (MVP == "Player 5")
        {
            P5RoundsWon += 1;
        }
    }

    private Dictionary<string, int> GetWinnerOrderedList()
    {
        Dictionary<string, int> orderedPartyresults = new();

        List<int> playerRoundPoints = new();

        playerRoundPoints.Add(P1RoundsWon);
        playerRoundPoints.Add(P2RoundsWon);
        playerRoundPoints.Add(P3RoundsWon);
        playerRoundPoints.Add(P4RoundsWon);
        playerRoundPoints.Add(P5RoundsWon);

        for (int i = 0; i < Turns; i++)
        {
            orderedPartyresults.Add("Player " + (i + 1), playerRoundPoints[i]);
        }

        return orderedPartyresults;
    }

    public void CreatePlayerList()
    {
        for (int i = 1; i <= Turns; i++)
        {
            PlayerNameList.Add("Player " + i);
            Debug.Log("Aded " + PlayerNameList[i - 1]);
        }
        Debug.Log(PlayerNameList);
    }

    public Dictionary<int, string> GetShuffledDictionary()
    {
        return ShuffledMinigames;
    }

    private void ShuffleAndReindex(Dictionary<string, string> MinigameExplanations)
    {
        List<string> keys = new List<string>(MinigameExplanations.Keys);

        System.Random rng = new System.Random();
        int n = keys.Count;
        while (n > 1)
        {
            n--;
            int k = rng.Next(n + 1);
            (keys[n], keys[k]) = (keys[k], keys[n]);
        }

        ShuffledMinigames.Clear();
        for (int i = 0; i < keys.Count; i++)
        {
            ShuffledMinigames[i] = MinigameExplanations[keys[i]];
        }
    }
}
