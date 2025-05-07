using UnityEngine;
using UnityEngine.UI;
using TMPro;
using Firebase.Database;
using Firebase.Extensions;
using TMPro.EditorUtilities;
using System.Collections.Generic;

public class RoomManagerCreate : MonoBehaviour
{
    // public TMP_InputField usernameInput;
    // public Button createRoomButton;

    // TMP Text to display the generated room code to the user
    public TMP_Text roomCodeDisplay;

    // Optional UI Text (non-TMP) to display error messages
    public Text errorMessageText;

    // Firebase Database reference
    private DatabaseReference dbReference;

    // Global variable to store the room code (accessible by other scripts)
    public static string CurrentRoomCode { get; private set; }

    public string username = "Player 1";

    void Start()
    {
        dbReference = FirebaseDatabase.DefaultInstance.RootReference;

        if (errorMessageText != null)
        {
            errorMessageText.text = "";
        }
    }

    public void OnCreateRoomButtonPressed()
    {
        /*
        string username = usernameInput.text.Trim();
        if (string.IsNullOrEmpty(username))
        {
            Debug.Log("Please enter a valid username.");
            if (errorMessageText != null)
            {
                errorMessageText.text = "Please enter a valid username.";
            }
            return;
        }
        */

        // Clear any previous error message.
        if (errorMessageText != null)
        {
            errorMessageText.text = "";
        }

        // Generate a random 6-digit room code once.
        int randomRoomNumber = Random.Range(100000, 1000000);
        string roomCode = randomRoomNumber.ToString();
        Debug.Log("Generated Room Code: " + roomCode);

        // Display the generated room code in the TMP text box.
        if (roomCodeDisplay != null)
        {
            roomCodeDisplay.text = "Lobby Code: " + roomCode;
        }

        // Check if the room already exists in Firebase.
        dbReference.Child("rooms").Child(roomCode).GetValueAsync().ContinueWithOnMainThread(task =>
        {
            if (task.IsFaulted || task.Result == null)
            {
                Debug.LogError("Error checking room existence: " + task.Exception);
                if (errorMessageText != null)
                {
                    errorMessageText.text = "Error checking room existence. Please try again.";
                }
                return;
            }

            if (task.Result.Exists)
            {
                // If the room code exists, show an error message.
                Debug.Log("Room code " + roomCode + " already exists. Please try again.");
                if (errorMessageText != null)
                {
                    errorMessageText.text = "Room code already exists, please try again.";
                }
            }
            else
            {
                // Room doesn't exist. Proceed to create it.
                CreateRoom(roomCode);
            }
        });
    }

    void CreateRoom(string roomCode)
    {
        // Convert the string roomCode to an int
        int roomCodeInt = int.Parse(roomCode);

        // Prepare player data with "ready" and "name"
        var playerData = new Dictionary<string, object>
    {
        { "ready", true },
        { "name", username }
    };

        // Prepare the full room data with id and player
        var roomData = new Dictionary<string, object>
    {
        { "id", roomCodeInt },
        { $"players/{username}", playerData }
    };

        dbReference.Child("rooms").Child(roomCode)
            .UpdateChildrenAsync(roomData).ContinueWithOnMainThread(task =>
            {
                if (task.IsCompleted)
                {
                    Debug.Log("Room " + roomCode + " created with player " + username + ", ready=true, and name field.");
                    CurrentRoomCode = roomCode;

                // Proceed to lobby screen
                UiController.Instance.ToLobbyScreen();
                }
                else
                {
                    Debug.LogError("Failed to create room: " + task.Exception);
                    if (errorMessageText != null)
                    {
                        errorMessageText.text = "Failed to create room. Please try again.";
                    }
                }
            });
    }



}