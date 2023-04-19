package org.shootingcombats.shootingcombats;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.shootingcombats.shootingcombats.util.TypedProperty;
import org.shootingcombats.shootingcombats.util.TypedPropertyImpl;
import org.shootingcombats.shootingcombats.util.Util;

import java.util.*;
import java.util.concurrent.TimeUnit;

public final class SimpleLobby implements Lobby {

    private final Map<String, TypedProperty> lobbyProperties;
    private final Map<UUID, PlayerStatus> players;
    private final List<CombatMap> combatMaps;
    private final String name, type;
    private final TimeUnit minutes = TimeUnit.MINUTES;
    private CombatMap finalCombatMap;
    private long durationMinutes;
    private UUID owner;
    private int minPlayers, maxPlayers;
    private LobbyStatus lobbyStatus;
    private Combat currentCombat;
    private Location lobbySpawn;
    public SimpleLobby(String name, UUID owner) {
        this(name, owner, 15, TimeUnit.MINUTES);
    }

    public SimpleLobby(String name, UUID owner, long timeInMinutes, TimeUnit timeUnit) {
        //TODO: take default min & max players from config
        this(name, owner, timeInMinutes, timeUnit, 2, 6);
    }

    public SimpleLobby(String name, UUID owner, long timeInMinutes, TimeUnit timeUnit, int minPlayers, int maxPlayers) {
        this.name = name;
        this.owner = owner;
        this.type = "Deathmatch";
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.lobbyStatus = LobbyStatus.NOT_READY;
        this.players = new HashMap<>();
        this.lobbyProperties = new LinkedHashMap<>();
        this.combatMaps = new ArrayList<>();
        setCombatDuration(timeUnit, timeInMinutes);
        setupProperties();
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
        checkStatus();
        Util.log(Bukkit.getPlayer(uuid).getName() + " joined lobby " + this.name);
        Util.sendMessage(uuid, "You joined lobby " + this.name);
    }

    @Override
    public void leaveLobby(UUID uuid) {
        players.remove(uuid);
        //If no players in lobby stops current battle and removes lobby from lobbies list
        if (players.isEmpty()) {
            if (currentCombat != null) {
                currentCombat.forcedStop();
            }
            //removeLobby();
        }
        if (owner.equals(uuid) && !players.isEmpty()) {
            setOwner(uuid);
            checkStatus();
        }

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
        if (number >= 2 && number <= maxPlayers) {
            this.minPlayers = number;
        }
    }

    @Override
    public void setMaxPlayers(int number) {
        if (number >= minPlayers && number <= Bukkit.getServer().getMaxPlayers()) {
            this.maxPlayers = number;
        }
    }

    @Override
    public int getMinPlayers() {
        return this.minPlayers;
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
        //TODO: set time range from config
        if (result >= 1) {
            this.durationMinutes = result;
        }
    }

    @Override
    public long getCombatDuration(TimeUnit timeUnit) {
        return timeUnit.convert(durationMinutes, TimeUnit.MINUTES);
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
    public boolean isInLobby(UUID uuid) {
        return players.containsKey(uuid);
    }

    @Override
    public Combat getCurrentCombat() {
        return this.currentCombat;
    }

    @Override
    public void startCombat(CombatMap combatMap) {
        this.currentCombat = new DeathmatchCombat(this, players.keySet(), durationMinutes, lobbyProperties, combatMap, finalCombatMap);
        currentCombat.start();
    }

    private void setupProperties() {
        //TODO: put values from config
        lobbyProperties.put("final-teleport", new TypedPropertyImpl(true));
        lobbyProperties.put("spectate-after-death", new TypedPropertyImpl(true));
        lobbyProperties.put("endgame-tags", new TypedPropertyImpl(true));
        lobbyProperties.put("final-map", new TypedPropertyImpl("null"));
    }

    private void checkStatus() {
        if (lobbyStatus != LobbyStatus.READY && lobbyStatus != LobbyStatus.NOT_READY) {
            return;
        }
        if (players.values().stream().anyMatch(status -> status == PlayerStatus.NOT_READY)) {
            this.lobbyStatus = LobbyStatus.NOT_READY;
        } else {
            this.lobbyStatus = LobbyStatus.READY;
        }
    }
}
