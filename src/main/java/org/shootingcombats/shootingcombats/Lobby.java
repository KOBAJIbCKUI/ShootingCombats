package org.shootingcombats.shootingcombats;

import org.bukkit.Location;
import org.shootingcombats.shootingcombats.util.TypedProperty;

import java.util.*;
import java.util.concurrent.TimeUnit;

public interface Lobby {
    String getName();
    void setName(String name);
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
    void setMaxPlayers(int number);
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
    void setPlayerStatus(UUID uuid, PlayerStatus playerStatus);
    enum LobbyStatus {
        READY,
        NOT_READY,
        RUNNING
    }

    enum PlayerStatus {
        READY,
        NOT_READY,
        IN_COMBAT
    }
}
