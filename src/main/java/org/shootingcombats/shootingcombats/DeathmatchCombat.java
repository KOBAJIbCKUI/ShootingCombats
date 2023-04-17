package org.shootingcombats.shootingcombats;

import org.bukkit.Bukkit;

import java.util.*;
import java.util.function.Consumer;

public class DeathmatchCombat implements Combat {

    private final Set<UUID> players;
    private final Set<UUID> spectators;
    private final List<Consumer<Lobby>> preCombatActions, innerCombatActions, postCombatActions;
    private final Runnable combatThread;
    private final Lobby lobby;

    public DeathmatchCombat(Lobby lobby, Set<UUID> players, List<Consumer<Lobby>> preCombatActions, List<Consumer<Lobby>> innerCombatActions, List<Consumer<Lobby>> postCombatActions, Runnable combatThread) {
        this.lobby = lobby;
        this.players = new HashSet<>(players);
        this.combatThread = combatThread;
        this.preCombatActions = preCombatActions;
        this.innerCombatActions = innerCombatActions;
        this.postCombatActions = postCombatActions;

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
    public Iterable<UUID> getPlayers() {
        return Collections.unmodifiableSet(players);
    }

    @Override
    public Iterable<UUID> getSpectators() {
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
        callPreCombatActions();
        Bukkit.getScheduler().runTask(ShootingCombats.getPlugin(), combatThread);
    }

    @Override
    public void forcedStop() {
        callPostCombatActions();
    }

    @Override
    public void normalStop() {
        callPostCombatActions();
    }

    @Override
    public void callPreCombatActions() {
        for (Consumer<Lobby> action : preCombatActions) {
            action.accept(lobby);
        }
    }

    @Override
    public void callInnerCombatActions() {
        for (Consumer<Lobby> action : innerCombatActions) {
            action.accept(lobby);
        }
    }

    @Override
    public void callPostCombatActions() {
        for (Consumer<Lobby> action : postCombatActions) {
            action.accept(lobby);
        }
    }
}
