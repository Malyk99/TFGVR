using UnityEngine;

public class PieceCollisionManager1 : MonoBehaviour
{
    private void OnTriggerEnter(Collider other)
    {
        if (other.tag == "p1")
        {
            PuzzleController.Instance.AddCorrectPiece();
        }
    }
    
    private void OnTriggerExit(Collider other)
    {
        if (other.tag == "p1")
        {
            PuzzleController.Instance.SubCorrectPiece();
        }
    }
}
