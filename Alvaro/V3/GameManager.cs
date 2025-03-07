using System.Collections.Generic;
using UnityEngine;

public class GameManager : MonoBehaviour
{
    public static GameManager Instance {  get; private set; }

    private Dictionary<int, string> ShuffledMinigames = new Dictionary<int, string>();

    public int Round;

    private void Awake()
    {
        if (Instance == null)
        {
            Instance = this;
        }
    }

    public void StartPartyGame(int RoundCount, int Players)
    {
        Round++;
        ShuffleAndReindex(UiController.Instance.GetMinigameDictionary());
        UiController.Instance.SetMinigameInformation(Round);
    }

    public void StartSingleMinigame(string Minigame)
    {
        UiController.Instance.SetSelectedMinigame(Minigame);
        UiController.Instance.SetMinigameInformation(Round);
        UiController.Instance.ToMiniGameExplanationScreen("MinigameSelectionScreen");
    }

    public void OnRoundEnd()
    {
        Round++;
        UiController.Instance.ToRoundResultsScreen();
    }

    public void OnPartyGameEnd()
    {
        Round = -1;
        ShuffledMinigames.Clear();

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
