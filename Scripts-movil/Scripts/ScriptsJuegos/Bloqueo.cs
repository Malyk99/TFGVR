using UnityEngine;
using Firebase.Database;
using Firebase.Extensions;
using System.Collections;

public class Bloqueo : MonoBehaviour
{
    public GameObject[] targetObjects; // Array of objects to show
    public bool activateListener = false; // Control when to activate the listener

    private DatabaseReference dbReference;
    private string roomCode;
    private bool listenerActivated = false; // Prevent multiple activations

    void Start()
    {
        dbReference = FirebaseDatabase.DefaultInstance.RootReference;
        roomCode = RoomManager.CurrentRoomCode; // Get room code from RoomManager

        // Ensure all objects are initially hidden
        foreach (GameObject obj in targetObjects)
        {
            obj.SetActive(false);
        }
    }

    void Update()
    {
        // Wait until activateListener is set to true
        if (!listenerActivated && activateListener)
        {
            ActivateListeners();
            listenerActivated = true;
        }
    }

    void ActivateListeners()
    {
        // Listen for changes in Firebase for each button trigger
        for (int i = 0; i < targetObjects.Length; i++)
        {
            int index = i; // Capture index for delegate
            dbReference.Child("rooms").Child(roomCode).Child("trigger").Child("button" + index).ValueChanged += (sender, args) => HandleTriggerChange(args, index);
        }

        Debug.Log("Firebase listeners activated!");
    }

    void HandleTriggerChange(ValueChangedEventArgs args, int index)
    {
        if (args.Snapshot == null || args.Snapshot.Value == null) return;

        bool isActive = bool.Parse(args.Snapshot.Value.ToString());
        if (isActive)
        {
            StartCoroutine(ShowObjectTemporarily(index));
        }
    }

    IEnumerator ShowObjectTemporarily(int index)
    {
        targetObjects[index].SetActive(true);
        yield return new WaitForSeconds(2);
        targetObjects[index].SetActive(false);
    }
}
