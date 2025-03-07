using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.UI;

public class SceneLoader : MonoBehaviour
{
    // UI
    public Button HostButon, LobbiesButton;
    public GameObject LoadScreen;

    public void RedirectToLobby()
    {
        HostButon.gameObject.SetActive(false);
        LobbiesButton.gameObject.SetActive(false);
        LoadScreen.SetActive(true);

        SteamLobby.Instance.HostLobby();
    }
}
