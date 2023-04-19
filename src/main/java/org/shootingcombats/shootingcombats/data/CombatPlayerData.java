package org.shootingcombats.shootingcombats.data;

import org.shootingcombats.shootingcombats.Combat;

import java.util.*;

public final class CombatPlayerData extends CombatData {

    private final Set<UUID> spectators;
    private final Map<UUID, PlayerStatus> players;
    private final Map<UUID, Integer> kills;

    public CombatPlayerData(Combat combat) {
        super(combat);
        this.spectators = new HashSet<>();
        this.players = new HashMap<>();
        this.kills = new HashMap<>();
    }

    public Map<UUID, PlayerStatus> getPlayers() {
        return Collections.unmodifiableMap(players);
    }

    public Set<UUID> getSpectators() {
        return Collections.unmodifiableSet(spectators);
    }

    public void addKill(UUID uuid) {
        addKill(uuid, 1);
    }

    public void addKill(UUID uuid, Integer numberOfKills) {
        kills.put(uuid, kills.get(uuid) + numberOfKills);
    }

    public Map<UUID, Integer> getKills() {
        return Collections.unmodifiableMap(kills);
    }

    public void addSpectator(UUID uuid) {
        spectators.add(uuid);
    }

    public void removeSpectator(UUID uuid) {
        spectators.remove(uuid);
    }

    public void addPlayer(UUID uuid) {
        players.put(uuid, PlayerStatus.ALIVE);
    }

    public void removePlayer(UUID uuid) {
        players.remove(uuid);
    }


    public enum PlayerStatus {
        ALIVE,
        KILLED
    }
}
