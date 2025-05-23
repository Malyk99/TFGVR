using UnityEngine;
using Firebase.Database;
using Firebase.Extensions;

public class FireBasePongPoints : MonoBehaviour
{
    private DatabaseReference dbReference;

    void Start()
    {
        dbReference = FirebaseDatabase.DefaultInstance.RootReference;
    }

    public void UpdateMinigame2Score(int score)
    {
        string roomCode = RoomManagerCreate.CurrentRoomCode;

        if (string.IsNullOrEmpty(roomCode))
        {
            Debug.LogWarning("Room code is not set. Cannot update score.");
            return;
        }

        dbReference.Child("rooms")
            .Child(roomCode)
            .Child("minigames")
            .Child("minigame2")
            .Child("points")
            .SetValueAsync(score)
            .ContinueWithOnMainThread(task =>
            {
                if (task.IsCompleted)
                {
                    Debug.Log($"Score {score} updated in Firebase for minigame2.");
                }
                else
                {
                    Debug.LogError("Failed to update minigame2 score: " + task.Exception);
                }
            });
    }
}
