using UnityEngine;

public class PieceCollisionManager4 : MonoBehaviour
{
    private void OnTriggerEnter(Collider other)
    {
        if (other.tag == "p4")
        {
            PuzzleController.Instance.AddCorrectPiece();
        }
    }
    
    private void OnTriggerExit(Collider other)
    {
        if (other.tag == "p4")
        {
            PuzzleController.Instance.SubCorrectPiece();
        }
    }
}
