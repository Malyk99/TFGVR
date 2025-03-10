using UnityEngine;
using UnityEngine.UI;
using Firebase.Database;
using Firebase.Extensions;
using TMPro;
using System.Collections;

public class BlockerManager : MonoBehaviour
{
    // Array of buttons corresponding to different blockers.
    // Assign these buttons in the Inspector (index 0 for blocker1, index 1 for blocker2, etc.)
    public Button[] blockerButtons;

    // Array of TMP_Text elements to display the individual button cooldown countdown.
    public TMP_Text[] countdownTexts;

    // TMP Text element that displays the room code (e.g., "Room code: 123456")
    public TMP_Text roomCodeText;

    // Firebase Database reference.
    private DatabaseReference dbReference;

    // Cooldown duration (in seconds) after a button is pressed.
    public float cooldownDuration = 10f;

    void Start()
    {
        dbReference = FirebaseDatabase.DefaultInstance.RootReference;

        // Attach listeners to each button.
        for (int i = 0; i < blockerButtons.Length; i++)
        {
            int index = i; // Capture the index for the lambda.
            blockerButtons[i].onClick.AddListener(() => OnBlockerButtonPressed(index));

            // Initialize the individual countdown text to empty.
            if (countdownTexts != null && countdownTexts.Length > i && countdownTexts[i] != null)
            {
                countdownTexts[i].text = "";
            }
        }
    }

    // Called when one of the blocker buttons is pressed.
    public void OnBlockerButtonPressed(int buttonIndex)
    {
        // Retrieve the room code from the UI element.
        string displayedText = roomCodeText.text; // Example: "Room code: 123456"
        string roomCode = ExtractRoomCode(displayedText);
        if (string.IsNullOrEmpty(roomCode))
        {
            Debug.LogError("No room code available! Cannot set blocker" + (buttonIndex + 1));
            return;
        }

        // Build the Firebase path for the corresponding blocker (e.g., "blocker1", "blocker2", etc.)
        string blockerKey = "blocker" + (buttonIndex + 1);
        dbReference.Child("rooms").Child(roomCode).Child(blockerKey)
            .SetValueAsync(true).ContinueWithOnMainThread(task =>
            {
                if (task.IsCompleted)
                {
                    Debug.Log(blockerKey + " set to true in Firebase for room: " + roomCode);
                    // Start the cooldown for the button so it can't be used for 10 seconds.
                    StartCoroutine(StartCooldown(blockerButtons[buttonIndex], countdownTexts[buttonIndex]));
                }
                else
                {
                    Debug.LogError("Failed to set " + blockerKey + ": " + task.Exception);
                }
            });
    }

    // Helper method to extract the room code from a string like "Room code: 123456"
    private string ExtractRoomCode(string text)
    {
        string prefix = "Room code:";
        if (text.ToLower().StartsWith(prefix.ToLower()))
        {
            return text.Substring(prefix.Length).Trim();
        }
        return text.Trim();
    }

    // Coroutine that disables a button during the cooldown and updates its associated countdown text.
    private IEnumerator StartCooldown(Button button, TMP_Text countdownText)
    {
        if (button == null)
            yield break;

        // Create a new ColorBlock for the cooldown state with red for all states.
        ColorBlock cooldownColors = button.colors;
        cooldownColors.normalColor = Color.red;
        cooldownColors.highlightedColor = Color.red;
        cooldownColors.pressedColor = Color.red;
        cooldownColors.disabledColor = Color.red;
        button.colors = cooldownColors;

        button.interactable = false;
        float remainingTime = cooldownDuration;
        while (remainingTime > 0)
        {
            if (countdownText != null)
            {
                countdownText.text = Mathf.Ceil(remainingTime).ToString();
            }
            remainingTime -= Time.deltaTime;
            yield return null;
        }

        if (countdownText != null)
        {
            countdownText.text = "";
        }

        // When re-enabling, set the button's colors explicitly to black.
        ColorBlock activeColors = button.colors;
        activeColors.normalColor = Color.black;
        activeColors.highlightedColor = Color.black;
        activeColors.pressedColor = Color.black;
        // The disabledColor is not used when the button is interactable.
        button.colors = activeColors;
        button.interactable = true;
    }


}
