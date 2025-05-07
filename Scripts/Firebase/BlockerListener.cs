using System.Collections;
using UnityEngine;
using Firebase.Database;
using Firebase.Extensions;
using UnityEngine.UI;

public class BlockerListener : MonoBehaviour
{
    public GameObject[] blockerObjects;
    public Text minigameTimeLeftText;

    private DatabaseReference dbReference;
    private string roomCode;

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

        roomCode = RoomManagerCreate.CurrentRoomCode;

        if (string.IsNullOrEmpty(roomCode))
        {
            Debug.LogError("No room code available in BlockerListener.");
            yield break;
        }

        // Set up a listener for each blocker
        for (int i = 0; i < blockerObjects.Length; i++)
        {
            int index = i;
            DatabaseReference blockerRef = dbReference
                .Child("rooms").Child(roomCode)
                .Child("minigames").Child("minigame1")
                .Child("blocker" + (index + 1));

            blockerRef.ValueChanged += (sender, args) =>
            {
                OnBlockerValueChanged(sender, args, index);
            };
        }

        // Set up a listener for the minigame timer
        DatabaseReference timeLeftRef = dbReference.Child("rooms").Child(roomCode).Child("minigameTimeLeft");
        timeLeftRef.ValueChanged += OnTimeLeftChanged;
    }

    private void OnBlockerValueChanged(object sender, ValueChangedEventArgs args, int index)
    {
        if (args.DatabaseError != null)
        {
            Debug.LogError("Error reading blocker" + (index + 1) + ": " + args.DatabaseError.Message);
            return;
        }

        if (args.Snapshot != null && args.Snapshot.Value != null)
        {
            bool isActive;
            if (bool.TryParse(args.Snapshot.Value.ToString(), out isActive))
            {
                Debug.Log("Blocker" + (index + 1) + " state changed to: " + isActive);

                if (blockerObjects[index] != null)
                {
                    blockerObjects[index].SetActive(isActive);
                }
            }
            else
            {
                Debug.LogError("Unable to parse blocker" + (index + 1) + " value as boolean.");
            }
        }
    }

    private void OnTimeLeftChanged(object sender, ValueChangedEventArgs args)
    {
        if (args.DatabaseError != null)
        {
            Debug.LogError("Error reading minigameTimeLeft: " + args.DatabaseError.Message);
            return;
        }

        if (minigameTimeLeftText == null)
        {
            Debug.LogWarning("minigameTimeLeftText is not assigned.");
            return;
        }

        float timeLeft = 0f;
        if (args.Snapshot != null && args.Snapshot.Value != null)
        {
            if (float.TryParse(args.Snapshot.Value.ToString(), out timeLeft))
            {
                minigameTimeLeftText.text = "Time Left: " + Mathf.Ceil(timeLeft).ToString();
            }
            else
            {
                minigameTimeLeftText.text = "Time Left: " + args.Snapshot.Value.ToString();
            }
        }
        else
        {
            minigameTimeLeftText.text = "Time Left: 0";
        }
    }
}
