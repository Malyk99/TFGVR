using UnityEngine;

public class PieceCollisionManager2 : MonoBehaviour
{
    private void OnTriggerEnter(Collider other)
    {
        if (other.tag == "p2")
        {
            PuzzleController.Instance.AddCorrectPiece();
        }
    }
    
    private void OnTriggerExit(Collider other)
    {
        if (other.tag == "p2")
        {
            PuzzleController.Instance.SubCorrectPiece();
        }
    }
}
