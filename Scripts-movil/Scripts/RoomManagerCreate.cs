using UnityEngine;
using UnityEngine.UI;
using TMPro;
using Firebase.Database;
using Firebase.Extensions;

public class RoomManagerCreate : MonoBehaviour
{
    public TMP_InputField usernameInput;
    public Button createRoomButton;

    // Canvas references for switching screens
    public GameObject currentCanvas;
    public GameObject nextCanvas;

    // TMP Text to display the generated room code to the user
    public TMP_Text roomCodeDisplay;

    // Optional UI Text (non-TMP) to display error messages
    public Text errorMessageText;

    // Firebase Database reference
    private DatabaseReference dbReference;

    // Global variable to store the room code (accessible by other scripts)
    public static string CurrentRoomCode { get; private set; }

    void Start()
    {
        dbReference = FirebaseDatabase.DefaultInstance.RootReference;
        createRoomButton.onClick.AddListener(OnCreateRoomButtonPressed);

        if (errorMessageText != null)
        {
            errorMessageText.text = "";
        }
    }

    public void OnCreateRoomButtonPressed()
    {
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
            roomCodeDisplay.text = roomCode;
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
                CreateRoom(roomCode, username);
            }
        });
    }

    void CreateRoom(string roomCode, string username)
    {
        // Create room by adding the user as a player.
        dbReference.Child("rooms").Child(roomCode).Child("players").Child(username)
            .SetValueAsync(true).ContinueWithOnMainThread(task =>
            {
                if (task.IsCompleted)
                {
                    Debug.Log("Room " + roomCode + " created successfully with user " + username);
                    CurrentRoomCode = roomCode;
                    // Switch from the current canvas to the next canvas.
                    currentCanvas.SetActive(false);
                    nextCanvas.SetActive(true);
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
