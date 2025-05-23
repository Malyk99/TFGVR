using UnityEngine;

public class DustTrigger : MonoBehaviour
{
    private ParticleSystem dust;

    void Start()
    {
        dust = GetComponentInChildren<ParticleSystem>();
    }

    void OnTriggerEnter(Collider other)
    {
        if (other.CompareTag("Player"))
        {
            dust.Play();
        }
    }

    void OnTriggerExit(Collider other)
    {
        if (other.CompareTag("Player"))
        {
            dust.Stop();
        }
    }
}
