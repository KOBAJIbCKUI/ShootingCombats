package org.shootingcombats.shootingcombats;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.shootingcombats.shootingcombats.data.Config;
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
    private String name;
    private CombatMap finalCombatMap;
    private long combatDurationMinutes;
    private UUID owner;
    private int maxPlayers;
    private LobbyStatus lobbyStatus;
    private Combat currentCombat;
    private Location lobbySpawn;

    public DeathmatchLobby(String name, UUID owner) {
        this.name = name;
        this.owner = owner;
        this.type = "Deathmatch";
        this.maxPlayers = Config.gbMaxLobbyPlayers;
        this.lobbyStatus = LobbyStatus.NOT_READY;
        this.players = new HashMap<>();
        this.lobbyProperties = new LinkedHashMap<>();
        this.combatMaps = new ArrayList<>();
        setCombatDuration(TimeUnit.MINUTES, Config.dmCombatDurMinutes);

        lobbyProperties.put("final-teleport", new TypedPropertyImpl(Config.dmFinalTeleport));
        lobbyProperties.put("minutes-to-final-tp", new TypedPropertyImpl(Config.dmMinutesToFinalTp));
        lobbyProperties.put("endgame-tags", new TypedPropertyImpl(Config.dmEndgameTags));
        lobbyProperties.put("minutes-to-tags", new TypedPropertyImpl(Config.dmMinutesToTags));
        lobbyProperties.put("spectate-after-death", new TypedPropertyImpl(Config.dmDeathSpectate));
        lobbyProperties.put("final-map", new TypedPropertyImpl(""));

        update();
    }

    @Override
    public String getName() {
        return this.name;
    }
    @Override
    public void setName(String name) {
        this.name = name;
        update();
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
        update();
    }

    @Override
    public Location getLobbySpawn() {
        return new Location(lobbySpawn.getWorld(), lobbySpawn.getX(), lobbySpawn.getY(), lobbySpawn.getZ());
    }

    @Override
    public void setOwner(UUID owner) {
        this.owner = owner;
        update();
    }

    @Override
    public void setProperty(String property, TypedProperty value) {
        if (lobbyProperties.containsKey(property)) {
            lobbyProperties.put(property, value);
            if (property.equals("final-map")) {
                //get map from map list
            }
            update();
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
        for (Map.Entry<UUID, PlayerStatus> entry : players.entrySet()) {
            if (entry.getValue() != PlayerStatus.IN_COMBAT) {
                Util.sendMessage(uuid, Bukkit.getPlayer(uuid).getName() + " joined lobby");
            }
        }
        update();
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
            setOwner(players.keySet().stream().findFirst().get());
            checkStatus();
        }

        Util.log(Bukkit.getPlayer(uuid).getName() + " left lobby " + this.name);
        Util.sendMessage(uuid, "You left lobby " + this.name);
        if (players.get(owner) != PlayerStatus.IN_COMBAT) {
            Util.sendMessage(owner, "Now you are host of lobby " + this.name);
        }

        for (Map.Entry<UUID, PlayerStatus> entry : players.entrySet()) {
            if (entry.getValue() != PlayerStatus.IN_COMBAT) {
                Util.sendMessage(uuid, Bukkit.getPlayer(uuid).getName() + " joined lobby");
            }
        }
        update();
    }

    @Override
    public void setPlayerReady(UUID uuid) {
        players.put(uuid, PlayerStatus.READY);
        update();
    }

    @Override
    public void unsetPlayerReady(UUID uuid) {
        players.put(uuid, PlayerStatus.NOT_READY);
        update();
    }

    @Override
    public int getPlayersNumber() {
        return players.size();
    }

    @Override
    public void setMaxPlayers(int number) {
        if (number >= 1 && number <= Config.gbMaxLobbyPlayers) {
            this.maxPlayers = number;
            update();
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
            update();
        }
    }

    @Override
    public boolean containsCombatMap(CombatMap combatMap) {
        return this.combatMaps.contains(combatMap);
    }

    @Override
    public void removeCombatMap(CombatMap combatMap) {
        this.combatMaps.remove(combatMap);
        update();
    }

    @Override
    public void removeCombatMap(int index) {
        if (index > 0 && index < combatMaps.size()) {
            this.combatMaps.remove(index - 1);
            update();
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
        if (result >= 1) {
            this.combatDurationMinutes = result;
            update();
        }
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
        update();
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
        if (lobbyStatus == LobbyStatus.READY) {
            players.replaceAll((k, v) -> PlayerStatus.IN_COMBAT);
            this.currentCombat = new DeathmatchCombat(this, players.keySet(), combatDurationMinutes, lobbyProperties, combatMap, finalCombatMap);
            currentCombat.start();
            update();
        }
    }

    @Override
    public void setPlayerStatus(UUID uuid, PlayerStatus playerStatus) {
        update();
    }

    private void update() {
        checkStatus();
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
