using UnityEngine;
using Firebase;
using Firebase.Extensions;
using System.Collections.Generic;
using Firebase.Database;

public class TargetSpawner : MonoBehaviour
{
    [Header("Testing Config")]
    public string roomCode = "testRoom"; // Set this in Inspector for testing

    [Header("Spawning")]
    public GameObject cubePrefab; // Enabled target
    public GameObject spherePrefab; // Disabled target
    [SerializeField] private Transform[] spawnPoints = new Transform[9];


    private DatabaseReference minigameRef;

    private void Start()
    {
        FirebaseApp.CheckAndFixDependenciesAsync().ContinueWithOnMainThread(task => {
            var dependencyStatus = task.Result;

            if (dependencyStatus == DependencyStatus.Available)
            {
                Debug.Log("Firebase ready ✅");

                // Now safe to reference Firebase
                minigameRef = FirebaseDatabase.DefaultInstance
                    .GetReference("rooms")
                    .Child(roomCode)
                    .Child("minigames")
                    .Child("minigame3");

                LoadRoundTargets(1);
            }
            else
            {
                Debug.LogError("Could not resolve all Firebase dependencies: " + dependencyStatus);
            }
        });
    }


    public void LoadRoundTargets(int roundNumber)
    {
        ClearExistingTargets();

        minigameRef.Child("round" + roundNumber).Child("targetStates").GetValueAsync().ContinueWith(task =>
        {
            if (task.IsCompletedSuccessfully && task.Result != null)
            {
                DataSnapshot snapshot = task.Result;

                for (int i = 0; i < spawnPoints.Length; i++)
                {
                    string key = "btn" + i;
                    bool isActive = true;

                    if (snapshot.HasChild(key))
                    {
                        bool.TryParse(snapshot.Child(key).Value.ToString(), out isActive);
                    }

                    GameObject prefabToSpawn = isActive ? cubePrefab : spherePrefab;

                    if (spawnPoints[i] == null)
                    {
                        Debug.LogError($"SpawnPoint[{i}] is NULL!");
                    }
                    else
                    {
                        Debug.Log($"Spawning {(isActive ? "CUBE" : "SPHERE")} at index {i}, position: {spawnPoints[i].position}");
                        Instantiate(prefabToSpawn, spawnPoints[i].position, Quaternion.identity);
                    }
                }

            }
            else
            {
                Debug.LogWarning("Failed to load targetStates: " + task.Exception);
            }
        });
    }

    private void ClearExistingTargets()
    {
        foreach (Transform point in spawnPoints)
        {
            Collider[] colliders = Physics.OverlapSphere(point.position, 0.1f);
            foreach (var col in colliders)
            {
                Destroy(col.gameObject);
            }
        }
    }
}
