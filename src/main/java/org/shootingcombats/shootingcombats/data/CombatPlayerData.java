package org.shootingcombats.shootingcombats.data;

import org.shootingcombats.shootingcombats.Combat;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class CombatPlayerData extends CombatData {

    private final Set<UUID> players, spectators;

    public CombatPlayerData(Combat combat, Set<UUID> players) {
        this(combat, players, new HashSet<>());
    }

    public CombatPlayerData(Combat combat, Set<UUID> players, Set<UUID> spectators) {
        super(combat);
        this.players = players;
        this.spectators = spectators;
    }

    public Set<UUID> getPlayers() {
        return Collections.unmodifiableSet(players);
    }

    public Set<UUID> getSpectators() {
        return Collections.unmodifiableSet(spectators);
    }

    public boolean addSpectator(UUID uuid) {
        return spectators.add(uuid);
    }

    public boolean removeSpectator(UUID uuid) {
        return spectators.remove(uuid);
    }

    public boolean addPlayer(UUID uuid) {
        return players.add(uuid);
    }

    public boolean removePlayer(UUID uuid) {
        return players.remove(uuid);
    }


}
