package org.shootingcombats.shootingcombats;

import org.bukkit.Bukkit;
import org.shootingcombats.shootingcombats.util.Util;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class SimpleLobby implements Lobby {

    private final Map<String, String> lobbyProperties = new LinkedHashMap<>();
    private final Map<UUID, PlayerStatus> players;
    private final String name, type;
    private int minPlayers, maxPlayers;
    private final TimeUnit minutes = TimeUnit.MINUTES;
    private long timeTicks;
    private List<Consumer<Lobby>> preCombatActions;
    private List<Consumer<Lobby>> innerCombatActions;
    private List<Consumer<Lobby>> postCombatActions;
    private Runnable battleThread;
    private LobbyStatus lobbyStatus;
    private Combat currentCombat;
    public SimpleLobby(String name) {
        this(name, 15);
    }

    public SimpleLobby(String name, int timeInMinutes) {
        //TODO: take default min & max players from config
        this(name, timeInMinutes, 2, 6);
    }

    public SimpleLobby(String name, int timeInMinutes, int minPlayers, int maxPlayers) {
        this.name = name;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;

        this.setTimeMinutes(timeInMinutes);

        this.lobbyStatus = LobbyStatus.NOT_READY;
        this.players = new HashMap<>();
        this.type = "Deathmatch";
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public void joinLobby(UUID uuid) {
        players.put(uuid, PlayerStatus.NOT_READY);
        checkStatus();
        Util.log(Bukkit.getPlayer(uuid).getName() + " joined lobby " + this.name);
        Util.sendMessage(uuid, "You joined lobby " + this.name);
    }

    @Override
    public void leaveLobby(UUID uuid) {
        players.remove(uuid);
        checkStatus();
        Util.log(Bukkit.getPlayer(uuid).getName() + " left lobby " + this.name);
        Util.sendMessage(uuid, "You left lobby " + this.name);
    }

    @Override
    public void setPlayerReady(UUID uuid) {
        players.put(uuid, PlayerStatus.READY);
        checkStatus();
    }

    @Override
    public void unsetPlayerReady(UUID uuid) {
        players.put(uuid, PlayerStatus.NOT_READY);
        checkStatus();
    }

    @Override
    public int getPlayersNumber() {
        return players.size();
    }

    @Override
    public void setMinPlayers(int number) {
        if (number > maxPlayers || number <= 1) {
            return;
        }
        this.minPlayers = number;
    }

    @Override
    public void setMaxPlayers(int number) {
        if (number < minPlayers || number > Bukkit.getServer().getMaxPlayers()) {
            return;
        }
        this.maxPlayers = number;
    }

    @Override
    public int getMinPlayers() {
        return minPlayers;
    }

    @Override
    public int getMaxPlayers() {
        return maxPlayers;
    }

    @Override
    public void setTimeMinutes(int timeInTimeUnits) {
        this.timeTicks = minutes.toSeconds(timeInTimeUnits) * 20;
    }

    @Override
    public long getTimeMinutes() {
        return minutes.convert(timeTicks / 20, minutes);
    }

    @Override
    public Combat getCurrentCombat() {
        return this.currentCombat;
    }

    @Override
    public Combat createCombat() {
        return null;
    }

    @Override
    public void addPreCombatAction(Consumer<Lobby> consumer) {
        preCombatActions.add(consumer);
    }

    @Override
    public void addInnerCombatAction(Consumer<Lobby> consumer) {
        innerCombatActions.add(consumer);
    }

    @Override
    public void addPostCombatAction(Consumer<Lobby> consumer) {
        postCombatActions.add(consumer);
    }

    private void setupDefaultActions() {

    }

    private void checkStatus() {
        if (lobbyStatus == LobbyStatus.STARTING || lobbyStatus == LobbyStatus.RUNNING || lobbyStatus == LobbyStatus.ENDING) {
            return;
        }
        if (players.values().stream().anyMatch(status -> status == PlayerStatus.NOT_READY)) {
            this.lobbyStatus = LobbyStatus.NOT_READY;
        } else {
            this.lobbyStatus = LobbyStatus.READY;
        }
    }
}
