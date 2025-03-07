using UnityEngine;
using Firebase.Database;
using TMPro; // For TextMeshPro
using UnityEngine.UI; // For Button

public class FirebaseChat : MonoBehaviour
{
    private DatabaseReference dbReference;

    public TMP_InputField messageInput; // Assign the InputField
    public TextMeshProUGUI chatDisplay; // Assign the Text component
    public Button sendButton; // Assign the Send Button

    void Start()
    {
        dbReference = FirebaseDatabase.DefaultInstance.RootReference;

        // Listen for chat updates
        FirebaseDatabase.DefaultInstance
            .GetReference("messages")
            .ValueChanged += OnMessageReceived;

        sendButton.onClick.AddListener(SendMessage);
    }

    // Send Message to Firebase
    public void SendMessage()
    {
        if (!string.IsNullOrEmpty(messageInput.text))
        {
            dbReference.Child("messages").SetValueAsync(messageInput.text);
            messageInput.text = ""; // Clear input after sending
        }
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
