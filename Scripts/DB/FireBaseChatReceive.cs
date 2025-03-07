using UnityEngine;
using Firebase.Database;
using TMPro; // For TextMeshPro
using UnityEngine.UI; // For Button

public class FirebaseChatReceive : MonoBehaviour
{
    private DatabaseReference dbReference;

    public TextMeshProUGUI chatDisplay; // Assign the Text component
    
    void Start()
    {
        dbReference = FirebaseDatabase.DefaultInstance.RootReference;

        // Listen for chat updates
        FirebaseDatabase.DefaultInstance
            .GetReference("messages")
            .ValueChanged += OnMessageReceived;

    }

    // Update UI When a New Message is Received
    private void OnMessageReceived(object sender, ValueChangedEventArgs args)
    {
        if (args.Snapshot.Exists)
        {
            chatDisplay.text = args.Snapshot.Value.ToString();
        }
    }
}
