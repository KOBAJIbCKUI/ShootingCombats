package org.shootingcombats.shootingcombats.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.shootingcombats.shootingcombats.ShootingCombats;
import org.shootingcombats.shootingcombats.combat.Combat;
import org.shootingcombats.shootingcombats.lobby.Lobby;
import org.shootingcombats.shootingcombats.util.Util;

import java.util.Collections;
import java.util.UUID;

public final class CombatListener implements Listener {
    private final ShootingCombats plugin;

    public CombatListener(ShootingCombats plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.LOWEST)
    private void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        UUID executor = event.getPlayer().getUniqueId();
        if (ShootingCombats.getLobbiesManager().getLobbies().stream().filter(lobby -> lobby.isPlayerInLobby(executor) && lobby.getPlayerStatus(executor) == Lobby.PlayerStatus.IN_COMBAT).findFirst().orElse(null) == null) {
            return;
        }

        String message = event.getMessage();

        if (message.startsWith("/sc combat leave") || message.startsWith("/sc combat stop")) {
            return;
        }
        event.setMessage("/");
        event.setCancelled(true);
        Util.sendMessage(executor, "You cannot use this command during combat!");
    }

    @EventHandler
    public void onProjectileLaunched(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() != null) {
            return;
        }
        Location projectileLocation = event.getEntity().getLocation();
        Location playerLocation;
        Player closestPlayer = null;
        double distance = Double.MAX_VALUE;
        for (Player player : Bukkit.getOnlinePlayers()) {
            playerLocation = player.getLocation();
            double newDistance = Math.sqrt(Math.pow(projectileLocation.getX() - playerLocation.getX(), 2) + Math.pow(projectileLocation.getY() - playerLocation.getY(), 2) + Math.pow(projectileLocation.getZ() - playerLocation.getZ(), 2));
            if (newDistance < distance) {
                distance = newDistance;
                closestPlayer = player;
            }
        }
        event.getEntity().setShooter(closestPlayer);
        Util.log("Called projectile launch event for " + event.getEntity());
        Util.log("Shooter is " + event.getEntity().getShooter());
    }

//    @EventHandler
//    public void onExplosion(EntityExplodeEvent event) {
//        Util.log("Entity explode event called");
//    }
//
//    @EventHandler
//    public void onExplosionPrime(ExplosionPrimeEvent event) {
//        Util.log("Entity explode prime event called");
//    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeathByAttack(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player defenderPlayer = (Player) event.getEntity();
        Player damagerPlayer = null;

        if (event.getDamager() instanceof Player) {
            damagerPlayer = (Player) event.getDamager();
            Util.log("Damager is Player");
        }
        if (event.getDamager() instanceof Projectile) {
            Util.log("Damager is Projectile");
            Projectile projectile = (Projectile) event.getDamager();

            Util.log("Projectile source is " + projectile.getShooter());
            if (projectile.getShooter() instanceof Player) {
                damagerPlayer = (Player) projectile.getShooter();
                Util.log("Damager source is Player");
            }
        }

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

        if (event instanceof EntityDamageByEntityEvent) {
            //Util.log("Entity damage by entity event skipped");
            return;
        }

//        Util.log("Entity damage event called");

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player damagedPlayer = (Player) event.getEntity();

//        Util.log("Source of damage: " + event.getCause());

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
                .filter(lobby -> lobby.isPlayerInLobby(quited.getUniqueId()))
                .findFirst()
                .orElse(null);

        Combat foundCombat = foundLobby != null ? foundLobby.getCurrentCombat() : null;

        if (foundCombat != null) {
            foundCombat.onQuit(quited.getUniqueId());
        }

        if (foundLobby != null) {
            foundLobby.leaveLobby(quited.getUniqueId());
        }

    }

    private void processDeath(UUID killed, UUID killer, Combat combat) {
//        Util.log("Processing kill");
        combat.onKill(killer, killed);

        final String deathMessage = Bukkit.getPlayer(killed).getName() + " killed by " + Bukkit.getPlayer(killer).getName();

        // Call bukkit player death event so other plugins can pick up on that too
        PlayerDeathEvent playerDeathEvent = new PlayerDeathEvent(Bukkit.getPlayer(killed), Collections.emptyList(), 0, deathMessage);
        Bukkit.getPluginManager().callEvent(playerDeathEvent);
    }

    private void processDeath(UUID killed, Combat combat) {
//        Util.log("Processing death");
        combat.onDeath(killed);

        String deathMessage = Bukkit.getPlayer(killed).getName() + " died somehow";

        // Call bukkit player death event so other plugins can pick up on that too
        PlayerDeathEvent playerDeathEvent = new PlayerDeathEvent(Bukkit.getPlayer(killed), Collections.emptyList(), 0, deathMessage);
        Bukkit.getPluginManager().callEvent(playerDeathEvent);
    }
}
