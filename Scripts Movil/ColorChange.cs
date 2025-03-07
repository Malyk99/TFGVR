using UnityEngine;
using UnityEngine.UI;

public class colorChange : MonoBehaviour
{
    public Renderer objectRenderer; // Assign your object in Inspector

    public void ChangeObjectColor(string colorName)
    {
        switch (colorName)
        {
            case "Red":
                objectRenderer.material.color = Color.red;
                break;
            case "Blue":
                objectRenderer.material.color = Color.blue;
                break;
            case "Green":
                objectRenderer.material.color = Color.green;
                break;
            default:
                objectRenderer.material.color = Color.white;
                break;
        }
    }
}
