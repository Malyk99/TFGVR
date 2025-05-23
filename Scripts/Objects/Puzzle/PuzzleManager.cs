using System.Collections;
using UnityEngine;
using Firebase.Database;
using Firebase.Extensions;

public class PuzzleTriggerManager : MonoBehaviour
{
    [Header("Trigger Zones")]
    public PuzzleZone[] puzzleZones; // Assign 3 trigger zones in the Inspector

    [Header("Win Settings")]
    public float confirmDuration = 1f;

    private bool isChecking = false;
    private string roomCode;
    private string minigameKey;
    private bool roomCodeReady = false;

    private void Start()
    {
        StartCoroutine(WaitForFirebaseSetup());
    }

    private IEnumerator WaitForFirebaseSetup()
    {
        while (string.IsNullOrEmpty(RoomManagerCreate.CurrentRoomCode))
        {
            yield return null;
        }

        roomCode = RoomManagerCreate.CurrentRoomCode;
        minigameKey = MinigameManager.MinigameKey; // Assumes it's already been selected

        if (string.IsNullOrEmpty(minigameKey))
        {
            Debug.LogWarning("?? Minigame key not yet set in MinigameManager. Will retry...");
            while (string.IsNullOrEmpty(MinigameManager.MinigameKey))
            {
                yield return null;
            }
            minigameKey = MinigameManager.MinigameKey;
        }

        roomCodeReady = true;
        Debug.Log($"?? Room code: {roomCode}, Minigame key: {minigameKey}");
    }

    //private void Update()
    //{
    //    if (roomCodeReady && AllZonesHavePuzzle() && !isChecking)
    //    {
    //        StartCoroutine(CheckForWin());
    //    }
    //}

    //private bool AllZonesHavePuzzle()
    //{
    //    foreach (var zone in puzzleZones)
    //    {
    //        if (!zone.HasPuzzleInside)
    //            return false;
    //    }
    //    return true;
    //}

    //private IEnumerator CheckForWin()
    //{
    //    isChecking = true;
    //    float timer = 0f;

    //    while (timer < confirmDuration)
    //    {
    //        if (!AllZonesHavePuzzle())
    //        {
    //            isChecking = false;
    //            yield break;
    //        }

    //        timer += Time.deltaTime;
    //        yield return null;
    //    }

    //    Debug.Log("?? YOU WIN!");
    //    SetGameStateToFinished();
    //    isChecking = false;
    //}

    private void SetGameStateToFinished()
    {
        if (string.IsNullOrEmpty(roomCode) || string.IsNullOrEmpty(minigameKey))
        {
            Debug.LogError("? Room code or minigame key missing.");
            return;
        }

        string path = $"rooms/{roomCode}/minigames/minigame4/gameState";

        FirebaseDatabase.DefaultInstance.GetReference(path)
            .SetValueAsync("finished")
            .ContinueWithOnMainThread(task =>
            {
                if (task.IsCompletedSuccessfully)
                    Debug.Log("? gameState set to 'finished'");
                else
                    Debug.LogError("? Failed to set gameState: " + task.Exception);
            });
    }
}
