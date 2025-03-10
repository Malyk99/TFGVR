using System.Collections.Generic;
using UnityEngine;

public class MenuManager : MonoBehaviour
{
    // List of canvases that you want to manage.
    // Drag your canvas GameObjects into this list via the Inspector.
    public List<GameObject> canvases;

    // Tracks the currently active canvas index.
    private int currentIndex = 0;

    // On start, we make sure only the first canvas is active.
    private void Start()
    {
        if (canvases.Count > 0)
        {
            ShowCanvas(currentIndex);
        }
    }

    // Call this method to show the next canvas.
    // This method wraps around to the first canvas if at the end of the list.
    public void NextCanvas()
    {
        if (canvases.Count == 0) return;

        currentIndex = (currentIndex + 1) % canvases.Count;
        ShowCanvas(currentIndex);
    }

    // Call this method to show the previous canvas.
    // It wraps around to the last canvas if at the beginning.
    public void PreviousCanvas()
    {
        if (canvases.Count == 0) return;

        currentIndex = (currentIndex - 1 + canvases.Count) % canvases.Count;
        ShowCanvas(currentIndex);
    }

    // Call this method to display a specific canvas by its index.
    public void ShowCanvas(int index)
    {
        if (index < 0 || index >= canvases.Count)
        {
            Debug.LogWarning("Canvas index out of range.");
            return;
        }

        // Loop through each canvas and enable only the one at the specified index.
        for (int i = 0; i < canvases.Count; i++)
        {
            canvases[i].SetActive(i == index);
        }

        currentIndex = index;
    }
}
