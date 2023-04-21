package org.shootingcombats.shootingcombats.data;

import org.shootingcombats.shootingcombats.lobby.Lobby;

public abstract class LobbyData {
    private final Lobby lobby;

    public LobbyData(Lobby lobby) {
        this.lobby = lobby;
    }

    public Lobby getLobby() {
        return this.lobby;
    }
}
