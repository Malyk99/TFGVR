using UnityEngine;
using Steamworks;
using System.Collections.Generic;

public class LobbiesListManager : MonoBehaviour
{
    public static LobbiesListManager Instance;

    // Lobbies List Variables
    public GameObject lobbiesMenu;
    public GameObject lobbyDataItemPrefab;
    public GameObject lobbyListContent;

    public GameObject lobbiesButton, hostButton;

    public List<GameObject> lobbyList = new List<GameObject>();

    private void Awake() 
    {
        if (Instance == null) { Instance = this; }
    }

    public void GetListOfLobbies()
    {
        lobbiesButton.SetActive(false);
        hostButton.SetActive(false);

        lobbiesMenu.SetActive(true);

        SteamLobby.Instance.GetLobbyList();
    }

    public void Back()
    {
        lobbiesButton.SetActive(true);
        hostButton.SetActive(true);

        lobbiesMenu.SetActive(false);
    }

    public void DisplayLobbies(List<CSteamID> lobbyIDs, LobbyDataUpdate_t result)
    {
        for (int i = 0; i < lobbyIDs.Count; i++)
        {
            if (lobbyIDs[i].m_SteamID == result.m_ulSteamIDLobby)
            {
                GameObject createdItem = Instantiate(lobbyDataItemPrefab);

                createdItem.GetComponent<LobbyDataEntry>().lobbyID = (CSteamID)lobbyIDs[i].m_SteamID;

                createdItem.GetComponent<LobbyDataEntry>().lobbyName =
                    SteamMatchmaking.GetLobbyData((CSteamID)lobbyIDs[i].m_SteamID, "name");

                createdItem.GetComponent<LobbyDataEntry>().SetLobbyData();

                createdItem.transform.SetParent(lobbyListContent.transform);
                createdItem.transform.localScale = Vector3.one;

                lobbyList.Add(createdItem);
            }
        }
    }

    public void DestroyLobbies()
    {
        foreach (GameObject item in lobbyList) { Destroy(item); }
        lobbyList.Clear();
    }
}
