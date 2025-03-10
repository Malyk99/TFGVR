using UnityEngine;
using UnityEngine.UI;
using Firebase.Database;
using Firebase;
using System.Collections.Generic;

public class AdminManager : MonoBehaviour
{
    public GameObject roomButtonPrefab; // Assign a Button prefab in Unity
    public Transform roomListContainer; // Assign the ScrollView Content in Unity
    private DatabaseReference dbReference;

    void Start()
    {
        // Initialize Firebase Database Reference
        dbReference = FirebaseDatabase.DefaultInstance.RootReference;
        LoadAllRooms(); // Load rooms when the scene starts
    }

    public void LoadAllRooms()
    {
        dbReference.Child("rooms").GetValueAsync().ContinueWith(task =>
        {
            if (task.IsCompleted)
            {
                DataSnapshot snapshot = task.Result;

                // Clear old buttons before refreshing list
                foreach (Transform child in roomListContainer)
                {
                    Destroy(child.gameObject);
                }

                foreach (DataSnapshot room in snapshot.Children)
                {
                    string roomId = room.Key;
                    CreateRoomButton(roomId);
                }
            }
            else
            {
                Debug.LogError("Failed to load rooms: " + task.Exception);
            }
        });
    }

    void CreateRoomButton(string roomId)
    {
        GameObject newButton = Instantiate(roomButtonPrefab, roomListContainer);
        newButton.GetComponentInChildren<Text>().text = roomId;

        // Add click event to delete the room
        newButton.GetComponent<Button>().onClick.AddListener(() => DeleteSpecificRoom(roomId));
    }

    public void DeleteSpecificRoom(string roomId)
    {
        dbReference.Child("rooms").Child(roomId).RemoveValueAsync().ContinueWith(task =>
        {
            if (task.IsCompleted)
            {
                Debug.Log("Deleted room: " + roomId);
                LoadAllRooms(); // Refresh list after deletion
            }
            else
            {
                Debug.LogError("Failed to delete room: " + task.Exception);
            }
        });
    }

    public void DeleteAllRooms()
    {
        dbReference.Child("rooms").RemoveValueAsync().ContinueWith(task =>
        {
            if (task.IsCompleted)
            {
                Debug.Log("All rooms have been deleted.");
                LoadAllRooms(); // Refresh list
            }
            else
            {
                Debug.LogError("Failed to delete rooms: " + task.Exception);
            }
        });
    }
}
