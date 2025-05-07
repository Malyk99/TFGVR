using UnityEngine;
using Firebase;
using Firebase.Database;
using Firebase.Extensions;

public class VRRoundTrigger : MonoBehaviour
{
    public string roomCode = "742646";
    public string roundKey = "round1";

    private bool hasTriggered = false;

    private void OnTriggerEnter(Collider other)
    {
        if (hasTriggered) return;

        if (other.CompareTag("Player"))
        {
            hasTriggered = true;
            Debug.Log(" VR player entered minigame zone");

            // Set gameState.started = true and start round timer
            FirebaseDatabase.DefaultInstance
                .GetReference("rooms")
                .Child(roomCode)
                .Child("gameState")
                .Child("started")
                .SetValueAsync(true);

            FirebaseDatabase.DefaultInstance
                .GetReference("rooms")
                .Child(roomCode)
                .Child("gameState")
                .Child("currentRound")
                .SetValueAsync(roundKey);

            // You can also trigger logic locally:
           // FindObjectOfType<RoundManager>()?.StartRoundSequence();
        }
    }
}
