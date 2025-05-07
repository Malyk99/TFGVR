using UnityEngine;

public class TimedExplosion : MonoBehaviour
{
    [Header("Explosion Settings")]
    public float delay = 2f;
    public float explosionRadius = 5f;
    public float explosionForce = 500f;
    public GameObject explosionEffectPrefab;

    [Header("Audio")]
    public AudioClip explosionSound;
    public AudioSource audioSource; // Optional: if left null, PlayClipAtPoint is used

    [Header("Audio Variation")]
    public float pitchMin = 0.9f;
    public float pitchMax = 1.1f;

    private void Start()
    {
        PlayExplosionSound(); // ?? Plays immediately
        Invoke(nameof(Explode), delay); // ?? Physics explosion comes later
    }

    private void PlayExplosionSound()
    {
        if (explosionSound == null) return;

        float randomPitch = Random.Range(pitchMin, pitchMax);

        if (audioSource != null)
        {
            audioSource.pitch = randomPitch;
            audioSource.PlayOneShot(explosionSound);
            Debug.Log("? Played explosion sound via AudioSource.");
        }
        else
        {
            Debug.Log("?? Playing explosion sound via PlayClipAtPoint");
            GameObject tempGO = new GameObject("TempExplosionAudio");
            tempGO.transform.position = transform.position;
            AudioSource tempAS = tempGO.AddComponent<AudioSource>();
            tempAS.clip = explosionSound;
            tempAS.pitch = randomPitch;
            tempAS.spatialBlend = 0f;
            tempAS.Play();
            Destroy(tempGO, explosionSound.length / randomPitch);
        }
    }

    private void Explode()
    {
        Debug.Log("?? Explosion triggered!");

        if (explosionEffectPrefab)
        {
            GameObject vfx = Instantiate(explosionEffectPrefab, transform.position, Quaternion.identity);
            Destroy(vfx, 2f);
        }

        Collider[] colliders = Physics.OverlapSphere(transform.position, explosionRadius);
        foreach (Collider nearby in colliders)
        {
            Rigidbody rb = nearby.attachedRigidbody;
            if (rb != null && rb != GetComponent<Rigidbody>())
            {
                rb.AddExplosionForce(explosionForce, transform.position, explosionRadius);
            }
        }

        Destroy(gameObject);
    }

    private void OnDrawGizmosSelected()
    {
        Gizmos.color = Color.red;
        Gizmos.DrawWireSphere(transform.position, explosionRadius);
    }
}
