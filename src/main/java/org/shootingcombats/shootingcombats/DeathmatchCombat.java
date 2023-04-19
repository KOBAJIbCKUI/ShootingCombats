package org.shootingcombats.shootingcombats;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.shootingcombats.shootingcombats.data.CombatPlayerData;
import org.shootingcombats.shootingcombats.util.Messages;
import org.shootingcombats.shootingcombats.util.TypedProperty;
import org.shootingcombats.shootingcombats.util.Util;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class DeathmatchCombat implements Combat {
    private final Map<String, TypedProperty> combatProperties;
    private final CombatMap combatMap, finalCombatMap;
    private final long minutesToEnd;
    private final CombatPlayerData combatPlayerData;
    private final Lobby lobby;

    private final BukkitRunnable countdownRunnable, combatRunnable;

    public DeathmatchCombat(Lobby lobby, Set<UUID> players, long minutesToEnd, Map<String, TypedProperty> properties, CombatMap combatMap) {
        this(lobby, players, minutesToEnd, properties, combatMap, null);
    }

    public DeathmatchCombat(Lobby lobby, Set<UUID> players, long minutesToEnd, Map<String, TypedProperty> properties, CombatMap combatMap, CombatMap finalCombatMap) {
        this.lobby = lobby;
        this.combatMap = new SimpleCombatMap(combatMap.getName(), combatMap.getBound());
        this.finalCombatMap = finalCombatMap != null ? new SimpleCombatMap(finalCombatMap.getName(), finalCombatMap.getBound()) : null;
        this.minutesToEnd = minutesToEnd;
        this.combatProperties = properties;

        this.combatPlayerData = new CombatPlayerData(this);
        for (UUID uuid : players) {
            combatPlayerData.addPlayer(uuid);
        }

        this.countdownRunnable = new CountdownThread(5);
        this.combatRunnable = new CombatThread(TimeUnit.MINUTES.toSeconds(minutesToEnd - 5), TimeUnit.MINUTES.toSeconds(minutesToEnd - 1), TimeUnit.MINUTES.toSeconds(minutesToEnd));
    }

    @Override
    public void joinAsPlayer(UUID uuid) {
        throw new UnsupportedOperationException("Combat doesn't support joins");
    }

    @Override
    public void leaveAsPlayer(UUID uuid) {
        this.combatPlayerData.removePlayer(uuid);
    }

    @Override
    public void joinAsSpectator(UUID uuid) {
        this.combatPlayerData.addSpectator(uuid);
    }

    @Override
    public void leaveAsSpectator(UUID uuid) {
        this.combatPlayerData.removeSpectator(uuid);
    }

    @Override
    public Set<UUID> getPlayers() {
        return combatPlayerData.getPlayers().keySet();
    }

    @Override
    public Set<UUID> getSpectators() {
        return combatPlayerData.getSpectators();
    }

    @Override
    public CombatMap getCombatMap() {
        return this.combatMap;
    }

    @Override
    public void onKill(UUID killer, UUID killed) {

    }

    @Override
    public void onQuit(UUID uuid) {

    }

    @Override
    public void start() {
        startCountdown();
    }

    @Override
    public void forcedStop() {

    }

    @Override
    public void normalStop() {
        Set<UUID> players = combatPlayerData.getPlayers().keySet();
        Set<UUID> spectators = combatPlayerData.getSpectators();
        if (players.size() != 1) {
            Util.sendTitle(players, Messages.ROUND_DRAW);
            Util.sendTitle(spectators, Messages.ROUND_DRAW);
        } else {
            Util.sendTitle(players, "" + ChatColor.BOLD + ChatColor.RED + players, Messages.BATTLE_WON);
        }
    }

    private void startCountdown() {
        this.countdownRunnable.runTaskTimer(ShootingCombats.getPlugin(), 0, 1 * 20);
    }

    private void startMainCombat() {
        Util.sendTitle(combatPlayerData.getPlayers().keySet(), Messages.BATTLE_HAS_BEGUN);
        Util.sendMessage(combatPlayerData.getSpectators(), String.format(Messages.MINUTES_TO_END, minutesToEnd));
        Util.sendTitle(combatPlayerData.getPlayers().keySet(), Messages.BATTLE_HAS_BEGUN);
        Util.sendMessage(combatPlayerData.getSpectators(), String.format(Messages.MINUTES_TO_END, minutesToEnd));
        this.combatRunnable.runTask(ShootingCombats.getPlugin());
    }

    private void startFinalCombat() {

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
            //TODO: get values from config
            if (!tagsShown && secondsLeft >= secondsToTags && combatProperties.get("endgame-tags").getValue(Boolean.class).orElse(true)) {
                this.tagsShown = true;
                //Bukkit.getPluginManager().callEvent(new ShowNameTagsEvent(shootingBattle));
                Util.sendMessage(combatPlayerData.getPlayers().keySet(), String.format(Messages.MINUTES_TO_END, (secondsToEnd - secondsToTags) / 60));
                Util.sendMessage(combatPlayerData.getPlayers().keySet(), Messages.PLAYERS_VISIBLE);
                Util.sendMessage(combatPlayerData.getSpectators(), String.format(Messages.MINUTES_TO_END, (secondsToEnd - secondsToTags) / 60));
                Util.sendMessage(combatPlayerData.getSpectators(), Messages.PLAYERS_VISIBLE);
            }
            if (finalCombatMap != null && !finalCombatStarted && secondsLeft >= secondsToFinalCombat && combatProperties.get("final-teleport").getValue(Boolean.class).orElse(true)) {
                finalCombatStarted = true;
                startFinalCombat();
                Util.sendTitle(combatPlayerData.getPlayers().keySet(), Messages.WELCOME_TO_GULAG, Messages.TRAITORS);
                Util.sendTitle(combatPlayerData.getSpectators(), Messages.WELCOME_TO_GULAG, Messages.TRAITORS);
            }
            if (secondsLeft >= secondsToFinalCombat) {
                //shootingBattle.getLobby().getBarData().prepareForGulag(false);
                Util.sendMessage(combatPlayerData.getPlayers().keySet(), Messages.MINUTE_TO_END);
                Util.sendMessage(combatPlayerData.getSpectators(), Messages.MINUTE_TO_END);
            }
            if (secondsLeft >= secondsToEnd) {
                normalStop();
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
                Util.sendMessage(combatPlayerData.getPlayers().keySet(), String.format(Messages.BATTLE_STARTS_IN, countdownSeconds));
                Util.sendMessage(combatPlayerData.getSpectators(), String.format(Messages.BATTLE_STARTS_IN, countdownSeconds));
            }
            countdownSeconds--;
        }
    }
}
