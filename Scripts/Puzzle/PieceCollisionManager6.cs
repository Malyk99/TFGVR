using UnityEngine;

public class PieceCollisionManager6 : MonoBehaviour
{
    private void OnTriggerEnter(Collider other)
    {
        if (other.tag == "p6")
        {
            PuzzleController.Instance.AddCorrectPiece();
        }
    }
    
    private void OnTriggerExit(Collider other)
    {
        if (other.tag == "p6")
        {
            PuzzleController.Instance.SubCorrectPiece();
        }
    }
}
