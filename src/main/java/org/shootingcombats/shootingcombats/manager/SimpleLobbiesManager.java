package org.shootingcombats.shootingcombats.manager;

import org.shootingcombats.shootingcombats.lobby.Lobby;

import java.util.*;

public final class SimpleLobbiesManager implements LobbiesManager {
    private final Set<Lobby> lobbies;

    public SimpleLobbiesManager() {
        this.lobbies = new TreeSet<>(Comparator.comparing(Lobby::getName));
    }

    @Override
    public void addLobby(Lobby lobby) {
        lobbies.add(lobby);
    }

    @Override
    public void removeLobby(Lobby lobby) {
        lobbies.remove(lobby);
    }

    @Override
    public void removeLobby(String name) {
        if (containsLobby(name)) {
            lobbies.removeIf(lobby -> lobby.getName().equals(name));
        }
    }

    @Override
    public boolean containsLobby(Lobby lobby) {
        return lobbies.contains(lobby);
    }

    @Override
    public boolean containsLobby(String name) {
        return lobbies.stream().anyMatch(lobby -> lobby.getName().equalsIgnoreCase(name));
    }

    @Override
    public Optional<Lobby> getLobby(String name) {
        return lobbies.stream().filter(lobby -> lobby.getName().equals(name)).findFirst();
    }

    @Override
    public List<Lobby> getLobbies() {
        return new ArrayList<>(lobbies);
    }
}
