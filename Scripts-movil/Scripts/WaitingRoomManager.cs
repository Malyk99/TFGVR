using UnityEngine;
using UnityEngine.UI;
using TMPro;
using Firebase.Database;
using Firebase.Extensions;
using System.Collections.Generic;
using System.Collections;

public class WaitingRoomManager : MonoBehaviour
{
    // UI Elements
    public GameObject playerItemPrefab;  // Prefab for each player in the ScrollView
    public Transform playerListContent;  // Content of the ScrollView
    public TMP_Text roomCodeText;        // Displays the room code
    public Button readyButton;           // Toggles Ready status
    public TMP_Text readyButtonText;     // Text for Ready Button
    public Button startGameButton;       // Host can start game when all are ready
    public GameObject waitingCanvas;     // The waiting room canvas

    // Firebase Database reference
    private DatabaseReference dbReference;

    // Dictionary to track player UI elements
    private Dictionary<string, GameObject> playerUIItems = new Dictionary<string, GameObject>();

    // Room and player info
    public string roomCode;
    private string currentPlayerName;

    void Start()
    {
        dbReference = FirebaseDatabase.DefaultInstance.RootReference;

        // Start waiting for the canvas to become active
        StartCoroutine(WaitForCanvasAndRoomCode());
    }

    // Waits for the canvas to be active and for the room code to be available
    private IEnumerator WaitForCanvasAndRoomCode()
    {
        // Wait until the waiting canvas is enabled
        while (!waitingCanvas.activeInHierarchy)
        {
            yield return null; // Waits for the next frame
        }

        // Now wait for the room code to be available
        while (string.IsNullOrEmpty(ExtractRoomCode(roomCodeText.text)))
        {
            yield return null;
        }

        // Now that the room code is available, initialize everything
        roomCode = ExtractRoomCode(roomCodeText.text);
        InitializeWaitingRoom();
    }

    // Initializes Firebase listeners once the room code is available
    private void InitializeWaitingRoom()
    {
        if (string.IsNullOrEmpty(roomCode))
        {
            Debug.LogError("No room code available. Cannot set up waiting room.");
            return;
        }

        // Get the current player's name (modify if using login system)
        currentPlayerName = PlayerPrefs.GetString("PlayerName", "Player" + Random.Range(1000, 9999));

        // Set up Firebase listeners
        DatabaseReference playersRef = dbReference.Child("rooms").Child(roomCode).Child("players");
        playersRef.ChildAdded += OnPlayerJoined;
        playersRef.ChildRemoved += OnPlayerLeft;
        playersRef.ChildChanged += OnReadyStatusChanged;

        // Button listeners
        readyButton.onClick.AddListener(ToggleReadyStatus);
        startGameButton.interactable = false; // Start disabled
    }

    // Extracts room code from "Room code: 123456"
    private string ExtractRoomCode(string text)
    {
        string prefix = "Room code:";
        if (text.ToLower().StartsWith(prefix.ToLower()))
        {
            return text.Substring(prefix.Length).Trim();
        }
        return text.Trim();
    }

    // When a player joins, update UI
    private void OnPlayerJoined(object sender, ChildChangedEventArgs args)
    {
        if (args.Snapshot.Exists)
        {
            string playerName = args.Snapshot.Key;
            bool isReady = args.Snapshot.Child("ready").Exists && args.Snapshot.Child("ready").Value.ToString() == "True";

            if (!playerUIItems.ContainsKey(playerName))
            {
                GameObject newPlayerItem = Instantiate(playerItemPrefab, playerListContent);
                newPlayerItem.transform.Find("PlayerNameText").GetComponent<TMP_Text>().text = playerName;
                newPlayerItem.transform.Find("ReadyStatusText").GetComponent<TMP_Text>().text = isReady ? "Ready" : "Not Ready";

                playerUIItems[playerName] = newPlayerItem;
            }

            CheckIfAllPlayersReady();
        }
    }

    // When a player leaves, remove from UI
    private void OnPlayerLeft(object sender, ChildChangedEventArgs args)
    {
        if (args.Snapshot.Exists)
        {
            string playerName = args.Snapshot.Key;

            if (playerUIItems.ContainsKey(playerName))
            {
                Destroy(playerUIItems[playerName]);
                playerUIItems.Remove(playerName);
            }

            CheckIfAllPlayersReady();
        }
    }

    // When a player's ready status changes, update UI
    private void OnReadyStatusChanged(object sender, ChildChangedEventArgs args)
    {
        if (args.Snapshot.Exists)
        {
            string playerName = args.Snapshot.Key;
            bool isReady = args.Snapshot.Child("ready").Exists && args.Snapshot.Child("ready").Value.ToString() == "True";

            if (playerUIItems.ContainsKey(playerName))
            {
                playerUIItems[playerName].transform.Find("ReadyStatusText").GetComponent<TMP_Text>().text = isReady ? "Ready" : "Not Ready";
            }

            CheckIfAllPlayersReady();
        }
    }

    // Player toggles Ready status in Firebase
    public void ToggleReadyStatus()
    {
        DatabaseReference playerRef = dbReference.Child("rooms").Child(roomCode).Child("players").Child(currentPlayerName).Child("ready");

        bool isCurrentlyReady = readyButtonText.text == "Ready";
        playerRef.SetValueAsync(!isCurrentlyReady).ContinueWithOnMainThread(task =>
        {
            if (task.IsCompleted)
            {
                readyButtonText.text = isCurrentlyReady ? "Not Ready" : "Ready";
            }
        });
    }

    // Checks if all players are ready
    private void CheckIfAllPlayersReady()
    {
        DatabaseReference playersRef = dbReference.Child("rooms").Child(roomCode).Child("players");

        playersRef.GetValueAsync().ContinueWithOnMainThread(task =>
        {
            if (task.IsCompleted && task.Result.Exists)
            {
                bool allReady = true;
                foreach (var player in task.Result.Children)
                {
                    if (!player.Child("ready").Exists || player.Child("ready").Value.ToString() != "True")
                    {
                        allReady = false;
                        break;
                    }
                }

                // Enable "Start Game" button only when all players are ready
                startGameButton.interactable = allReady;
            }
        });
    }
}
