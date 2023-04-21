package org.shootingcombats.shootingcombats.data;

import org.shootingcombats.shootingcombats.*;

import java.util.*;

public final class DmLobbyPlayerData extends LobbyData {
    private final Map<UUID, Lobby.PlayerStatus> players;
    private final Map<UUID, PlayerState> playersStates;

    public DmLobbyPlayerData(Lobby lobby) {
        super(lobby);
        this.players = new HashMap<>();
        this.playersStates = new HashMap<>();
    }

    public Set<UUID> getPlayers() {
        return Collections.unmodifiableSet(players.keySet());
    }

    public Set<UUID> getPlayers(Lobby.PlayerStatus status) {
        Set<UUID> result = new HashSet<>();
        for (Map.Entry<UUID, Lobby.PlayerStatus> entry : players.entrySet()) {
            if (entry.getValue() == status) {
                result.add(entry.getKey());
            }
        }
        return Collections.unmodifiableSet(result);
    }

    public void addPlayer(UUID uuid) {
        players.put(uuid, Lobby.PlayerStatus.NA);
        PlayerState playerState = new DmState(uuid);
        playerState.store();
        playersStates.put(uuid, playerState);
    }

    public void removePlayer(UUID uuid) {
        players.remove(uuid);
        playersStates.remove(uuid).restore();
    }

    public void setPlayerStatus(UUID uuid, Lobby.PlayerStatus playerStatus) {
        players.computeIfAbsent(uuid, k -> playerStatus);
    }

    public Lobby.PlayerStatus getPlayerStatus(UUID uuid) {
        return players.get(uuid);
    }

    public int getPlayerNumber() {
        return players.size();
    }

    public boolean containsPlayer(UUID uuid) {
        return players.containsKey(uuid);
    }

}
