package org.shootingcombats.shootingcombats;

import org.bukkit.Bukkit;
import org.shootingcombats.shootingcombats.util.Util;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class DeathmatchCombat implements Combat {

    private final Set<UUID> players;
    private final Set<UUID> spectators;
    private final Map<String, String> combatProperties;
    private final CombatMap combatMap, finalCombatMap;
    private final long minutesToEnd;
    private final Lobby lobby;

    public DeathmatchCombat(Lobby lobby, Set<UUID> players, long minutesToEnd, Map<String, String> properties, CombatMap combatMap) {
        this(lobby, players, minutesToEnd, properties, combatMap, null);
    }

    public DeathmatchCombat(Lobby lobby, Set<UUID> players, long minutesToEnd, Map<String, String> properties, CombatMap combatMap, CombatMap finalCombatMap) {
        this.lobby = lobby;
        this.combatMap = new SimpleCombatMap(combatMap.getName(), combatMap.getBound());
        this.finalCombatMap = finalCombatMap != null ? new SimpleCombatMap(finalCombatMap.getName(), finalCombatMap.getBound()) : null;
        this.minutesToEnd = minutesToEnd;
        this.combatProperties = properties;

        this.players = new HashSet<>(players);
        this.spectators = new HashSet<>();

    }
    @Override
    public void joinAsPlayer(UUID uuid) {
        throw new UnsupportedOperationException("Combat doesn't support joins");
    }

    @Override
    public void leaveAsPlayer(UUID uuid) {
        players.remove(uuid);
    }

    @Override
    public void joinAsSpectator(UUID uuid) {
        spectators.add(uuid);
    }

    @Override
    public void leaveAsSpectator(UUID uuid) {
        spectators.remove(uuid);
    }

    @Override
    public Set<UUID> getPlayers() {
        return Collections.unmodifiableSet(players);
    }

    @Override
    public Set<UUID> getSpectators() {
        return Collections.unmodifiableSet(spectators);
    }

    @Override
    public void onKill(UUID killer, UUID killed) {

    }

    @Override
    public void onQuit(UUID uuid) {

    }

    @Override
    public void start() {

    }

    @Override
    public void forcedStop() {

    }

    @Override
    public void normalStop() {

    }

    private void startCountdown() {

    }

    private void startMainCombat() {

    }

    private void startFinalCombat() {

    }



    private class CombatThread implements Runnable {
        private final int id;

        private long secondsLeft;
        private final long secondsToTags;
        private final long secondsToFinalCombat;
        private final long secondsToEnd;
        private boolean tagsShown;
        private boolean finalCombatStarted;

        public CombatThread(int id, long secondsToTags, long secondsToFinalCombat, long secondsToEnd) {
            this.id = id;
            this.secondsLeft = 0;
            this.secondsToTags = secondsToTags;
            this.secondsToFinalCombat = secondsToFinalCombat;
            this.secondsToEnd = secondsToEnd;
            this.tagsShown = false;
            this.finalCombatStarted = false;
        }

        @Override
        public void run() {
//            if (lobby.getCurrentCombat() == null || lobby.getLobbyStatus() != Lobby.LobbyStatus.RUNNING) {
//                stop();
//                return;
//            }
            secondsLeft += 10;
            //shootingBattle.getLobby().getBarData().updateBar(remainingTime - timer);
            //TODO: get values from config
            if (!tagsShown && secondsLeft >= secondsToTags && "true".equals(combatProperties.getOrDefault("endgame-tags", "true"))) {
                this.tagsShown = true;
                //Bukkit.getPluginManager().callEvent(new ShowNameTagsEvent(shootingBattle));
                //Util.sendMessage(shootingBattle.getBattlePlayerData().getAllPlayers(), String.format(Messages.MINUTES_TO_END, (this.remainingTime - this.showNameTagsTime) / 60));
                //Util.sendMessage(shootingBattle.getBattlePlayerData().getAllPlayers(), Messages.PLAYERS_VISIBLE);
            } else if (!finalCombatStarted && secondsLeft >= secondsToFinalCombat && "true".equals(combatProperties.getOrDefault("final-teleport", "true"))) {
                finalCombatStarted = true;
                startFinalCombat();
                //if (shootingBattle.getLobby().isTeleportToGulag()) {
                //    shootingBattle.startGulag();
                //    Util.sendTitle(shootingBattle.getBattlePlayerData().getPlayers(), Messages.WELCOME_TO_GULAG, Messages.TRAITORS);
                //}
                //shootingBattle.getLobby().getBarData().prepareForGulag(false);
                //Util.sendMessage(shootingBattle.getBattlePlayerData().getAllPlayers(), Messages.MINUTE_TO_END);
            } else if (secondsLeft >= secondsToEnd) {
                normalStop();
            }
        }

        public void stop() {
            Bukkit.getScheduler().cancelTask(id);
        }
    }

    private class CountdownThread implements Runnable {
        private final int id;
        private int countdownSeconds;

        public CountdownThread(int id, int countdownSeconds) {
            this.id = id;
            this.countdownSeconds = countdownSeconds;
        }

        @Override
        public void run() {
            if (countdownSeconds <= 0) {
                stop();
                startMainCombat();
            } else {
                //Util.sendMessage(shootingBattle.getBattlePlayerData().getAllPlayers(), String.format(Messages.BATTLE_STARTS_IN, timer));
            }
            countdownSeconds--;
        }

        public void stop() {
            Bukkit.getScheduler().cancelTask(id);
        }
    }
}
