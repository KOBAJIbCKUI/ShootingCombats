package org.shootingcombats.shootingcombats.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.shootingcombats.shootingcombats.ShootingCombats;
import org.shootingcombats.shootingcombats.combat.Combat;
import org.shootingcombats.shootingcombats.lobby.Lobby;

import java.util.Collections;
import java.util.UUID;

public final class CombatListener implements Listener {
    private final ShootingCombats plugin;

    public CombatListener(ShootingCombats plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeathByAttack(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player defenderPlayer = (Player) event.getEntity();
        Player damagerPlayer = event.getDamager() instanceof Player ? (Player) event.getDamager() : null;

        if (event.getFinalDamage() < defenderPlayer.getHealth()) {
            return;
        }

        Lobby foundLobby = ShootingCombats.getLobbiesManager().getLobbies().stream()
                .filter(lobby -> lobby.getLobbyStatus() == Lobby.LobbyStatus.RUNNING)
                .filter(lobby -> lobby.getCurrentCombat().getPlayers().contains(defenderPlayer.getUniqueId()))
                .findFirst().orElse(null);
        if (foundLobby == null) {
            return;
        }

        Combat foundCombat = foundLobby.getCurrentCombat();
        if (foundCombat == null) {
            return;
        }

        event.setCancelled(true);
        if (damagerPlayer == null) {
            processDeath(defenderPlayer.getUniqueId(), foundCombat);
        } else {
            processDeath(defenderPlayer.getUniqueId(), damagerPlayer.getUniqueId(), foundCombat);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeathByOtherSource(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player damagedPlayer = (Player) event.getEntity();

        if (event.getFinalDamage() < damagedPlayer.getHealth()) {
            return;
        }

        Lobby foundLobby = ShootingCombats.getLobbiesManager().getLobbies().stream()
                .filter(lobby -> lobby.getLobbyStatus() == Lobby.LobbyStatus.RUNNING)
                .filter(lobby -> lobby.getCurrentCombat().getPlayers().contains(damagedPlayer.getUniqueId()))
                .findFirst().orElse(null);
        if (foundLobby == null) {
            return;
        }

        Combat foundCombat = foundLobby.getCurrentCombat();
        if (foundCombat == null) {
            return;
        }

        event.setCancelled(true);
        processDeath(damagedPlayer.getUniqueId(), foundCombat);
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        Player quited = event.getPlayer();
        Lobby foundLobby = ShootingCombats.getLobbiesManager().getLobbies().stream()
                .filter(lobby -> lobby.getLobbyStatus() == Lobby.LobbyStatus.RUNNING)
                .filter(lobby -> lobby.getCurrentCombat().getPlayers().contains(quited.getUniqueId()))
                .findFirst().orElse(null);
        if (foundLobby == null) {
            return;
        }

        Combat foundCombat = foundLobby.getCurrentCombat();
        if (foundCombat != null) {
            foundCombat.onQuit(quited.getUniqueId());
        }
        foundLobby.leaveLobby(quited.getUniqueId());
    }

    private void processDeath(UUID killed, UUID killer, Combat combat) {
        combat.onKill(killer, killed);

        final String deathMessage = Bukkit.getPlayer(killed).getName() + " killed by " + Bukkit.getPlayer(killer).getName();

        // Call bukkit player death event so other plugins can pick up on that too
        PlayerDeathEvent playerDeathEvent = new PlayerDeathEvent(Bukkit.getPlayer(killed), Collections.emptyList(), 0, deathMessage);
        Bukkit.getPluginManager().callEvent(playerDeathEvent);
    }

    private void processDeath(UUID killed, Combat combat) {
        combat.onDeath(killed);

        String deathMessage = Bukkit.getPlayer(killed).getName() + " died somehow";

        // Call bukkit player death event so other plugins can pick up on that too
        PlayerDeathEvent playerDeathEvent = new PlayerDeathEvent(Bukkit.getPlayer(killed), Collections.emptyList(), 0, deathMessage);
        Bukkit.getPluginManager().callEvent(playerDeathEvent);
    }
}
