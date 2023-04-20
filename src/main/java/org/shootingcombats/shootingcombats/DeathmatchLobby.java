package org.shootingcombats.shootingcombats;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.shootingcombats.shootingcombats.data.PluginConfig;
import org.shootingcombats.shootingcombats.util.TypedProperty;
import org.shootingcombats.shootingcombats.util.TypedPropertyImpl;
import org.shootingcombats.shootingcombats.util.Util;

import java.util.*;
import java.util.concurrent.TimeUnit;

public final class DeathmatchLobby implements Lobby {

    private final Map<String, TypedProperty> lobbyProperties;
    private final Map<UUID, PlayerStatus> players;
    private final List<CombatMap> combatMaps;
    private final String type;
    private final DeathmatchLobbyBoard lobbyBoard;
    private String name;
    private long combatDurationMinutes;
    private UUID owner;
    private int maxPlayers;
    private LobbyStatus lobbyStatus;
    private Combat currentCombat;
    private Location lobbySpawn;

    private DeathmatchLobby(String name) {
        this.name = name;
        this.type = "Deathmatch";
        this.maxPlayers = Math.min(8, Bukkit.getMaxPlayers());
        this.lobbyStatus = LobbyStatus.NOT_READY;
        this.players = new HashMap<>();
        this.lobbyProperties = new LinkedHashMap<>();
        this.combatMaps = new ArrayList<>();
        setCombatDuration(TimeUnit.MINUTES, PluginConfig.dmCombatDurMinutes);

        lobbyProperties.put("final-teleport", new TypedPropertyImpl(PluginConfig.dmFinalTeleport));
        lobbyProperties.put("minutes-to-final-tp", new TypedPropertyImpl(PluginConfig.dmMinutesToFinalTp));
        lobbyProperties.put("endgame-tags", new TypedPropertyImpl(PluginConfig.dmEndgameTags));
        lobbyProperties.put("minutes-to-tags", new TypedPropertyImpl(PluginConfig.dmMinutesToTags));
        lobbyProperties.put("spectate-after-death", new TypedPropertyImpl(PluginConfig.dmDeathSpectate));

        this.lobbyBoard = new DeathmatchLobbyBoard();
    }

    public static DeathmatchLobby newLobby(String name) {
        return new DeathmatchLobby(Objects.requireNonNull(name));
    }

    @Override
    public String getName() {
        return this.name;
    }
    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public UUID getOwner() {
        return this.owner;
    }

    @Override
    public void setLobbySpawn(Location location) {
        this.lobbySpawn = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
    }

    @Override
    public Location getLobbySpawn() {
        return new Location(lobbySpawn.getWorld(), lobbySpawn.getX(), lobbySpawn.getY(), lobbySpawn.getZ());
    }

    @Override
    public void setOwner(UUID owner) {
        this.owner = owner;
        this.lobbyBoard.setOwner(owner);
    }

    @Override
    public void setProperty(String property, TypedProperty value) {
        if (lobbyProperties.containsKey(property)) {
            lobbyProperties.put(property, value);
        }
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
    public void joinLobby(UUID uuid) {
        players.put(uuid, PlayerStatus.NOT_READY);
        this.lobbyBoard.addPlayerToBoard(uuid);
        this.lobbyBoard.setPlayerStatus(uuid, PlayerStatus.NOT_READY);
        updateLobbyStatus();
        Util.log(Bukkit.getPlayer(uuid).getName() + " joined lobby " + this.name);
        for (Map.Entry<UUID, PlayerStatus> entry : players.entrySet()) {
            if (entry.getValue() != PlayerStatus.IN_COMBAT) {
                Util.sendMessage(uuid, Bukkit.getPlayer(uuid).getName() + " joined lobby");
            }
        }
    }

    @Override
    public void leaveLobby(UUID uuid) {
        players.remove(uuid);
        this.lobbyBoard.removePlayerFromBoard(uuid);

        if (owner.equals(uuid)) {
            setOwner(players.keySet().stream().findFirst().orElse(null));
            if (owner != null && players.get(owner) != PlayerStatus.IN_COMBAT) {
                Util.sendMessage(owner, "You are owner of this lobby");
            }
        }

        Util.log(Bukkit.getPlayer(uuid).getName() + " left lobby " + this.name);

        for (Map.Entry<UUID, PlayerStatus> entry : players.entrySet()) {
            if (entry.getValue() != PlayerStatus.IN_COMBAT) {
                Util.sendMessage(uuid, Bukkit.getPlayer(uuid).getName() + " left lobby");
            }
        }
    }

    @Override
    public int getPlayersNumber() {
        return players.size();
    }

    @Override
    public void setMaxPlayers(int number) {
        if (number >= 1 && number <= Bukkit.getServer().getMaxPlayers()) {
            this.maxPlayers = number;
        }
    }

    @Override
    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    @Override
    public void addCombatMap(CombatMap combatMap) {
        if (!containsCombatMap(combatMap)) {
            this.combatMaps.add(combatMap);
        }
    }

    @Override
    public boolean containsCombatMap(CombatMap combatMap) {
        return this.combatMaps.contains(combatMap);
    }

    @Override
    public void removeCombatMap(CombatMap combatMap) {
        this.combatMaps.remove(combatMap);
    }

    @Override
    public void removeCombatMap(int index) {
        if (index > 0 && index < combatMaps.size()) {
            this.combatMaps.remove(index - 1);
        }
    }

    @Override
    public Collection<CombatMap> getCombatMaps() {
        return new HashSet<>(combatMaps);
    }

    @Override
    public int getCombatMapsNumber() {
        return combatMaps.size();
    }

    @Override
    public void setCombatDuration(TimeUnit timeUnit, long timeInTimeUnits) {
        long result = timeUnit.toMinutes(timeInTimeUnits);
        if (result >= 1 && result <= PluginConfig.gbMaxCombatDurMinutes) {
            this.combatDurationMinutes = result;
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

    @Override
    public long getCombatDuration(TimeUnit timeUnit) {
        return timeUnit.convert(combatDurationMinutes, TimeUnit.MINUTES);
    }

    @Override
    public LobbyStatus getLobbyStatus() {
        return this.lobbyStatus;
    }

    @Override
    public void setLobbyStatus(LobbyStatus lobbyStatus) {
        this.lobbyStatus = lobbyStatus;
    }

    @Override
    public boolean isPlayerInLobby(UUID uuid) {
        return players.containsKey(uuid);
    }

    @Override
    public Collection<UUID> getPlayers() {
        return new HashSet<>(players.keySet());
    }

    @Override
    public Combat getCurrentCombat() {
        return this.currentCombat;
    }

    @Override
    public void startCombat(CombatMap combatMap) {
        switch (lobbyStatus) {
            case READY: {
                for (UUID uuid : players.keySet()) {
                    setPlayerStatus(uuid, PlayerStatus.IN_COMBAT);
                }
                Util.sendMessage(owner, "You successfully started the combat");

                this.currentCombat = new DeathmatchCombat(this, players.keySet(), combatDurationMinutes, lobbyProperties, combatMap);
                currentCombat.start();
                break;
            }
            case RUNNING: {
                Util.sendMessage(owner, "Unable to start a combat!");
                Util.sendMessage(owner, "Lobby is already in combat!");
                break;
            }
            case NOT_READY: {
                Util.sendMessage(owner, "Unable to start a combat!");
                Util.sendMessage(owner, "Not all players are READY!");
                break;
            }
        }
    }

    @Override
    public void stopCombat() {
        if (lobbyStatus == LobbyStatus.RUNNING) {
            currentCombat.forcedStop();
        }
    }

    @Override
    public void setPlayerStatus(UUID uuid, PlayerStatus playerStatus) {
        players.put(uuid, playerStatus);
        this.lobbyBoard.setPlayerStatus(uuid, playerStatus);
        updateLobbyStatus();
    }

    private void updateLobbyStatus() {
        if (players.values().stream().anyMatch(status -> status == PlayerStatus.IN_COMBAT)) {
            lobbyStatus = LobbyStatus.RUNNING;
            return;
        }

        if (players.values().stream().anyMatch(status -> status == PlayerStatus.NOT_READY)) {
            lobbyStatus = LobbyStatus.NOT_READY;
            return;
        }

        if (players.values().stream().allMatch(status -> status == PlayerStatus.READY)) {
            lobbyStatus = LobbyStatus.READY;
        }
    }
}
