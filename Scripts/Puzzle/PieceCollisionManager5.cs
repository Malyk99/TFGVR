using UnityEngine;

public class PieceCollisionManager5 : MonoBehaviour
{
    private void OnTriggerEnter(Collider other)
    {
        if (other.tag == "p5")
        {
            PuzzleController.Instance.AddCorrectPiece();
        }
    }
    
    private void OnTriggerExit(Collider other)
    {
        if (other.tag == "p5")
        {
            PuzzleController.Instance.SubCorrectPiece();
        }
    }
}
