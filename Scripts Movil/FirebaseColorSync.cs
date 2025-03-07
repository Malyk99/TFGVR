using UnityEngine;
using Firebase.Database;
using Firebase;
using System.Threading.Tasks;

public class FirebaseColorSync : MonoBehaviour
{
    private DatabaseReference dbReference;
    public Renderer objectRenderer; // Assign your object in Inspector

    void Start()
    {
        // Initialize Firebase Database Reference
        dbReference = FirebaseDatabase.DefaultInstance.RootReference;

        // Listen for color changes
        FirebaseDatabase.DefaultInstance
            .GetReference("color")
            .ValueChanged += OnColorChanged;
    }

    // Function to update color in Firebase
    public void ChangeColor(string color)
    {
        dbReference.Child("color").SetValueAsync(color);
    }

    // Function triggered when color value changes in Firebase
    private void OnColorChanged(object sender, ValueChangedEventArgs args)
    {
        if (args.Snapshot.Exists)
        {
            string newColor = args.Snapshot.Value.ToString();
            ApplyColor(newColor);
        }
    }

    // Function to change the object color
    private void ApplyColor(string colorName)
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

