using UnityEngine;
using Firebase.Database;
using Firebase.Extensions;
using System.Collections;

public class Minigame2PositionListener : MonoBehaviour
{
    public GameObject movableObject;
    public Transform leftEnd;
    public Transform rightEnd;

    private DatabaseReference dbReference;
    private string roomCode;

    private float minValue = -100f;
    private float maxValue = 100f;
    private bool listenerSet = false;

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

        if (movableObject == null || leftEnd == null || rightEnd == null)
        {
            Debug.LogError("Missing references! Assign movableObject, leftEnd, and rightEnd.");
            yield break;
        }


        if (!listenerSet)
        {
            listenerSet = true;
            ListenToPosition();
        }
    }

    void ListenToPosition()
    {
        var posRef = dbReference
            .Child("rooms")
            .Child(roomCode)
            .Child("minigames")
            .Child("minigame2")
            .Child("position");

        posRef.ValueChanged += OnPositionChanged;
    }

    void OnPositionChanged(object sender, ValueChangedEventArgs args)
    {
        if (args.DatabaseError != null)
        {
            Debug.LogError("Firebase error: " + args.DatabaseError.Message);
            return;
        }

        if (args.Snapshot != null && args.Snapshot.Value != null)
        {
            Debug.Log("Raw Firebase value: " + args.Snapshot.Value);

            float rawPosition;
            if (float.TryParse(args.Snapshot.Value.ToString(), out rawPosition))
            {
                float t = Mathf.InverseLerp(minValue, maxValue, rawPosition);
                Vector3 newPosition = Vector3.Lerp(leftEnd.position, rightEnd.position, t);

                Debug.Log($"Parsed value: {rawPosition} ? t: {t:F2} ? new X: {newPosition.x:F2}");

                if (movableObject != null)
                {
                    movableObject.transform.position = new Vector3(
                        newPosition.x,
                        movableObject.transform.position.y,
                        movableObject.transform.position.z
                    );
                }
            }
            else
            {
                Debug.LogError("Couldn't parse Firebase value to float: " + args.Snapshot.Value);
            }
        }
        else
        {
            Debug.LogWarning("Snapshot or value is null.");
        }
    }
}
