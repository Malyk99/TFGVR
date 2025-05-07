using UnityEngine;
using Firebase;
using Firebase.Database;
using Firebase.Extensions;
using System.Collections;

public class FirebaseRoundManager : MonoBehaviour
{
    public Collider triggerZone;
    public int totalRounds = 4;

    [Header("Audio")]
    public AudioClip roundEndSound;
    public AudioClip slowMoSound;
    public AudioSource audioSource;

    private string roomCode;
    private DatabaseReference dbRef;
    private bool roundInProgress = false;
    private bool triggerActivated = false;
    private SimpleSpawner spawner;

    private void Start()
    {
        spawner = FindObjectOfType<SimpleSpawner>();
        dbRef = FirebaseDatabase.DefaultInstance.RootReference;

        FirebaseApp.CheckAndFixDependenciesAsync().ContinueWithOnMainThread(task =>
        {
            if (task.Result == DependencyStatus.Available)
            {
                Debug.Log("? Firebase ready.");
                StartCoroutine(WaitForRoomCode());
            }
            else
            {
                Debug.LogError("? Firebase init failed: " + task.Result);
            }
        });
    }

    private IEnumerator WaitForRoomCode()
    {
        while (string.IsNullOrEmpty(RoomManagerCreate.CurrentRoomCode))
        {
            Debug.Log("? Waiting for room code...");
            yield return null;
        }

        roomCode = RoomManagerCreate.CurrentRoomCode;
        Debug.Log("?? Room code acquired: " + roomCode);
    }

    private void OnTriggerEnter(Collider other)
    {
        if (triggerActivated || roundInProgress) return;

        if (other.CompareTag("Player"))
        {
            Debug.Log("?? Player entered trigger zone, starting rounds...");
            triggerActivated = true;
            StartCoroutine(HandleRounds());
        }
    }

    private IEnumerator HandleRounds()
    {
        roundInProgress = true;

        // Notify Android clients
        SetFirebaseRoundStart(true);

        for (int round = 1; round <= totalRounds; round++)
        {
            string roundKey = "round" + round;

            Debug.Log($"?? Waiting 5s before round {round}...");
            yield return new WaitForSeconds(5f);

            bool[] targetStates = new bool[9];
            yield return StartCoroutine(FetchTargetStates(roundKey, targetStates));

            spawner.SpawnTargets(targetStates);

            Debug.Log($"?? Round {round}: active phase (10s)");
            yield return new WaitForSeconds(9f);

            // ?? Final slow-motion second
            TriggerSlowMotion();

            if (audioSource && slowMoSound)
                audioSource.PlayOneShot(slowMoSound);

            yield return new WaitForSecondsRealtime(1f);
            ResetTimeScale();

            spawner.ClearTargets();

            if (audioSource && roundEndSound)
                audioSource.PlayOneShot(roundEndSound);
        }

        Debug.Log("? All rounds complete.");
        SetFirebaseRoundStart(false);
        roundInProgress = false;
    }

    private IEnumerator FetchTargetStates(string roundKey, bool[] result)
    {
        var roundRef = dbRef
            .Child("rooms")
            .Child(roomCode)
            .Child("minigames")
            .Child("minigame3")
            .Child(roundKey)
            .Child("targetStates");

        var task = roundRef.GetValueAsync();
        yield return new WaitUntil(() => task.IsCompleted);

        if (task.Exception != null)
        {
            Debug.LogError("?? Failed to fetch targetStates: " + task.Exception);
            yield break;
        }

        DataSnapshot snapshot = task.Result;
        for (int i = 0; i < 9; i++)
        {
            string key = "btn" + i;
            result[i] = true;

            if (snapshot.HasChild(key))
                bool.TryParse(snapshot.Child(key).Value.ToString(), out result[i]);
        }
    }

    private void SetFirebaseRoundStart(bool value)
    {
        dbRef.Child("rooms")
             .Child(roomCode)
             .Child("gameState")
             .Child("roundStart")
             .SetValueAsync(value);
    }

    private void TriggerSlowMotion()
    {
        Time.timeScale = 0.25f;
        Time.fixedDeltaTime = 0.02f * Time.timeScale;
        Debug.Log("?? Slow motion triggered!");
    }

    private void ResetTimeScale()
    {
        Time.timeScale = 1f;
        Time.fixedDeltaTime = 0.02f;
        Debug.Log("?? Time scale reset.");
    }
}
