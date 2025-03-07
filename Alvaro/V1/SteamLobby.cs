using UnityEngine;
using Mirror;
using Steamworks;
using System.Collections.Generic;
using UnityEngine.UI;
using UnityEngine.SceneManagement;

public class SteamLobby : MonoBehaviour
{
    public static SteamLobby Instance;

    // Callbacks
    protected Callback<LobbyCreated_t> LobbyCreated;
    protected Callback<GameLobbyJoinRequested_t> JoinRequest;
    protected Callback<LobbyEnter_t> LobbyEntered;

    // Lobby Callbacks
    protected Callback<LobbyMatchList_t> LobbyList;
    protected Callback<LobbyDataUpdate_t> LobbyDataUpdated;

    public List<CSteamID> lobbyIDs = new List<CSteamID>();

    // Variables
    public ulong CurrentLobbyID;
    private const string HostAddressKey = "HostAddress";
    private CustomNetworkManager manager;

    private void Awake()
    {
        DontDestroyOnLoad(gameObject);
    }

    private void Start()
    {
        if (!SteamManager.Initialized) { return; }

        manager = GetComponent<CustomNetworkManager>();
        if (Instance == null) { Instance = this; }

        LobbyCreated = Callback<LobbyCreated_t>.Create(OnLobbyCreated);
        JoinRequest = Callback<GameLobbyJoinRequested_t>.Create(OnJoinRequest);
        LobbyEntered = Callback<LobbyEnter_t>.Create(OnLobbyEntered);

        LobbyList = Callback<LobbyMatchList_t>.Create(OnGetLobbyList);
        LobbyDataUpdated = Callback<LobbyDataUpdate_t>.Create(OnGetLobbyData);
    }

    public void HostLobby()
    {
        Debug.Log("Attempting to Host a new Lobby");

        SteamMatchmaking.CreateLobby(ELobbyType.k_ELobbyTypePublic, manager.maxConnections);
    }

    private void OnLobbyCreated(LobbyCreated_t callback)
    {
        if(callback.m_eResult != EResult.k_EResultOK) { return; }

        Debug.Log("Lobby created succesfully");

        manager.StartHost();

        SteamMatchmaking.SetLobbyData(new CSteamID(callback.m_ulSteamIDLobby), HostAddressKey, SteamUser.GetSteamID().ToString());
        SteamMatchmaking.SetLobbyData(new CSteamID(callback.m_ulSteamIDLobby), "name", SteamFriends.GetPersonaName().ToString());
    }

    private void OnJoinRequest(GameLobbyJoinRequested_t callback)
    {
        Debug.Log("Request to join lobby");
        SteamMatchmaking.JoinLobby(callback.m_steamIDLobby);
    }

    private void OnLobbyEntered(LobbyEnter_t callback)
    {
        // Everyone
        CurrentLobbyID = callback.m_ulSteamIDLobby;

        // Clients
        if(NetworkServer.active) { return; }

        manager.networkAddress = SteamMatchmaking.GetLobbyData(new CSteamID(callback.m_ulSteamIDLobby), HostAddressKey);

        manager.StartClient();
    }

    public void JoinLobby(CSteamID lobbyID)
    {
        SteamMatchmaking.JoinLobby(lobbyID);
    }

    public void GetLobbyList()
    {
        if (lobbyIDs.Count > 0) { lobbyIDs.Clear(); }

        SteamMatchmaking.AddRequestLobbyListResultCountFilter(60);
        SteamMatchmaking.RequestLobbyList();
    }

    void OnGetLobbyList(LobbyMatchList_t result)
    {
        if (LobbiesListManager.Instance.lobbyList.Count > 0) { LobbiesListManager.Instance.DestroyLobbies(); }

        for(int i = 0; i < result.m_nLobbiesMatching; i++)
        {
            CSteamID lobbyID = SteamMatchmaking.GetLobbyByIndex(i);
            lobbyIDs.Add(lobbyID);
            SteamMatchmaking.RequestLobbyData(lobbyID);
        }
    }

    void OnGetLobbyData(LobbyDataUpdate_t result)
    {
        LobbiesListManager.Instance.DisplayLobbies(lobbyIDs, result);
    }
}
