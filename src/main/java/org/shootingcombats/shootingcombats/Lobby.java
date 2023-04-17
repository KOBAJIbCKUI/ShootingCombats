package org.shootingcombats.shootingcombats;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public interface Lobby {
    String getName();
    String getType();
    void joinLobby(UUID uuid);
    void leaveLobby(UUID uuid);
    void setPlayerReady(UUID uuid);
    void unsetPlayerReady(UUID uuid);
    int getPlayersNumber();
    void setMinPlayers(int number);
    void setMaxPlayers(int number);
    int getMinPlayers();
    int getMaxPlayers();
    void setTimeMinutes(int timeInTimeUnits);
    long getTimeMinutes();
    Combat getCurrentCombat();
    Combat createCombat();
    void addPreCombatAction(Consumer<Lobby> consumer);
    void addInnerCombatAction(Consumer<Lobby> consumer);
    void addPostCombatAction(Consumer<Lobby> consumer);

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
