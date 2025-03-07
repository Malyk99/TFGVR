using UnityEngine;
using Mirror;
using Steamworks;

public class PlayerObjectController : NetworkBehaviour
{
    // Player data
    [SyncVar] public int ConnectionID;
    [SyncVar] public int PlayerIdNumber;
    [SyncVar] public ulong PlayerSteamID;
    [SyncVar(hook = nameof(PlayerNameUpdate))] public string PlayerName;
    [SyncVar(hook = nameof(PlayerReadyUpdate))] public bool Ready;

    private CustomNetworkManager manager;

    private CustomNetworkManager Manager
    {
        get
        {
            if (manager != null) { return manager; }

            return manager = CustomNetworkManager.singleton as CustomNetworkManager;
        }
    }

    private void Start()
    {
        DontDestroyOnLoad(this.gameObject);
    }

    private void PlayerReadyUpdate(bool OldValue, bool NewValue)
    {
        if (isServer)
        {
            this.Ready = NewValue;
        }

        if (isClient)
        {
            if (LobbyController.Instance != null)
            {
                LobbyController.Instance.UpdatePlayerList();
            }
        }
    }

    [Command]
    private void CmdSetPlayerReady()
    {
        this.Ready = !this.Ready;
    }

    public void ChangeReady()
    {
        if (isOwned)
        {
            CmdSetPlayerReady();
        }
    }

    public override void OnStartAuthority()
    {
        CommandSetPlayerName(SteamFriends.GetPersonaName());
        gameObject.name = "LocalGamePlayer";

        if (LobbyController.Instance != null)
        {
            LobbyController.Instance.FindLocalPlayer();
            LobbyController.Instance.UpdateLobbyName();
        }
    }

    public override void OnStartClient()
    {
        if (Manager != null)
        {
            Manager.GamePlayers.Add(this);
        }

        if (LobbyController.Instance != null)
        {
            LobbyController.Instance.UpdateLobbyName();
            LobbyController.Instance.UpdatePlayerList();
        }
    }

    public override void OnStopClient()
    {
        if (Manager != null)
        {
            Manager.GamePlayers.Remove(this);
        }

        if (LobbyController.Instance != null)
        {
            LobbyController.Instance.UpdatePlayerList();
        }
    }

    [Command]
    private void CommandSetPlayerName(string playerName)
    {
        this.PlayerName = playerName;
    }


    public void PlayerNameUpdate(string OldValue, string NewValue)
    {
        if (isServer) // Host
        {
            this.PlayerName = NewValue;
        }

        if (isClient) // Client
        {
            if (LobbyController.Instance != null)
            {
                LobbyController.Instance.UpdatePlayerList();
            }
        }
    }

    // Start Game

    public void CanStartGame(string SceneName)
    {
        if (isOwned)
        {
            CmdCanStartGame(SceneName);
        }
    }

    [Command]
    public void CmdCanStartGame(string SceneName)
    {
        manager.StartGame(SceneName);
    }
}
