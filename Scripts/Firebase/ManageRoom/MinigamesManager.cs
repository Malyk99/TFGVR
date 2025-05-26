using UnityEngine;
using System.Collections;
using Firebase.Database;
using Firebase.Extensions;

public class MinigameManager : MonoBehaviour
{
    public static string MinigameKey; // e.g., "minigame1", "minigame2", etc.
    private string roomCode;
    private bool roomCodeReady = false;

    private void Start()
    {
        StartCoroutine(WaitForRoomCode());
    }

    private IEnumerator WaitForRoomCode()
    {
        while (string.IsNullOrEmpty(RoomManagerCreate.CurrentRoomCode))
        {
            yield return null;
        }

        roomCode = RoomManagerCreate.CurrentRoomCode;
        roomCodeReady = true;
    }

    public static void SelectMinigame(string uiKey)
    {
        switch (uiKey.ToLower())
        {
            case "basketball":
                MinigameKey = "minigame1"; break;
            case "pong":
                MinigameKey = "minigame2"; break;
            case "archery":
                MinigameKey = "minigame3"; break;
            case "puzzle":
                MinigameKey = "minigame4"; break;
            default:
                Debug.LogError("? Unknown minigame key: " + uiKey); return;
        }

        Debug.Log("?? Minigame selected: " + MinigameKey);
    }

    public void SetGameStateToTutorial()
    {
        SetGameState("tutorial");
    }

    public void SetGameStateToInProgress()
    {
        SetGameState("inProgress");
    }

    private void SetGameState(string newState)
    {
        if (!roomCodeReady || string.IsNullOrEmpty(roomCode))
        {
            Debug.LogError("? Room code is not ready.");
            return;
        }

        if (string.IsNullOrEmpty(MinigameKey))
        {
            Debug.LogError("? Minigame key is not set.");
            return;
        }

        string path = $"rooms/{roomCode}/minigames/{MinigameKey}/gameState";

        FirebaseDatabase.DefaultInstance.GetReference(path)
            .SetValueAsync(newState)
            .ContinueWithOnMainThread(task =>
            {
                if (task.IsCompletedSuccessfully)
                    Debug.Log($"? gameState set to '{newState}' at {path}");
                else
                    Debug.LogError("? Failed to set gameState: " + task.Exception);
            });
    }
}
