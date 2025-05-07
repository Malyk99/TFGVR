using UnityEngine;
using UnityEngine.UI;
using Firebase.Database;
using Firebase.Extensions;
using System.Collections;
using System.Collections.Generic;

public class PlayerSlotManager : MonoBehaviour
{
    public Text[] playerSlots; // Assign in Inspector
    private DatabaseReference dbReference;
    private bool isListening = false;

    void Start()
    {
        dbReference = FirebaseDatabase.DefaultInstance.RootReference;
        StartCoroutine(WaitForRoomCode());
    }

    IEnumerator WaitForRoomCode()
    {
        while (string.IsNullOrEmpty(RoomManagerCreate.CurrentRoomCode))
        {
            yield return null;
        }

        if (!isListening)
        {
            isListening = true;
            ListenToPlayerList(RoomManagerCreate.CurrentRoomCode);
        }
    }

    void ListenToPlayerList(string roomCode)
    {
        DatabaseReference playersRef = dbReference.Child("rooms").Child(roomCode).Child("players");

        playersRef.ValueChanged += (object sender, ValueChangedEventArgs args) =>
        {
            if (args.DatabaseError != null)
            {
                Debug.LogError("Failed to retrieve player list: " + args.DatabaseError.Message);
                return;
            }

            // Hide all slots initially
            foreach (Text slot in playerSlots)
            {
                slot.gameObject.SetActive(false);
            }

            int index = 0;

            if (args.Snapshot != null && args.Snapshot.HasChildren)
            {
                foreach (var child in args.Snapshot.Children)
                {
                    if (index >= playerSlots.Length) break;

                    string displayName = child.Key; // default fallback
                    bool isReady = false;

                    // Try to get "name" field from the player node
                    if (child.Child("name") != null && child.Child("name").Value != null)
                    {
                        displayName = child.Child("name").Value.ToString();
                    }

                    if (child.Child("ready") != null && child.Child("ready").Value != null)
                    {
                        bool.TryParse(child.Child("ready").Value.ToString(), out isReady);
                    }

                    Text slot = playerSlots[index];
                    slot.text = displayName + " - " + (isReady ? "Ready" : "Not Ready");
                    slot.gameObject.SetActive(true);

                    index++;
                }
            }
        };
    }
}
