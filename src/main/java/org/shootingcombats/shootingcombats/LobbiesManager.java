package org.shootingcombats.shootingcombats;

import java.util.Collection;

public interface LobbiesManager {
    boolean addLobby(Lobby lobby);
    boolean removeLobby(Lobby lobby);
    boolean removeLobby(String name);
    boolean containsLobby(Lobby lobby);
    boolean containsLobby(String name);
    Collection<Lobby> getLobbies();
}
