using UnityEngine;
using Firebase.Database;
using Firebase;
using System.Collections;

public class FirebasePositionSync : MonoBehaviour
{
    private DatabaseReference dbReference;
    public Transform objectToMove; // Assign your moving object
    public Transform referencePoint; // Empty GameObject as a fixed reference
    public float moveSpeed = 5f; // Base speed
    private bool isMoving = false; // Prevents overlapping movements

    void Start()
    {
        dbReference = FirebaseDatabase.DefaultInstance.RootReference;

        FirebaseDatabase.DefaultInstance
            .GetReference("position")
            .ValueChanged += OnPositionChanged;
    }

    // Methods for UI Buttons (Only Moves on X-Axis)
    public void MoveRight()
    {
        if (!isMoving) SetNewPosition(referencePoint.position.x + 2f);
    }

    public void MoveLeft()
    {
        if (!isMoving) SetNewPosition(referencePoint.position.x - 2f);
    }

    // Function to update position in Firebase
    private void SetNewPosition(float newX)
    {
        dbReference.Child("position").SetValueAsync(newX.ToString()); // Store only X
    }

    // Triggered when position updates in Firebase
    private void OnPositionChanged(object sender, ValueChangedEventArgs args)
    {
        if (args.Snapshot.Exists && !isMoving) // Only move if not already moving
        {
            float newX = float.Parse(args.Snapshot.Value.ToString());
            Vector3 targetPosition = new Vector3(newX, referencePoint.position.y, referencePoint.position.z);

            StartCoroutine(MoveObjectSmoothly(targetPosition));
        }
    }

    // Coroutine to move object smoothly with fast slowdown
    IEnumerator MoveObjectSmoothly(Vector3 targetPosition)
    {
        isMoving = true; // Lock movement

        while (Vector3.Distance(objectToMove.position, targetPosition) > 0.01f)
        {
            float distance = Vector3.Distance(objectToMove.position, targetPosition);
            float dynamicSpeed = Mathf.Clamp(moveSpeed * (distance / 2f), 10f, moveSpeed); // Faster slowdown

            objectToMove.position = Vector3.Lerp(objectToMove.position, targetPosition, dynamicSpeed * Time.deltaTime);
            yield return null; // Wait for next frame
        }

        objectToMove.position = targetPosition; // Ensure exact position
        isMoving = false; // Unlock movement
    }
}
