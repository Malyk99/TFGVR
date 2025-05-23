using UnityEngine;

public class PieceCollisionManager3 : MonoBehaviour
{
    private void OnTriggerEnter(Collider other)
    {
        if (other.tag == "p3")
        {
            PuzzleController.Instance.AddCorrectPiece();
        }
    }
    
    private void OnTriggerExit(Collider other)
    {
        if (other.tag == "p3")
        {
            PuzzleController.Instance.SubCorrectPiece();
        }
    }
}
