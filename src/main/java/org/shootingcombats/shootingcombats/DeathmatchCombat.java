package org.shootingcombats.shootingcombats;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.shootingcombats.shootingcombats.data.CombatPlayerData;
import org.shootingcombats.shootingcombats.data.Config;
import org.shootingcombats.shootingcombats.util.Messages;
import org.shootingcombats.shootingcombats.util.TypedProperty;
import org.shootingcombats.shootingcombats.util.Util;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public final class DeathmatchCombat implements Combat {
    private final long minutesToEnd, minutesToTags, minutesToFinalTp;
    private final boolean endgameTags, finalTeleport, spectateAfterDeath;
    private final CombatPlayerData combatPlayerData;
    private final Lobby lobby;
    private final BukkitRunnable countdownRunnable, combatRunnable;
    private final CombatMap finalCombatMap;
    private CombatMap currentCombatMap;

    public DeathmatchCombat(DeathmatchLobby lobby, Set<UUID> players, long minutesToEnd, Map<String, TypedProperty> properties, CombatMap combatMap) {
        this(lobby, players, minutesToEnd, properties, combatMap, null);
    }

    public DeathmatchCombat(DeathmatchLobby lobby, Set<UUID> players, long minutesToEnd, Map<String, TypedProperty> properties, CombatMap combatMap, CombatMap finalCombatMap) {
        this.lobby = lobby;
        this.currentCombatMap = new SimpleCombatMap(combatMap.getName(), combatMap.getBound());
        this.finalCombatMap = finalCombatMap != null ? new SimpleCombatMap(finalCombatMap.getName(), finalCombatMap.getBound()) : null;
        this.minutesToEnd = minutesToEnd;
        this.minutesToTags = properties.get("minutes-to-tags").getValue(Integer.class).orElse(Config.dmMinutesToTags);
        this.minutesToFinalTp = properties.get("minutes-to-final-tp").getValue(Integer.class).orElse(Config.dmMinutesToFinalTp);
        this.endgameTags = properties.get("endgame-tags").getValue(Boolean.class).orElse(Config.dmEndgameTags);
        this.finalTeleport = properties.get("final-teleport").getValue(Boolean.class).orElse(Config.dmFinalTeleport);
        this.spectateAfterDeath = properties.get("spectate-after-death").getValue(Boolean.class).orElse(Config.dmDeathSpectate);

        this.combatPlayerData = new CombatPlayerData(this);
        for (UUID uuid : players) {
            combatPlayerData.addPlayer(uuid);
        }

        this.countdownRunnable = new CountdownThread(5);
        this.combatRunnable = new CombatThread(TimeUnit.MINUTES.toSeconds(minutesToTags), TimeUnit.MINUTES.toSeconds(minutesToFinalTp), TimeUnit.MINUTES.toSeconds(minutesToEnd));
    }

    @Override
    public void joinAsPlayer(UUID uuid) {
        throw new UnsupportedOperationException("Combat doesn't support joins");
    }

    @Override
    public void leaveAsPlayer(UUID uuid) {
        this.combatPlayerData.removePlayer(uuid);
        lobby.setPlayerStatus(uuid, Lobby.PlayerStatus.NOT_READY);
    }

    @Override
    public void joinAsSpectator(UUID uuid) {
        this.combatPlayerData.addSpectator(uuid);
        lobby.setPlayerStatus(uuid, Lobby.PlayerStatus.IN_COMBAT);
    }

    @Override
    public void leaveAsSpectator(UUID uuid) {
        this.combatPlayerData.removeSpectator(uuid);
        lobby.setPlayerStatus(uuid, Lobby.PlayerStatus.NOT_READY);
    }

    @Override
    public Set<UUID> getPlayers() {
        return combatPlayerData.getAlivePlayers();
    }

    @Override
    public Set<UUID> getSpectators() {
        return combatPlayerData.getSpectators();
    }

    @Override
    public CombatMap getCurrentCombatMap() {
        return this.currentCombatMap;
    }

    @Override
    public void onKill(UUID killer, UUID killed) {
        combatPlayerData.addKill(killer);
        combatPlayerData.markPlayerAsKilled(killed);
        leaveAsPlayer(killed);

        Player player = Bukkit.getPlayer(killed);
        if (spectateAfterDeath) {
            Location deathLocation = player.getLocation();
            joinAsSpectator(killed);
            player.spigot().respawn();
            player.teleport(deathLocation);
        } else {
            player.spigot().respawn();
            player.teleport(lobby.getLobbySpawn());
        }

        if (checkWinConditions()) {
            normalStop();
        }
    }

    @Override
    public void onQuit(UUID quited) {
        combatPlayerData.markPlayerAsQuited(quited);
        leaveAsPlayer(quited);

        if (checkWinConditions()) {
            normalStop();
        }
    }

    @Override
    public void start() {
        startCountdown();
    }

    @Override
    public void forcedStop() {
        stopTasks();
        Set<UUID> players = combatPlayerData.getAlivePlayers();
        Set<UUID> spectators = combatPlayerData.getSpectators();
        Util.sendTitle(players, Messages.BATTLE_STOPPED);
        Util.sendTitle(spectators, Messages.BATTLE_STOPPED);
        for (UUID uuid : players) {
            leaveAsPlayer(uuid);
        }
        for (UUID uuid : spectators) {
            leaveAsSpectator(uuid);
        }
    }

    @Override
    public void normalStop() {
        Set<UUID> players = combatPlayerData.getAlivePlayers();
        Set<UUID> spectators = combatPlayerData.getSpectators();
        if (players.size() != 1) {
            Util.sendTitle(players, Messages.ROUND_DRAW);
            Util.sendTitle(spectators, Messages.ROUND_DRAW);
        } else {
            Util.sendTitle(players, "" + ChatColor.BOLD + ChatColor.RED + players, Messages.BATTLE_WON);
        }
        combatPlayerData.rewardPlayers();

        for (UUID uuid : players) {
            leaveAsPlayer(uuid);
        }
        for (UUID uuid : spectators) {
            leaveAsSpectator(uuid);
        }

    }

    private void startCountdown() {
        combatPlayerData.restoreHealth();
        combatPlayerData.freezePlayers();
        spawn(combatPlayerData.getAlivePlayers(), currentCombatMap.getSpawns());

        this.countdownRunnable.runTaskTimer(ShootingCombats.getPlugin(), 0, 1 * 20);

    }

    private void startMainCombat() {
        combatPlayerData.unfreezePlayers();
        Util.sendTitle(combatPlayerData.getAlivePlayers(), Messages.BATTLE_HAS_BEGUN);
        Util.sendMessage(combatPlayerData.getSpectators(), String.format(Messages.MINUTES_TO_END, minutesToEnd));
        Util.sendTitle(combatPlayerData.getAlivePlayers(), Messages.BATTLE_HAS_BEGUN);
        Util.sendMessage(combatPlayerData.getSpectators(), String.format(Messages.MINUTES_TO_END, minutesToEnd));

        this.combatRunnable.runTask(ShootingCombats.getPlugin());
    }

    private void startFinalCombat() {
        this.currentCombatMap = this.finalCombatMap;
        spawn(combatPlayerData.getAlivePlayers(), currentCombatMap.getSpawns());
        Util.sendTitle(combatPlayerData.getAlivePlayers(), Messages.WELCOME_TO_GULAG, Messages.TRAITORS);
        Util.sendTitle(combatPlayerData.getSpectators(), Messages.WELCOME_TO_GULAG, Messages.TRAITORS);
    }

    private void stopTasks() {
        countdownRunnable.cancel();
        combatRunnable.cancel();
    }

    private void spawn(UUID uuid, Location location) {
        Bukkit.getPlayer(uuid).teleport(location);
    }

    private void spawn(Set<UUID> uuids, List<Location> locations) {
        Collections.shuffle(locations, ThreadLocalRandom.current());
        for (UUID uuid : uuids) {
            Bukkit.getPlayer(uuid).teleport(locations.remove(0));
        }
    }

    private boolean checkWinConditions() {
        return combatPlayerData.getAlivePlayers().size() <= 1;
    }

    private class CombatThread extends BukkitRunnable {
        private long secondsLeft;
        private final long secondsToTags;
        private final long secondsToFinalCombat;
        private final long secondsToEnd;
        private boolean tagsShown;
        private boolean finalCombatStarted;

        public CombatThread(long secondsToTags, long secondsToFinalCombat, long secondsToEnd) {
            this.secondsLeft = 0;
            this.secondsToTags = secondsToTags;
            this.secondsToFinalCombat = secondsToFinalCombat;
            this.secondsToEnd = secondsToEnd;
            this.tagsShown = false;
            this.finalCombatStarted = false;
        }

        @Override
        public void run() {
            secondsLeft += 10;
            //shootingBattle.getLobby().getBarData().updateBar(remainingTime - timer);
            if (endgameTags && !tagsShown && secondsLeft >= secondsToTags) {
                this.tagsShown = true;
                //Bukkit.getPluginManager().callEvent(new ShowNameTagsEvent(shootingBattle));
                Util.sendMessage(combatPlayerData.getAlivePlayers(), String.format(Messages.MINUTES_TO_END, (secondsToEnd - secondsToTags) / 60));
                Util.sendMessage(combatPlayerData.getAlivePlayers(), Messages.PLAYERS_VISIBLE);
                Util.sendMessage(combatPlayerData.getSpectators(), String.format(Messages.MINUTES_TO_END, (secondsToEnd - secondsToTags) / 60));
                Util.sendMessage(combatPlayerData.getSpectators(), Messages.PLAYERS_VISIBLE);
            }
            if (finalTeleport && !finalCombatStarted && secondsLeft >= secondsToFinalCombat) {
                finalCombatStarted = true;
                startFinalCombat();
            }
            if (secondsLeft >= secondsToFinalCombat) {
                //shootingBattle.getLobby().getBarData().prepareForGulag(false);
                Util.sendMessage(combatPlayerData.getAlivePlayers(), String.format(Messages.MINUTES_TO_END, (secondsToEnd - secondsToFinalCombat) / 60));
                Util.sendMessage(combatPlayerData.getSpectators(), String.format(Messages.MINUTES_TO_END, (secondsToEnd - secondsToFinalCombat) / 60));
            }
            if (secondsLeft >= secondsToEnd) {
                normalStop();
                cancel();
            }
        }
    }

    private class CountdownThread extends BukkitRunnable {
        private int countdownSeconds;

        public CountdownThread(int countdownSeconds) {
            this.countdownSeconds = countdownSeconds;
        }

        @Override
        public void run() {
            if (countdownSeconds <= 0) {
                startMainCombat();
                cancel();
            } else {
                Util.sendMessage(combatPlayerData.getPlayers(), String.format(Messages.BATTLE_STARTS_IN, countdownSeconds));
                Util.sendMessage(combatPlayerData.getSpectators(), String.format(Messages.BATTLE_STARTS_IN, countdownSeconds));
            }
            countdownSeconds--;
        }
    }
}
