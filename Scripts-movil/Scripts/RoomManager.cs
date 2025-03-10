using UnityEngine;
using TMPro;
using UnityEngine.UI;
using Firebase.Database;
using Firebase.Extensions;

public class RoomManager : MonoBehaviour
{
    public TMP_InputField usernameInput;
    public TMP_InputField roomCodeInput;
    public TextMeshProUGUI roomCodeDisplay; // UI Text to display room code
    public Button joinRoomButton;

    // UI Text box (non-TMP) to display error messages.
    public Text errorMessageText;

    public GameObject currentCanvas;
    public GameObject gameCanvas;
    public GameObject adminCanvas;

    private DatabaseReference dbReference;
    public static string CurrentRoomCode { get; private set; } // Public variable for other scripts

    void Start()
    {
        dbReference = FirebaseDatabase.DefaultInstance.RootReference;
        joinRoomButton.interactable = false;
        usernameInput.onValueChanged.AddListener(delegate { CheckInputs(); });
        roomCodeInput.onValueChanged.AddListener(delegate { CheckInputs(); });
        roomCodeDisplay.text = ""; // Ensure it's empty at start
        if (errorMessageText != null)
        {
            errorMessageText.text = ""; // Clear any error message at start
        }
    }

    void CheckInputs()
    {
        joinRoomButton.interactable = !string.IsNullOrEmpty(usernameInput.text.Trim()) &&
                                      !string.IsNullOrEmpty(roomCodeInput.text.Trim());
    }

    public void OnJoinRoomButtonPressed()
    {
        string username = usernameInput.text.Trim();
        string roomCode = roomCodeInput.text.Trim();

        if (string.IsNullOrEmpty(username) || string.IsNullOrEmpty(roomCode))
        {
            Debug.Log("Please enter a valid username and room code.");
            return;
        }

        // Clear any previous error message.
        if (errorMessageText != null)
        {
            errorMessageText.text = "";
        }

        CurrentRoomCode = roomCode; // Store room code for global access
        roomCodeDisplay.text = roomCode; // Update UI text

        CheckIfAdmin(username, roomCode, (isAdmin) =>
        {
            if (isAdmin)
            {
                Debug.Log($"Admin {username} detected for Room {roomCode}. Switching to Admin Canvas.");
                SwitchToAdminCanvas();
            }
            else
            {
                CheckRoomExists(roomCode, (exists) =>
                {
                    if (exists)
                    {
                        Debug.Log($"Room {roomCode} exists. Joining...");
                        SwitchToGameCanvas();
                    }
                    else
                    {
                        if (errorMessageText != null)
                        {
                            errorMessageText.text = "Room number doesn't exist";
                        }
                    }
                });
            }
        });
    }

    void CheckIfAdmin(string username, string roomCode, System.Action<bool> callback)
    {
        dbReference.Child("admins").Child(username).GetValueAsync().ContinueWithOnMainThread(task =>
        {
            if (task.IsFaulted || task.Result == null)
            {
                Debug.LogError("Error checking admin: " + task.Exception);
                callback(false);
                return;
            }

            callback(task.Result.Exists && task.Result.Value.ToString() == roomCode);
        });
    }

    void CheckRoomExists(string roomCode, System.Action<bool> callback)
    {
        dbReference.Child("rooms").Child(roomCode).GetValueAsync().ContinueWithOnMainThread(task =>
        {
            if (task.IsFaulted || task.Result == null)
            {
                Debug.LogError("Error checking room: " + task.Exception);
                callback(false);
                return;
            }

            callback(task.Result.Exists);
        });
    }

    void SwitchToGameCanvas()
    {
        currentCanvas.SetActive(false);
        gameCanvas.SetActive(true);
    }

    void SwitchToAdminCanvas()
    {
        currentCanvas.SetActive(false);
        adminCanvas.SetActive(true);
    }
}
