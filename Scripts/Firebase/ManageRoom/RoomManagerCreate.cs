using UnityEngine;
using UnityEngine.UI;
using TMPro;
using Firebase.Database;
using Firebase.Extensions;
using System.Collections.Generic;

public class RoomManagerCreate : MonoBehaviour
{
    public TMP_Text roomCodeDisplay;
    public Text errorMessageText;

    // UI for privacy toggle
    public Button privacyToggleButton;
    public TMP_Text privacyToggleButtonText;

    private DatabaseReference dbReference;

    public static string CurrentRoomCode { get; private set; }

    public string username = "Player 1";

    [Header("Room Settings")]
    public bool isPrivate = true;

    void Start()
    {
        dbReference = FirebaseDatabase.DefaultInstance.RootReference;

        if (errorMessageText != null)
            errorMessageText.text = "";

        if (privacyToggleButtonText != null)
            privacyToggleButtonText.text = isPrivate ? "Private" : "Public";
    }

    public void OnCreateRoomButtonPressed()
    {
        if (errorMessageText != null)
            errorMessageText.text = "";

        int randomRoomNumber = Random.Range(100000, 1000000);
        string roomCode = randomRoomNumber.ToString();
        Debug.Log("Generated Room Code: " + roomCode);

        if (roomCodeDisplay != null)
            roomCodeDisplay.text = "Lobby Code: " + roomCode;

        dbReference.Child("rooms").Child(roomCode).GetValueAsync().ContinueWithOnMainThread(task =>
        {
            if (task.IsFaulted || task.Result == null)
            {
                Debug.LogError("Error checking room existence: " + task.Exception);
                if (errorMessageText != null)
                    errorMessageText.text = "Error checking room existence. Please try again.";
                return;
            }

            if (task.Result.Exists)
            {
                Debug.Log("Room code " + roomCode + " already exists. Please try again.");
                if (errorMessageText != null)
                    errorMessageText.text = "Room code already exists, please try again.";
            }
            else
            {
                CreateRoom(roomCode);
            }
        });
    }

    void CreateRoom(string roomCode)
    {
        int roomCodeInt = int.Parse(roomCode);

        // Step 1: Create main room structure
        var playerData = new Dictionary<string, object>
    {
        { "ready", true },
        { "name", username }
    };

        var roomData = new Dictionary<string, object>
    {
        { "id", roomCodeInt },
        { "private", isPrivate },
        { "players", new Dictionary<string, object> { { username, playerData } } }
    };

        dbReference.Child("rooms").Child(roomCode)
            .SetValueAsync(roomData).ContinueWithOnMainThread(task =>
            {
                if (task.IsCompleted)
                {
                    Debug.Log("Room basic structure created.");
                    CurrentRoomCode = roomCode;

                // Step 2: Now add minigame1 manually with SetValueAsync
                dbReference.Child("rooms").Child(roomCode)
                        .Child("minigames").Child("minigame1").Child("gameState")
                        .SetValueAsync(null).ContinueWithOnMainThread(minigameTask =>
                        {
                            if (minigameTask.IsCompleted)
                            {
                                Debug.Log("minigame1 structure added successfully.");
                            }
                            else
                            {
                                Debug.LogError("Failed to add minigame1: " + minigameTask.Exception);
                            }

                        // Navigate to lobby regardless
                        UiController.Instance.ToLobbyScreen();
                        });
                }
                else
                {
                    Debug.LogError("Failed to create room: " + task.Exception);
                    if (errorMessageText != null)
                        errorMessageText.text = "Failed to create room. Please try again.";
                }
            });
    }




    public void TogglePrivacyStatus()
    {
        isPrivate = !isPrivate;

        if (privacyToggleButtonText != null)
            privacyToggleButtonText.text = isPrivate ? "Private" : "Public";

        if (!string.IsNullOrEmpty(CurrentRoomCode))
        {
            dbReference.Child("rooms").Child(CurrentRoomCode).Child("private").SetValueAsync(isPrivate)
                .ContinueWithOnMainThread(task =>
                {
                    if (task.IsCompleted)
                    {
                        Debug.Log("Privacy status updated to " + isPrivate + " in Firebase.");
                    }
                    else
                    {
                        Debug.LogError("Failed to update privacy in Firebase: " + task.Exception);
                        if (errorMessageText != null)
                            errorMessageText.text = "Failed to update privacy. Try again.";
                    }
                });
        }
    }
}
