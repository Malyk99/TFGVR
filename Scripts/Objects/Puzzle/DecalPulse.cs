using UnityEngine;

public class DecalPulse : MonoBehaviour
{
    [Header("Pulse Settings")]
    public float pulseSpeed = 2f;
    public float scaleAmount = 0.2f;

    [Header("Flash Settings")]
    public float flashSpeed = 4f;
    public float minAlpha = 0.3f;
    public float maxAlpha = 1f;

    private Vector3 initialScale;
    private Material mat;
    private Color baseColor;

    private void Start()
    {
        initialScale = transform.localScale;

        Renderer renderer = GetComponent<Renderer>();
        if (renderer != null)
        {
            mat = renderer.material;
            baseColor = mat.color;
        }
        else
        {
            Debug.LogWarning("?? No Renderer found on decal!");
        }
    }

    private void Update()
    {
        // Pulse scale
        float scale = 1 + Mathf.Sin(Time.time * pulseSpeed) * scaleAmount;
        transform.localScale = initialScale * scale;

        // Flash alpha
        if (mat != null)
        {
            float alpha = Mathf.Lerp(minAlpha, maxAlpha, (Mathf.Sin(Time.time * flashSpeed) + 1f) / 2f);
            Color c = baseColor;
            c.a = alpha;
            mat.color = c;
        }
    }
}
