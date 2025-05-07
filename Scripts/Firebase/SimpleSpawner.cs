using UnityEngine;
using System.Collections;

public class SimpleSpawner : MonoBehaviour
{
    [Header("Spawner Setup")]
    public Transform[] spawnPoints = new Transform[9];
    public GameObject cubeBlue;
    public GameObject cubeRed;

    [Header("Audio")]
    public AudioClip spawnSound;
    public AudioSource audioSource;

    private GameObject[] spawnedTargets = new GameObject[9];

    public void SpawnTargets(bool[] targetStates)
    {
        if (targetStates.Length != 9)
        {
            Debug.LogError("? targetStates must have exactly 9 elements.");
            return;
        }

        ClearTargets();

        for (int i = 0; i < 9; i++)
        {
            if (spawnPoints[i] == null) continue;

            GameObject prefab = targetStates[i] ? cubeBlue : cubeRed;
            GameObject obj = Instantiate(prefab, spawnPoints[i].position, Quaternion.identity);
            obj.transform.localScale = Vector3.zero;

            spawnedTargets[i] = obj;
            StartCoroutine(AnimateGrow(obj.transform, 0.2f));

            if (audioSource && spawnSound)
                audioSource.PlayOneShot(spawnSound);
        }

        Debug.Log("? Targets spawned.");
    }

    private IEnumerator AnimateGrow(Transform target, float duration)
    {
        Vector3 finalScale = Vector3.one; // Default fallback

        // Try to get original prefab scale via instance
        if (target != null)
        {
            finalScale = target.localScale == Vector3.zero ? Vector3.one : target.localScale;
        }

        target.localScale = Vector3.zero;

        float elapsed = 0f;
        while (elapsed < duration)
        {
            float t = elapsed / duration;
            target.localScale = Vector3.Lerp(Vector3.zero, finalScale, t);
            elapsed += Time.deltaTime;
            yield return null;
        }

        target.localScale = finalScale;
    }


    public void ClearTargets()
    {
        for (int i = 0; i < spawnedTargets.Length; i++)
        {
            if (spawnedTargets[i] != null)
            {
                StartCoroutine(AnimateShrinkAndDestroy(spawnedTargets[i].transform, 0.2f));
                spawnedTargets[i] = null;
            }
        }

        Debug.Log("?? Targets animating out.");
    }

    private IEnumerator AnimateShrinkAndDestroy(Transform target, float duration)
    {
        Vector3 initialScale = target.localScale;
        float elapsed = 0f;

        while (elapsed < duration)
        {
            float t = elapsed / duration;
            target.localScale = Vector3.Lerp(initialScale, Vector3.zero, t);
            elapsed += Time.deltaTime;
            yield return null;
        }

        Destroy(target.gameObject);
    }
}
