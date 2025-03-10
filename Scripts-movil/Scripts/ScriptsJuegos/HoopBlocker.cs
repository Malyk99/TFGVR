using UnityEngine;
using TMPro;
using UnityEngine.UI;
using Firebase.Database;
using Firebase.Extensions;
using System.Collections;

public class HoopBlocker : MonoBehaviour
{
    public Button[] actionButtons; // Array de botones
    public TMP_Text[] buttonTexts; // Texto de los botones

    private DatabaseReference dbReference;
    private bool[] canPressButton;
    private string roomCode;

    void Start()
    {
        dbReference = FirebaseDatabase.DefaultInstance.RootReference;
        roomCode = RoomManager.CurrentRoomCode; // Obtener código de sala

        /*if (string.IsNullOrEmpty(roomCode))
        {
            Debug.LogError("Error: Room code no está asignado.");
            return;
        }*/

        canPressButton = new bool[actionButtons.Length];
        for (int i = 0; i < actionButtons.Length; i++)
        {
            canPressButton[i] = true;
            int index = i; // Capturar índice correctamente
            actionButtons[i].onClick.AddListener(() => TriggerObject(index));
        }
    }

    void TriggerObject(int buttonIndex)
    {
        if (!canPressButton[buttonIndex]) return; // Prevenir spam

        canPressButton[buttonIndex] = false;
        actionButtons[buttonIndex].interactable = false;
        actionButtons[buttonIndex].image.color = Color.yellow; // Indicar espera
        buttonTexts[buttonIndex].text = "Sending...";

        // Enviar actualización a Firebase
        string buttonPath = $"rooms/{roomCode}/trigger/button{buttonIndex}";
        Debug.Log($"Enviando a Firebase: {buttonPath}");
        
            string keyPath = "rooms/" + roomCode + "/blocker" + buttonIndex;
            Debug.Log("Firebase key path: " + keyPath);
            dbReference.Child("rooms").Child(roomCode).Child("blocker" + buttonIndex)
                .SetValueAsync(true).ContinueWithOnMainThread(task =>
                {
                    // ...
            });


        dbReference.Child("rooms").Child(roomCode).Child("trigger").Child($"button{buttonIndex}")
            .SetValueAsync(true).ContinueWithOnMainThread(task =>
            {
                if (task.IsFaulted || task.IsCanceled)
                {
                    Debug.LogError($"Error enviando a Firebase: {task.Exception}");
                }
                else
                {
                    Debug.Log($"Botón {buttonIndex} activado en Firebase.");
                    StartCoroutine(ResetDatabase(buttonIndex));
                    StartCoroutine(ResetButtonCooldown(buttonIndex));
                }
            });
    }

    IEnumerator ResetDatabase(int buttonIndex)
    {
        yield return new WaitForSeconds(3);
        dbReference.Child("rooms").Child(roomCode).Child("trigger").Child($"button{buttonIndex}")
            .SetValueAsync(false).ContinueWithOnMainThread(task =>
            {
                if (task.IsFaulted || task.IsCanceled)
                {
                    Debug.LogError($"Error reseteando en Firebase: {task.Exception}");
                }
                else
                {
                    Debug.Log($"Botón {buttonIndex} reseteado en Firebase.");
                }
            });
    }

    IEnumerator ResetButtonCooldown(int buttonIndex)
    {
        yield return new WaitForSeconds(10);
        canPressButton[buttonIndex] = true;
        actionButtons[buttonIndex].interactable = true;
        actionButtons[buttonIndex].image.color = Color.white;
        buttonTexts[buttonIndex].text = "Press Me!";
    }
}
