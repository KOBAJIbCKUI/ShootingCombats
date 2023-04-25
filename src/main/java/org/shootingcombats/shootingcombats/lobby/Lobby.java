package org.shootingcombats.shootingcombats.lobby;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.shootingcombats.shootingcombats.combat.Combat;
import org.shootingcombats.shootingcombats.map.CombatMap;
import org.shootingcombats.shootingcombats.util.TypedProperty;

import java.util.*;
import java.util.concurrent.TimeUnit;

public interface Lobby {
    String getName();
    void setName(UUID executor, String name);
    String getType();
    UUID getOwner();
    void setOwner(UUID executor, UUID player);
    void setLobbySpawn(UUID executor, Location location);
    Location getLobbySpawn();
    void setProperty(UUID executor, String property, TypedProperty value);
    Optional<TypedProperty> getProperty(String property);
    Map<String, TypedProperty> getProperties();
    boolean containsProperty(String property);
    void joinLobby(UUID player);
    void leaveLobby(UUID player);
    int getPlayersNumber();
    void setMaxPlayers(UUID executor, int number);
    int getMaxPlayers();
    void setCombatDuration(UUID executor, TimeUnit timeUnit, long timeInTimeUnits);
    long getCombatDuration(TimeUnit timeUnit);
    LobbyStatus getLobbyStatus();
    //void setLobbyStatus(LobbyStatus lobbyStatus);
    boolean isPlayerInLobby(UUID uuid);
    Collection<UUID> getPlayers();
    Combat getCurrentCombat();
    void startCombat(UUID executor, CombatMap combatMap);
    void stopCombat(UUID executor);
    void setPlayerStatus(UUID player, PlayerStatus playerStatus);
    PlayerStatus getPlayerStatus(UUID uuid);
    void dismissLobby(UUID executor);


    enum LobbyStatus {
        READY("" + ChatColor.GREEN + ChatColor.BOLD + "READY"),
        NOT_READY("" + ChatColor.GRAY + ChatColor.BOLD + "NOT READY"),
        RUNNING("" + ChatColor.RED  + ChatColor.BOLD + "RUNNING");

        private final String name;

        LobbyStatus(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    enum PlayerStatus {
        READY,
        NOT_READY,
        IN_COMBAT,
        NA
    }

    enum LobbyType {

        DEATHMATCH("deathmatch");

        private final String type;

        LobbyType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return this.type;
        }
    }
}
