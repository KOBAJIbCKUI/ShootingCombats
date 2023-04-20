package org.shootingcombats.shootingcombats;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public final class SimpleLobbiesManager implements LobbiesManager {
    private final Set<Lobby> lobbies;

    public SimpleLobbiesManager() {
        this.lobbies = new LinkedHashSet<>();
    }

    @Override
    public boolean addLobby(Lobby lobby) {
        if (!containsLobby(lobby)) {
            lobbies.add(lobby);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeLobby(Lobby lobby) {
        if (containsLobby(lobby)) {
            lobbies.remove(lobby);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeLobby(String name) {
        if (containsLobby(name)) {
            lobbies.removeIf(lobby -> lobby.getName().equals(name));
            return true;
        }
        return false;
    }

    @Override
    public boolean containsLobby(Lobby lobby) {
        return lobbies.contains(lobby);
    }

    @Override
    public boolean containsLobby(String name) {
        return lobbies.stream().anyMatch(lobby -> lobby.getName().equals(name));
    }

    @Override
    public Collection<Lobby> getLobbies() {
        return Collections.unmodifiableSet(lobbies);
    }
}
