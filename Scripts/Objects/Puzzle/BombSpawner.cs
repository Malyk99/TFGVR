using UnityEngine;
using Firebase;
using Firebase.Database;
using Firebase.Extensions;
using System.Collections;

public class FirebaseBombSpawner : MonoBehaviour
{
    [Header("Spawner Settings")]
    public GameObject bombPrefab;
    public GameObject warningDecalPrefab;
    public Vector2 areaSize = new Vector2(10f, 10f);
    public float spawnHeight = 10f;
    public Collider triggerZone;

    private string roomCode;
    private DatabaseReference dbRef;
    private bool spawnListenerSet = false;
    private bool triggered = false;

    private void Start()
    {
        dbRef = FirebaseDatabase.DefaultInstance.RootReference;

        FirebaseApp.CheckAndFixDependenciesAsync().ContinueWithOnMainThread(task =>
        {
            if (task.Result == DependencyStatus.Available)
            {
                StartCoroutine(WaitForRoomCode());
            }
            else
            {
                Debug.LogError("? Firebase not ready: " + task.Result);
            }
        });
    }

    private IEnumerator WaitForRoomCode()
    {
        while (string.IsNullOrEmpty(RoomManagerCreate.CurrentRoomCode))
        {
            yield return null;
        }

        roomCode = RoomManagerCreate.CurrentRoomCode;
    }

    private void OnTriggerEnter(Collider other)
    {
        if (triggered || string.IsNullOrEmpty(roomCode)) return;
        if (!other.CompareTag("Player")) return;

        triggered = true;

        

        StartListeningForSpawn();
    }

    private void StartListeningForSpawn()
    {
        if (spawnListenerSet) return;
        spawnListenerSet = true;

        var spawnRef = dbRef.Child("rooms").Child(roomCode).Child("minigames").Child("minigame4").Child("spawnBomb");

        Debug.Log("?? Firebase listener attached to: rooms/" + roomCode + "/minigames/minigame4/spawnBomb");

        // Live listener
        spawnRef.ValueChanged += (sender, args) =>
        {
            Debug.Log("?? spawnBomb ValueChanged triggered!");

            if (args.DatabaseError != null)
            {
                Debug.LogError("?? Firebase error: " + args.DatabaseError.Message);
                return;
            }

            string val = args.Snapshot?.Value?.ToString();
            Debug.Log("?? spawnBomb value received: " + val);

            if (!string.IsNullOrEmpty(val) && val.ToLower() == "true")
            {
                Debug.Log("?? Bomb triggered via value change!");
                SpawnBomb();
                spawnRef.SetValueAsync(false);
            }
        };

        // Immediate value check
        spawnRef.GetValueAsync().ContinueWithOnMainThread(task =>
        {
            if (task.IsCompletedSuccessfully && task.Result?.Value?.ToString() == "true")
            {
                Debug.Log("?? spawnBomb already true on init — spawning bomb!");
                SpawnBomb();
                spawnRef.SetValueAsync(false);
            }
            else
            {
                Debug.Log("?? Initial spawnBomb value: " + task.Result?.Value);
            }
        });
    }

    private void SpawnBomb()
    {
        if (bombPrefab == null)
        {
            Debug.LogError("? bombPrefab not assigned!");
            return;
        }

        Vector3 center = transform.position;

        float randX = Random.Range(-areaSize.x / 2f, areaSize.x / 2f);
        float randZ = Random.Range(-areaSize.y / 2f, areaSize.y / 2f);

        Vector3 spawnPos = new Vector3(center.x + randX, center.y + spawnHeight, center.z + randZ);

        Debug.Log($"?? Attempting to spawn bomb at: {spawnPos} (center={center}, height={spawnHeight})");

        GameObject bomb = Instantiate(bombPrefab, spawnPos, Quaternion.identity);

        if (warningDecalPrefab != null)
        {
            Vector3 groundPos = new Vector3(spawnPos.x, center.y, spawnPos.z);

            GameObject decal = Instantiate(warningDecalPrefab, groundPos, warningDecalPrefab.transform.rotation);
            decal.transform.localScale = warningDecalPrefab.transform.localScale; // preserve prefab scale

            Destroy(decal, 2f);
        }


        if (bomb != null)
            Debug.Log("? Bomb instantiated successfully!");
        else
            Debug.LogError("?? Instantiate returned null!");
    }

    private void OnDrawGizmosSelected()
    {
        Gizmos.color = new Color(1f, 0.5f, 0f, 0.4f);
        Vector3 size = new Vector3(areaSize.x, 0.1f, areaSize.y);
        Gizmos.DrawCube(transform.position + Vector3.up * spawnHeight, size);
        Gizmos.color = Color.red;
        Gizmos.DrawWireCube(transform.position + Vector3.up * spawnHeight, size);
    }
}
