using UnityEngine;

public class PuzzleZone : MonoBehaviour
{
    public bool HasPuzzleInside { get; private set; }

    private void OnTriggerEnter(Collider other)
    {
        if (other.CompareTag("puzzle"))
        {
            HasPuzzleInside = true;
        }
    }

    private void OnTriggerExit(Collider other)
    {
        if (other.CompareTag("puzzle"))
        {
            HasPuzzleInside = false;
        }
    }
}
