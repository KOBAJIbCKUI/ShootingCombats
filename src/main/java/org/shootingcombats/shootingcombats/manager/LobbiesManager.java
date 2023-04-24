package org.shootingcombats.shootingcombats.manager;

import org.shootingcombats.shootingcombats.lobby.Lobby;

import java.util.List;
import java.util.Optional;

public interface LobbiesManager {
    void addLobby(Lobby lobby);
    void removeLobby(Lobby lobby);
    void removeLobby(String name);
    boolean containsLobby(Lobby lobby);
    boolean containsLobby(String name);
    Optional<Lobby> getLobby(String name);
    List<Lobby> getLobbies();
}
