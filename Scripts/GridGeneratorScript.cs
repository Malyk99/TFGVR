using UnityEngine;

public class GridGeneratorScript : MonoBehaviour
{
    [Header("Grid Settings")]
    public int rows = 3;
    public int columns = 3;
    public float spacing = 2f;
    public Vector3 startOffset = Vector3.zero;

    [Header("Gizmo Settings")]
    public Color gizmoColor = Color.green;
    public float gizmoSize = 0.3f;

    [ContextMenu("Generate Grid")]
    public void GenerateGrid()
    {
        ClearOldPoints();

        for (int y = 0; y < rows; y++)
        {
            for (int x = 0; x < columns; x++)
            {
                GameObject point = new GameObject($"SpawnPoint_{y}_{x}");
                point.transform.SetParent(transform);
                point.transform.localPosition = startOffset + new Vector3(x * spacing, -y * spacing, 0);
            }
        }

        Debug.Log("Grid generated.");
    }

    private void ClearOldPoints()
    {
        for (int i = transform.childCount - 1; i >= 0; i--)
        {
            DestroyImmediate(transform.GetChild(i).gameObject);
        }
    }

    private void OnDrawGizmos()
    {
        Gizmos.color = gizmoColor;

        foreach (Transform child in transform)
        {
            Gizmos.DrawSphere(child.position, gizmoSize);
        }
    }
}
