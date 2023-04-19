package org.shootingcombats.shootingcombats;

import org.bukkit.Location;
import org.shootingcombats.shootingcombats.util.TypedProperty;
import org.shootingcombats.shootingcombats.util.TypedPropertyImpl;

import java.util.*;
import java.util.concurrent.TimeUnit;

public interface Lobby {
    String getName();
    String getType();
    void setOwner(UUID uuid);
    UUID getOwner();
    void setLobbySpawn(Location location);
    Location getLobbySpawn();
    void setProperty(String property, TypedProperty value);
    Optional<TypedProperty> getProperty(String property);
    boolean containsProperty(String property);
    void joinLobby(UUID uuid);
    void leaveLobby(UUID uuid);
    void setPlayerReady(UUID uuid);
    void unsetPlayerReady(UUID uuid);
    int getPlayersNumber();
    void setMinPlayers(int number);
    void setMaxPlayers(int number);
    int getMinPlayers();
    int getMaxPlayers();
    void addCombatMap(CombatMap combatMap);
    boolean containsCombatMap(CombatMap combatMap);
    void removeCombatMap(CombatMap combatMap);
    void removeCombatMap(int index);
    Collection<CombatMap> getCombatMaps();
    int getCombatMapsNumber();
    void setCombatDuration(TimeUnit timeUnit, long timeInTimeUnits);
    long getCombatDuration(TimeUnit timeUnit);
    LobbyStatus getLobbyStatus();
    void setLobbyStatus(LobbyStatus lobbyStatus);
    boolean isInLobby(UUID uuid);
    Combat getCurrentCombat();
    void startCombat(CombatMap combatMap);

    enum LobbyStatus {
        READY,
        NOT_READY,
        STARTING,
        RUNNING,
        ENDING
    }

    enum PlayerStatus {
        READY,
        NOT_READY,
        IN_COMBAT
    }
}
