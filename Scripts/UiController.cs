using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;
using UnityEngine.UI;

public class UiController : MonoBehaviour
{
    public static UiController Instance { get; private set; }

    public GameObject MainScreen, SettingsScreen, GameTypeScreen,
        PartySettingsScreen, MinigameSelectionScreen, CustomGameScreen,
        MinigameExplanationScreen, SingleMinigameSettingsScreen, SwapVrScreen,
        RoundResultsScreen, EndResultsScreen, LobbyScreen;

    [Header("Minigame Explanation Screen")]
    public Button StartGameButton;
    public Text MinigameNameText;
    public Text ExplanationText;

    [Header("Party Settings Screen")]
    public Button PlayerAddButton;
    public Button PlayerSubtractButton;
    public Button RoundAddButton;
    public Button RoundSubtractButton;
    public Text PlayerCountText;
    public Text RoundCountText;

    [Header("Single Settings Screen")]
    public Button PlayerAddButton2;
    public Button PlayerSubtractButton2;
    public Text PlayerCountText2;

    [Header("Swap Vr Screen")]
    public Text PlayerNameText;

    [Header("Round Results Screen")]
    public Text RW1;
    public Text RW2;
    public Text RW3;
    public Text RW4;
    public Text RW5;

    [Header("End Results Screen")]
    public Text EW1;
    public Text EW2;
    public Text EW3;
    public Text EW4;
    public Text EW5;

    [Header("Lobby Screen")]
    public Text PR1;
    public Text PR2;
    public Text PR3;
    public Text PR4;
    public Text PR5;

    public Text ReadyButtonText;

    public Button StartLobbyGameButton;

    public bool Ready1 = false;
    public bool Ready2 = false;
    public bool Ready3 = false;
    public bool Ready4 = false;
    public bool Ready5 = false;

    public bool AllReady = false;

    public bool OnLobby = false;

    private readonly Dictionary<string, string> MinigameExplanations = new();

    private bool LastMinigamesAgainstPartySettings;

    private string SelectedMinigame;

    private int PlayerCount;
    private int RoundCount;

    private int MaxPlayers = 5;
    private int MinPlayers = 1;
    private int MaxRounds = 5; // Tiene que ser igual al número de minijuegos disponibles
    private int MinRounds = 2;

    private bool PlayerAddInactive;
    private bool PlayerSubtractInactive;
    private bool RoundAddInactive;
    private bool RoundSubtractInactive;

    private void Awake()
    {
        if (Instance == null)
        {
            Instance = this;
        }
    }

    private void Start()
    {
        FillDictionary();

        PlayerCount = MinPlayers;
        RoundCount = MinRounds;

        PlayerSubtractButton.interactable = false;
        PlayerSubtractButton2.interactable = false;
        RoundSubtractButton.interactable = false;
    }

    private void Update()
    {
        if (OnLobby && !AllReady)
        {
            if (PlayerCount >= 1 && !Ready1) return;
            //if (PlayerCount >= 2 && !Ready2) return;
            //if (PlayerCount >= 3 && !Ready3) return;
            //if (PlayerCount >= 4 && !Ready4) return;
            //if (PlayerCount == 5 && !Ready5) return;

            AllReady = true;
            StartLobbyGameButton.interactable = true;
        }

        if (OnLobby && !LobbyScreen.activeInHierarchy)
        {
            OnLobby = false;
        }
    }

    public void ToMainScreen(string Origin)
    {
        if (Origin == "GameTypeScreen")
        {
            GameTypeScreen.SetActive(false);
        }
        else if (Origin == "SettingsScreen")
        {
            SettingsScreen.SetActive(false);
        }

        MainScreen.SetActive(true);
    }

    public void ToSettingsScreen()
    {
        MainScreen.SetActive(false);
        SettingsScreen.SetActive(true);
    }

    public void ToGameTypeScreen(string Origin)
    {
        if (Origin == "MainScreen")
        {
            MainScreen.SetActive(false);
        }
        else if (Origin == "PartySettingsScreen")
        {
            PartySettingsScreen.SetActive(false);
        }
        else if (Origin == "MinigameSelectionScreen")
        {
            MinigameSelectionScreen.SetActive(false);
        }
        else if (Origin == "CustomGameScreen")
        {
            CustomGameScreen.SetActive(false);
        }
        else if (Origin == "EndResultsScreen")
        {
            EndResultsScreen.SetActive(false);
        }

        GameTypeScreen.SetActive(true);
    }

    public void ToPartySettingsScreen(string Origin)
    {
        if (Origin == "GameTypeScreen")
        {
            GameTypeScreen.SetActive(false);
        }
        else if (Origin == "MinigameExplanationScreen")
        {
            MinigameExplanationScreen.SetActive(false);
        }
        else if (Origin == "LobbyScreen")
        {
            LobbyScreen.SetActive(false);
        }

        LastMinigamesAgainstPartySettings = false;
        PartySettingsScreen.SetActive(true);
    }

    public void ToSingleMinigameSettingsScreen(string Origin)
    {
        if (Origin == "MinigameSelectionScreen")
        {
            MinigameSelectionScreen.SetActive(false);
        }
        else if (Origin == "MinigameExplanationScreen")
        {
            MinigameExplanationScreen.SetActive(false);
        }
        else if (Origin == "LobbyScreen")
        {
            LobbyScreen.SetActive(false);
        }

        SingleMinigameSettingsScreen.SetActive(true);
    }

    public void ToLobbyScreen()
    {
        if (PartySettingsScreen.activeInHierarchy)
        {
            PartySettingsScreen.SetActive(false);
        }
        else if (SingleMinigameSettingsScreen.activeInHierarchy)
        {
            SingleMinigameSettingsScreen.SetActive(false);
        }
        else
        {
            MinigameExplanationScreen.SetActive(false);
            SwapVrScreen.SetActive(false);
        }

        LobbyScreen.SetActive(true);
        OnLobby = true;
    }

    public void ToSwapVrScreen(string PlayerName, bool Launched)
    {

        PlayerNameText.text = "Swap the Vr to " + PlayerName + "!";

        if (!Launched)
        {
            MinigameExplanationScreen.SetActive(false);
            SwapVrScreen.SetActive(true);
        }
    }

    public void ToMinigameSelectionScreen(string Origin)
    {
        if (Origin == "GameTypeScreen")
        {
            GameTypeScreen.SetActive(false);
        }
        else if (Origin == "SingleMinigameSettingsScreen")
        {
            SingleMinigameSettingsScreen.SetActive(false);
        }

        LastMinigamesAgainstPartySettings = true;
        MinigameSelectionScreen.SetActive(true);
    }

    public void ToCustomGameScreen()
    {
        GameTypeScreen.SetActive(false);
        CustomGameScreen.SetActive(true);
    }

    public void ToMiniGameExplanationScreen(string Origin)
    {
        SetMinigameInformation();

        if (Origin == "LobbyScreen")
        {
            LobbyScreen.SetActive(false);
        }

        if (Origin != "RoundResultsScreen")
        {
            Debug.Log("Enabling Minigame Explanation Screen");
            MinigameExplanationScreen.SetActive(true);
        }
    }

    public void ToRoundResultsScreen(Dictionary<string, int> OrderedResults, int Turns)
    {
        SetRoundResults(OrderedResults, Turns);

        SwapVrScreen.SetActive(false);
        RoundResultsScreen.SetActive(true);
    }

    public void ToEndResultsScreen(Dictionary<string, int> OrderedResults, int Turns)
    {
        SetEndResults(OrderedResults, Turns);

        if (Turns == 1)
        {
            MinigameExplanationScreen.SetActive(false);
        }
        else
        {
            SwapVrScreen.SetActive(false);
        }

        EndResultsScreen.SetActive(true);
    }

    public void DisableRoundResultsScreen()
    {
        RoundResultsScreen.SetActive(false);
    }

    public void BackFromLobbyScreen()
    {
        string screen = "LobbyScreen";

        if (LastMinigamesAgainstPartySettings)
        {
            ToSingleMinigameSettingsScreen(screen);
            GameManager.Instance.OnGeneralGameEnd();
        }
        else
        {
            ToPartySettingsScreen(screen);
            GameManager.Instance.OnPartyGameEnd(true);
        }
    }

    public void UpdatePlayers(int sum)
    {
        PlayerCount += sum;
        PlayerCountText.text = "Player count: " + PlayerCount;
        PlayerCountText2.text = "Player count: " + PlayerCount;

        if (PlayerCount == MaxPlayers)
        {
            PlayerAddButton.interactable = false;
            PlayerAddButton2.interactable = false;
            PlayerAddInactive = true;
        }
        else if (PlayerCount < MaxPlayers && PlayerAddInactive)
        {
            PlayerAddButton.interactable = true;
            PlayerAddButton2.interactable = true;
            PlayerAddInactive = false;
        }

        if (PlayerCount == MinPlayers)
        {
            PlayerSubtractButton.interactable = false;
            PlayerSubtractButton2.interactable = false;
            PlayerSubtractInactive = true;
        }
        else if (PlayerCount > MinPlayers && (PlayerSubtractInactive || PlayerCount == MinPlayers + 1))
        {
            PlayerSubtractButton.interactable = true;
            PlayerSubtractButton2.interactable = true;
            PlayerSubtractInactive = false;
        }
    }

    public void UpdateRounds(int sum)
    {
        RoundCount += sum;
        RoundCountText.text = "Rounds: " + RoundCount;

        if (RoundCount >= MaxRounds)
        {
            RoundAddButton.interactable = false;
            RoundAddInactive = true;
        }
        else if (RoundCount < MaxRounds && RoundAddInactive)
        {
            RoundAddButton.interactable = true;
            RoundAddInactive = false;
        }

        if (RoundCount <= MinRounds)
        {
            RoundSubtractButton.interactable = false;
            RoundSubtractInactive = true;
        }
        else if (RoundCount > MinRounds && (RoundSubtractInactive || RoundCount == MinRounds + 1))
        {
            RoundSubtractButton.interactable = true;
            RoundSubtractInactive = false;
        }

        GameManager.Instance.SetMaxRounds(RoundCount);
    }

    public void UpdatePlayerStatus()
    {
        Ready1 = !Ready1;
        PR1.text = Ready1 ? "Player 1: Ready" : "Player 1: Not Ready";
        ReadyButtonText.text = Ready1 ? "Unready" : "Ready";

        if (!Ready1)
        {
            AllReady = false;
            StartLobbyGameButton.interactable = false;
        }
    }

    public string GetSelectedMinigame()
    {
        return SelectedMinigame;
    }

    public int GetPlayerCount()
    {
        return PlayerCount;
    }

    public void UnreadyThen()
    {
        Ready1 = false;
    }

    public void SetMinigameInformation()
    {
        string minigame;
        int Round = GameManager.Instance.GetRound();

        if (Round != -1)
        {
            minigame = GameManager.Instance.GetShuffledDictionary()[Round];
        }
        else
        {
            minigame = SelectedMinigame;
        }

        MinigameNameText.text = minigame;
        ExplanationText.text = MinigameExplanations[minigame];
    }

    public void SetRoundResults(Dictionary<string, int> OrderedResults, int Turns)
    {
        RW1.text = new StringBuilder("1st " + OrderedResults.Keys.First() + ": " + OrderedResults.Values.First() + " Points!").ToString();

        if (Turns >= 2)
        {
            RW2.text = new StringBuilder("2nd " + OrderedResults.Keys.ElementAt(1) + ": " + OrderedResults.Values.ElementAt(1) + " Points!").ToString();
        }

        if (Turns >= 3)
        {
            RW3.text = new StringBuilder("3rd " + OrderedResults.Keys.ElementAt(2) + ": " + OrderedResults.Values.ElementAt(2) + " Points!").ToString();
        }

        if (Turns >= 4)
        {
            RW4.text = new StringBuilder("4th " + OrderedResults.Keys.ElementAt(3) + ": " + OrderedResults.Values.ElementAt(3) + " Points!").ToString();
        }

        if (Turns == 5)
        {
            RW5.text = new StringBuilder("5th " + OrderedResults.Keys.Last() + ": " + OrderedResults.Values.Last() + " Points!").ToString();
        }
    }

    public void SetEndResults(Dictionary<string, int> OrderedResults, int Turns) // Points vs rounds won
    {
        EW1.text = new StringBuilder("1st: " + OrderedResults.Keys.First() + " - " + OrderedResults.Values.First() + " Points!").ToString();

        if (Turns >= 2)
        {
            EW2.text = new StringBuilder("2nd: " + OrderedResults.Keys.ElementAt(1) + " - " + OrderedResults.Values.ElementAt(1) + " Points!").ToString();
        }

        if (Turns >= 3)
        {
            EW3.text = new StringBuilder("3rd: " + OrderedResults.Keys.ElementAt(2) + " - " + OrderedResults.Values.ElementAt(2) + " Points!").ToString();
        }

        if (Turns >= 4)
        {
            EW4.text = new StringBuilder("4th: " + OrderedResults.Keys.ElementAt(3) + " - " + OrderedResults.Values.ElementAt(3) + " Points!").ToString();
        }

        if (Turns == 5)
        {
            EW5.text = new StringBuilder("5th: " + OrderedResults.Keys.Last() + " - " + OrderedResults.Values.Last() + " Points!").ToString();
        }
    }

    public void SetSelectedMinigame(string Minigame) // LLAMAR DESDE EL INSPECTOR, CADA MINIJUEGO ES X (SEGÚN LO SELECCIONADO)
    {
        SelectedMinigame = Minigame;
    }

    public Dictionary<string, string> GetMinigameDictionary()
    {
        return MinigameExplanations;
    }

    public void QuitGame()
    {
        Application.Quit();
    }

    private void FillDictionary()
    {
        MinigameExplanations["Basketball"] = "Grab the basketballs and score as many points as you can! Beware of the blocking demons tho...";
        MinigameExplanations["Pong"] = "Swing the paddle and hit the ball! Hit the opposite wall as many times as you can to win, but be careful, the demons can control the other paddle";
        MinigameExplanations["Archery"] = "Shoot the un-crossed targets! Be careful not to hit the rest, the demons control the targets positions!";
        MinigameExplanations["Puzzle"] = "Build the puzzle as fast as you can! Beware of the little demons tho, they tend to be a little explosive...";
    }
}
