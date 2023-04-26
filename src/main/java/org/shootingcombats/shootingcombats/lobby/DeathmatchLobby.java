package org.shootingcombats.shootingcombats.lobby;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.shootingcombats.shootingcombats.combat.Combat;
import org.shootingcombats.shootingcombats.combat.DeathmatchCombat;
import org.shootingcombats.shootingcombats.bar.DmLobbyBar;
import org.shootingcombats.shootingcombats.board.DmLobbyBoard;
import org.shootingcombats.shootingcombats.data.DmLobbyPlayerData;
import org.shootingcombats.shootingcombats.data.PluginConfig;
import org.shootingcombats.shootingcombats.manager.LobbiesManager;
import org.shootingcombats.shootingcombats.map.CombatMap;
import org.shootingcombats.shootingcombats.util.TypedProperty;
import org.shootingcombats.shootingcombats.util.TypedPropertyImpl;
import org.shootingcombats.shootingcombats.util.Util;

import java.util.*;
import java.util.concurrent.TimeUnit;

public final class DeathmatchLobby implements Lobby {

    private final Map<String, TypedProperty> lobbyProperties;
    private final List<CombatMap> combatMaps;
    private final String type;
    private final DmLobbyBoard lobbyBoard;
    private final DmLobbyBar lobbyBar;
    private final DmLobbyPlayerData lobbyPlayerData;
    private final LobbiesManager lobbiesManager;
    private String name;
    private long combatDurationMinutes;
    private UUID owner;
    private int maxPlayers;
    private LobbyStatus lobbyStatus;
    private Combat currentCombat;
    private Location lobbySpawn;

    public DeathmatchLobby(String name, UUID owner, LobbiesManager lobbiesManager) {
        this.name = Objects.requireNonNull(name);
        this.lobbiesManager = Objects.requireNonNull(lobbiesManager);
        this.owner = Objects.requireNonNull(owner);
        this.type = "Deathmatch";

        this.maxPlayers = Math.min(8, Bukkit.getMaxPlayers());
        this.lobbyStatus = LobbyStatus.NOT_READY;
        this.lobbyProperties = new LinkedHashMap<>();
        this.combatMaps = new ArrayList<>();
        this.combatDurationMinutes = PluginConfig.dmCombatDurMinutes;
        this.lobbySpawn = Bukkit.getPlayer(owner).getLocation();
        this.lobbySpawn.setY(lobbySpawn.getY() + 1);

        this.lobbyPlayerData = new DmLobbyPlayerData(this);
        this.lobbyBoard = new DmLobbyBoard();
        this.lobbyBar = new DmLobbyBar(name, owner, lobbyStatus);

        lobbyProperties.put("final-teleport", new TypedPropertyImpl(PluginConfig.dmFinalTeleport));
        lobbyProperties.put("minutes-to-final-tp", new TypedPropertyImpl(PluginConfig.dmMinutesToFinalTp));
        lobbyProperties.put("endgame-tags", new TypedPropertyImpl(PluginConfig.dmEndgameTags));
        lobbyProperties.put("minutes-to-tags", new TypedPropertyImpl(PluginConfig.dmMinutesToTags));
        lobbyProperties.put("spectate-after-death", new TypedPropertyImpl(PluginConfig.dmDeathSpectate));

        joinLobby(owner);
        this.lobbyBoard.setOwner(owner);
        lobbiesManager.addLobby(this);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(UUID executor, String name) {
        if (executor.equals(owner)) {
            Util.sendMessage(executor, "Only owner of lobby can do this!");
            return;
        }
        if ("".equals(name)) {
            Util.sendMessage(executor, "Lobby name cannot be empty!");
            return;
        }
        Util.sendMessage(executor, "Lobby name changed from " + this.name + " to " + name);
        this.name = name;
        lobbyBar.setLobbyName(name);
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public void setLobbySpawn(UUID executor, Location location) {
        if (!owner.equals(executor)) {
            Util.sendMessage(executor, "Only owner of lobby can do this!");
            return;
        }
        this.lobbySpawn = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
        Util.sendMessage(executor, "Spawn location changed to [" + lobbySpawn.getX() + " " + lobbySpawn.getY() + " " + lobbySpawn.getZ() + "] (" + location.getWorld().getName() + ")");
    }

    @Override
    public Location getLobbySpawn() {
        return new Location(lobbySpawn.getWorld(), lobbySpawn.getX(), lobbySpawn.getY(), lobbySpawn.getZ());
    }

    @Override
    public void setOwner(UUID executor, UUID newOwner) {
        if (!owner.equals(executor)) {
            Util.sendMessage(executor, "Only owner of lobby can do this!");
            return;
        }
        Util.sendMessage(executor, "You are no owner of lobby " + this.name + " anymore");
        Util.sendMessage(lobbyPlayerData.getPlayers(PlayerStatus.READY), Bukkit.getPlayer(newOwner).getName() + " is now owner of this lobby");
        Util.sendMessage(lobbyPlayerData.getPlayers(PlayerStatus.NOT_READY), Bukkit.getPlayer(newOwner).getName() + " is now owner of this lobby");

        this.owner = newOwner;
        this.lobbyBoard.setOwner(newOwner);
        this.lobbyBar.setOwner(newOwner);
    }

    @Override
    public UUID getOwner() {
        return this.owner;
    }

    @Override
    public void setProperty(UUID executor, String property, TypedProperty value) {
        if (owner.equals(executor)) {
            Util.sendMessage(executor, "Only owner of lobby can do this!");
            return;
        }
        if (!lobbyProperties.containsKey(property)) {
            Util.sendMessage(executor, "This lobby doesn't contain property " + property + "!");
            return;
        }
        lobbyProperties.put(property, value);
        Util.sendMessage(executor, "Property " + property + " successfully set to " + value.getValue(value.getValueClass()));
    }

    @Override
    public Optional<TypedProperty> getProperty(String property) {
        return Optional.ofNullable(lobbyProperties.get(property));
    }

    @Override
    public boolean containsProperty(String property) {
        return lobbyProperties.containsKey(property);
    }

    @Override
    public Map<String, TypedProperty> getProperties() {
        return Collections.unmodifiableMap(lobbyProperties);
    }

    @Override
    public void joinLobby(UUID uuid) {
        if (isPlayerInLobby(uuid)) {
            Util.sendMessage(uuid, "You are already in lobby " + this.name + "!");
            return;
        }
        if (lobbyPlayerData.getPlayerNumber() >= maxPlayers) {
            Util.sendMessage(uuid, "Lobby " + this.name + " is full!");
            return;
        }
        Util.sendMessage(lobbyPlayerData.getPlayers(PlayerStatus.READY), Bukkit.getPlayer(uuid).getName() + " joined lobby");
        Util.sendMessage(lobbyPlayerData.getPlayers(PlayerStatus.NOT_READY), Bukkit.getPlayer(uuid).getName() + " joined lobby");
        Util.sendMessage(uuid, "You joined lobby " + this.name);
        Bukkit.getPlayer(uuid).teleport(lobbySpawn);
        lobbyPlayerData.addPlayer(uuid);
        lobbyBoard.createEntriesForPlayer(uuid);
        lobbyBoard.addPlayerToBoard(uuid);
        lobbyBar.addPlayerToBar(uuid);
        setPlayerStatus(uuid, PlayerStatus.NOT_READY);

        updateLobbyStatus();
        //Util.log(Bukkit.getPlayer(uuid).getName() + " joined lobby " + this.name);

    }

    @Override
    public void leaveLobby(UUID uuid) {
        if (!isPlayerInLobby(uuid)) {
            Util.sendMessage(uuid, "You are not in lobby " + this.name + "!");
            return;
        }
        Util.sendMessage(lobbyPlayerData.getPlayers(PlayerStatus.READY), Bukkit.getPlayer(uuid).getName() + " left lobby");
        Util.sendMessage(lobbyPlayerData.getPlayers(PlayerStatus.NOT_READY), Bukkit.getPlayer(uuid).getName() + " left lobby");
        Util.sendMessage(uuid, "You successfully left lobby " + this.name);
        lobbyPlayerData.removePlayer(uuid);
        if (owner.equals(uuid)) {
            if (lobbyPlayerData.getPlayerNumber() > 0) {
                setOwner(uuid, lobbyPlayerData.getPlayers().stream().findFirst().get());
            } else {
                dismissLobby(uuid);
            }
        }
        lobbyBoard.removePlayerFromBoard(uuid);
        lobbyBar.removePlayerFromBar(uuid);
        Bukkit.getPlayer(uuid).teleport(lobbySpawn.getWorld().getSpawnLocation());

        Util.log(Bukkit.getPlayer(uuid).getName() + " left lobby " + this.name);
    }

    @Override
    public int getPlayersNumber() {
        return lobbyPlayerData.getPlayerNumber();
    }

    @Override
    public void setMaxPlayers(UUID executor, int number) {
        if (owner.equals(executor)) {
            Util.sendMessage(executor, "Only owner of lobby can do this!");
            return;
        }
        if (number < 1 && number > Bukkit.getServer().getMaxPlayers()) {
            Util.sendMessage(executor, "Max players number must be in range from 1 to " + Bukkit.getServer().getMaxPlayers() + "!");
            return;
        }
        this.maxPlayers = number;
        Util.sendMessage(executor, "Max players number is set to " + number);
    }

    @Override
    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    @Override
    public void setCombatDuration(UUID executor, TimeUnit timeUnit, long timeInTimeUnits) {
        if (owner.equals(executor)) {
            Util.sendMessage(executor, "Only owner of lobby can do this!");
            return;
        }
        long result = timeUnit.toMinutes(timeInTimeUnits);
        if (result < 1 && result > PluginConfig.gbMaxCombatDurMinutes) {
            Util.sendMessage(executor, "Combat duration must be in range from 1 to " + PluginConfig.gbMaxCombatDurMinutes + " minutes!");
            return;
        }
        this.combatDurationMinutes = result;
        Util.sendMessage(executor, "Combat duration set to " + result + " minutes!");
    }

    @Override
    public long getCombatDuration(TimeUnit timeUnit) {
        return timeUnit.convert(combatDurationMinutes, TimeUnit.MINUTES);
    }

    @Override
    public LobbyStatus getLobbyStatus() {
        return this.lobbyStatus;
    }

    @Override
    public boolean isPlayerInLobby(UUID uuid) {
        return lobbyPlayerData.containsPlayer(uuid);
    }

    @Override
    public Collection<UUID> getPlayers() {
        return lobbyPlayerData.getPlayers();
    }

    @Override
    public Combat getCurrentCombat() {
        return this.currentCombat;
    }

    @Override
    public void startCombat(UUID executor, CombatMap combatMap) {
        if (!owner.equals(executor)) {
            Util.sendMessage(executor, "Only owner of lobby can do this!");
            return;
        }
        switch (lobbyStatus) {
            case READY: {
                if (lobbyPlayerData.getPlayerNumber() > combatMap.spawnsNumber()) {
                    Util.sendMessage(executor, combatMap.getName() + " has too few spawns");
                    return;
                }

                if (lobbyPlayerData.getPlayerNumber() < 2) {
                    Util.sendMessage(executor, "Unable to start a combat!");
                    Util.sendMessage(executor, "Too few players!");
                    return;
                }

                for (UUID uuid : lobbyPlayerData.getPlayers()) {
                    setPlayerStatus(uuid, PlayerStatus.IN_COMBAT);
                }
                Util.sendMessage(executor, "You successfully started the combat");

                this.currentCombat = new DeathmatchCombat(this, lobbyPlayerData.getPlayers(), combatDurationMinutes, lobbyProperties, combatMap);
                currentCombat.start();
                return;
            }
            case RUNNING: {
                Util.sendMessage(executor, "Unable to start a combat!");
                Util.sendMessage(executor, "Lobby is already in combat!");
                return;
            }
            case NOT_READY: {
                Util.sendMessage(executor, "Unable to start a combat!");
                Util.sendMessage(executor, "Not all players are READY!");
            }
        }
    }

    @Override
    public void stopCombat(UUID executor) {
        if (owner.equals(executor)) {
            Util.sendMessage(executor, "Only owner of lobby can do this!");
            return;
        }
        if (lobbyStatus != LobbyStatus.RUNNING) {
            Util.sendMessage(executor, "There are no combats in lobby " + this.name);
            return;
        }
        currentCombat.forcedStop();
        Util.sendMessage(executor, "You successfully stopped the combat");
    }

    @Override
    public void removeCurrentBattle() {
        this.currentCombat = null;
    }

    @Override
    public void setPlayerStatus(UUID player, PlayerStatus playerStatus) {
        lobbyPlayerData.setPlayerStatus(player, playerStatus);
        lobbyBoard.setPlayerStatus(player, playerStatus);
        updateLobbyStatus();
    }

    @Override
    public PlayerStatus getPlayerStatus(UUID uuid) {
        return lobbyPlayerData.getPlayerStatus(uuid);
    }

    public void prepareForCombat(UUID uuid) {
        setPlayerStatus(uuid, PlayerStatus.IN_COMBAT);
        lobbyBar.removePlayerFromBar(uuid);
    }

    public void unprepareFromCombat(UUID uuid) {
        setPlayerStatus(uuid, PlayerStatus.NOT_READY);
        lobbyBar.addPlayerToBar(uuid);
    }

    @Override
    public void dismissLobby(UUID executor) {
        if (!owner.equals(executor)) {
            Util.sendMessage(executor, "Only owner of lobby can do this!");
            return;
        }
        for (UUID uuid : new HashSet<>(lobbyPlayerData.getPlayers())) {
            setPlayerStatus(uuid, PlayerStatus.NA);
            lobbyPlayerData.removePlayer(uuid);
            lobbyBoard.removePlayerFromBoard(uuid);
            lobbyBar.removePlayerFromBar(uuid);
            //TODO: change to world where player come from
            Bukkit.getPlayer(uuid).teleport(lobbySpawn.getWorld().getSpawnLocation());
        }
        lobbiesManager.removeLobby(this);
    }

    private void updateLobbyStatus() {
        if (lobbyPlayerData.getPlayers(PlayerStatus.IN_COMBAT).size() > 0) {
            lobbyStatus = LobbyStatus.RUNNING;
            lobbyBar.setLobbyStatus(lobbyStatus);
            return;
        }

        if (lobbyPlayerData.getPlayers(PlayerStatus.NOT_READY).size() > 0) {
            lobbyStatus = LobbyStatus.NOT_READY;
            lobbyBar.setLobbyStatus(lobbyStatus);
            return;
        }

        if (lobbyPlayerData.getPlayers(PlayerStatus.READY).size() == lobbyPlayerData.getPlayerNumber()) {
            lobbyStatus = LobbyStatus.READY;
            lobbyBar.setLobbyStatus(lobbyStatus);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeathmatchLobby that = (DeathmatchLobby) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
