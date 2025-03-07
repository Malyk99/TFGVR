using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class UiController : MonoBehaviour
{
    public static UiController Instance { get; private set; }

    public GameObject MainScreen, SettingsScreen, GameTypeScreen, PartySettingsScreen, MinigameSelectionScreen, CustomGameScreen, MinigameExplanationScreen, ResultsScreen;

    [Header("Minigame Explanation Screen")]
    public Button BackButton;
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

    private readonly Dictionary<string, string> MinigameExplanations = new Dictionary<string, string>();

    private bool LastMinigamesAgainstPartySettings;

    private string SelectedMinigame;

    private int PlayerCount;
    private int RoundCount;

    private int MaxPlayers = 5;
    private int MinPlayers = 1;
    private int MaxRounds = 10;
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

        LastMinigamesAgainstPartySettings = false;
        PartySettingsScreen.SetActive(true);
    }

    public void Updateplayers(int sum)
    {
        PlayerCount += sum;
        PlayerCountText.text = "Player count: " + PlayerCount;

        if (PlayerCount >= MaxPlayers)
        {
            PlayerAddButton.interactable = false;
            PlayerAddInactive = true;
        }
        else if (PlayerCount < MaxPlayers && PlayerAddInactive)
        {
            PlayerAddButton.interactable = true;
            PlayerAddInactive = false;
        }

        if (PlayerCount <= MinPlayers)
        {
            PlayerSubtractButton.interactable = false;
            PlayerSubtractInactive = true;
        }
        else if (PlayerCount > MinPlayers && PlayerSubtractInactive)
        {
            PlayerSubtractButton.interactable = true;
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
        else if (RoundCount > MinRounds && RoundSubtractInactive)
        {
            RoundSubtractButton.interactable = true;
            RoundSubtractInactive = false;
        }
    }

    public void ToMinigameSelectionScreen(string Origin)
    {
        if (Origin == "GameTypeScreen")
        {
            GameTypeScreen.SetActive(false);
        }
        else if (Origin == "MinigameExplanationScreen")
        {
            MinigameExplanationScreen.SetActive(false);
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
        if (Origin == "PartySettingsScreen")
        {
            PartySettingsScreen.SetActive(false);
        } 
        else if (Origin == "MinigameSelectionScreen")
        {
            MinigameSelectionScreen.SetActive(false);
        }

        if (Origin != null)
        {
            MinigameExplanationScreen.SetActive(true);
            BackButton.gameObject.SetActive(true);
        }
        else
        {
            StartGameButton.gameObject.transform.position =
                new Vector3(0, StartGameButton.gameObject.transform.position.y, StartGameButton.gameObject.transform.position.z);

            BackButton.gameObject.SetActive(false);
        }
    }

    public void ToRoundResultsScreen()
    {
        MinigameExplanationScreen.SetActive(false);
        SetResultInformation();
        ResultsScreen.SetActive(true);
    }

    public void BackFromMinigameExplanationScreen()
    {
        string screen = "MinigameExplanationScreen";

        if (LastMinigamesAgainstPartySettings)
        {
            ToMinigameSelectionScreen(screen);
        }
        else
        {
            ToPartySettingsScreen(screen);
        }
    }

    public void SetMinigameInformation(int Round)
    {
        string minigame;

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

    public void SetResultInformation()
    {

    }

    public void SetSelectedMinigame(string Minigame)
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
        MinigameExplanations["Basketball"] = "Basketball explanation";

        // Rellenar con minijuegos
    }
}
