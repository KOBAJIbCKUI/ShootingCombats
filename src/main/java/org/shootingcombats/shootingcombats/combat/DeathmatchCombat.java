package org.shootingcombats.shootingcombats.combat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.shootingcombats.shootingcombats.bar.DmCombatBar;
import org.shootingcombats.shootingcombats.board.DmCombatBoard;
import org.shootingcombats.shootingcombats.ShootingCombats;
import org.shootingcombats.shootingcombats.data.DmCombatPlayerData;
import org.shootingcombats.shootingcombats.data.PluginConfig;
import org.shootingcombats.shootingcombats.lobby.DeathmatchLobby;
import org.shootingcombats.shootingcombats.map.CombatMap;
import org.shootingcombats.shootingcombats.map.SimpleCombatMap;
import org.shootingcombats.shootingcombats.util.Messages;
import org.shootingcombats.shootingcombats.util.TypedProperty;
import org.shootingcombats.shootingcombats.util.Util;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public final class DeathmatchCombat implements Combat {
    private final long minutesToEnd, minutesToTags, minutesToFinalTp;
    private final boolean endgameTags, finalTeleport, spectateAfterDeath;
    private final DmCombatPlayerData combatPlayerData;
    private final DeathmatchLobby lobby;
    private final BukkitRunnable countdownRunnable, combatRunnable;
    private final DmCombatBoard combatBoard;
    private final DmCombatBar combatBar;
    private final CombatMap currentCombatMap;

    public DeathmatchCombat(DeathmatchLobby lobby, Set<UUID> players, long minutesToEnd, Map<String, TypedProperty> properties, CombatMap combatMap) {
        this(lobby, players, minutesToEnd, properties, combatMap, null);
    }

    public DeathmatchCombat(DeathmatchLobby lobby, Set<UUID> players, long minutesToEnd, Map<String, TypedProperty> properties, CombatMap combatMap, CombatMap finalCombatMap) {
        this.lobby = lobby;
        this.currentCombatMap = new SimpleCombatMap(combatMap.getName(), combatMap.getBound());
        this.minutesToEnd = minutesToEnd;
        this.minutesToTags = properties.get("minutes-to-tags").getValue(Integer.class).orElse(PluginConfig.dmMinutesToTags);
        this.minutesToFinalTp = properties.get("minutes-to-final-tp").getValue(Integer.class).orElse(PluginConfig.dmMinutesToFinalTp);
        this.endgameTags = properties.get("endgame-tags").getValue(Boolean.class).orElse(PluginConfig.dmEndgameTags);
        this.finalTeleport = properties.get("final-teleport").getValue(Boolean.class).orElse(PluginConfig.dmFinalTeleport);
        this.spectateAfterDeath = properties.get("spectate-after-death").getValue(Boolean.class).orElse(PluginConfig.dmDeathSpectate);

        this.combatPlayerData = new DmCombatPlayerData(this);
        this.combatBoard = new DmCombatBoard();
        this.combatBar = new DmCombatBar(minutesToEnd);
        for (UUID uuid : players) {
            lobby.prepareForCombat(uuid);
            combatBoard.addPlayerToBoard(uuid);
            combatBoard.addPlayer(uuid);
            combatBar.addPlayerToBar(uuid);
            combatPlayerData.addPlayer(uuid);
        }

        this.countdownRunnable = new CountdownThread(5);
        this.combatRunnable = new CombatThread(TimeUnit.MINUTES.toSeconds(minutesToTags), TimeUnit.MINUTES.toSeconds(minutesToFinalTp), TimeUnit.MINUTES.toSeconds(minutesToEnd));
    }

    @Override
    public void joinAsPlayer(UUID uuid) {
        Util.sendMessage(uuid, "Deathmatches doesn't support joins during combat");
    }

    @Override
    public void leaveAsPlayer(UUID uuid) {
        combatBoard.removePlayerFromBoard(uuid);
        combatBar.removePlayerFromBar(uuid);
        lobby.unprepareFromCombat(uuid);
        this.combatPlayerData.removePlayer(uuid);
        Bukkit.getPlayer(uuid).teleport(lobby.getLobbySpawn());
    }

    @Override
    public void joinAsSpectator(UUID uuid) {
        lobby.prepareForCombat(uuid);
        combatBoard.addPlayerToBoard(uuid);
        combatBoard.addSpectator(uuid);
        combatBar.removePlayerFromBar(uuid);
        combatPlayerData.addSpectator(uuid);
    }

    @Override
    public void leaveAsSpectator(UUID uuid) {
        combatBoard.removePlayerFromBoard(uuid);
        combatBar.removePlayerFromBar(uuid);
        lobby.unprepareFromCombat(uuid);
        this.combatPlayerData.removeSpectator(uuid);
        Bukkit.getPlayer(uuid).teleport(lobby.getLobbySpawn());
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

        Player player = Bukkit.getPlayer(killed);
        if (spectateAfterDeath) {
            replacePlayerToSpectators(killed);
        } else {
            leaveAsPlayer(killed);
            player.teleport(lobby.getLobbySpawn());
        }

        String deathMessage = Bukkit.getPlayer(killed).getName() + " killed by " + Bukkit.getPlayer(killer).getName();
        Util.sendMessage(combatPlayerData.getAlivePlayers(), deathMessage);
        Util.sendMessage(combatPlayerData.getSpectators(), deathMessage);

        if (checkWinConditions()) {
            normalStop();
        }
    }

    @Override
    public void onDeath(UUID killed) {
        combatPlayerData.markPlayerAsKilled(killed);

        Player player = Bukkit.getPlayer(killed);
        if (spectateAfterDeath) {
            replacePlayerToSpectators(killed);
        } else {
            leaveAsPlayer(killed);
            player.teleport(lobby.getLobbySpawn());
        }

        String deathMessage = Bukkit.getPlayer(killed).getName() + " died somehow";
        Util.sendMessage(combatPlayerData.getAlivePlayers(), deathMessage);
        Util.sendMessage(combatPlayerData.getSpectators(), deathMessage);

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
        Set<UUID> players = new HashSet<>(combatPlayerData.getAlivePlayers());
        Set<UUID> spectators = new HashSet<>(combatPlayerData.getSpectators());
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
        Set<UUID> players = new HashSet<>(combatPlayerData.getAlivePlayers());
        Set<UUID> spectators = new HashSet<>(combatPlayerData.getSpectators());
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

    private void replacePlayerToSpectators(UUID uuid) {
        combatPlayerData.replacePlayerToSpectators(uuid);
        combatBoard.addSpectator(uuid);
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

        this.combatRunnable.runTaskTimer(ShootingCombats.getPlugin(), 0, 1 * 20);
    }

    private void startFinalCombat() {
        Location finalLocation = currentCombatMap.getSpawns().get(ThreadLocalRandom.current().nextInt(currentCombatMap.spawnsNumber()));
        for (UUID uuid : combatPlayerData.getAlivePlayers()) {
            spawn(uuid, finalLocation);
        }
        //TODO: change message
        Util.sendTitle(combatPlayerData.getAlivePlayers(), Messages.FINAL_BATTLE);
        Util.sendTitle(combatPlayerData.getSpectators(), Messages.FINAL_BATTLE);
    }

    private void stopTasks() {
        countdownRunnable.cancel();
        combatRunnable.cancel();
    }

    private void spawn(UUID uuid, Location location) {
        Bukkit.getPlayer(uuid).teleport(location);
    }

    private void spawn(Set<UUID> uuids, List<Location> locations) {
        if (locations.size() != 0) {
            Collections.shuffle(locations, ThreadLocalRandom.current());
            int index = 0;
            for (UUID uuid : uuids) {
                if (index >= locations.size()) {
                    index = 0;
                }
                Bukkit.getPlayer(uuid).teleport(locations.get(index));
                index++;
            }
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
        private boolean tagsShown, finalCombatStarted;

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
            secondsLeft += 1;
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
            combatBar.setSecondsLeft(secondsLeft);
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
