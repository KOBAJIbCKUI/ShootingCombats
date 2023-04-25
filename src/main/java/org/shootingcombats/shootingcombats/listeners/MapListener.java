package org.shootingcombats.shootingcombats.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.shootingcombats.shootingcombats.ShootingCombats;
import org.shootingcombats.shootingcombats.lobby.Lobby;

import java.util.UUID;

public final class MapListener implements Listener {
    private final ShootingCombats plugin;

    public MapListener(ShootingCombats plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        UUID mover = event.getPlayer().getUniqueId();

        Lobby foundLobby = ShootingCombats.getLobbiesManager().getLobbies().stream()
                .filter(lobby -> lobby.isPlayerInLobby(mover))
                .filter(lobby -> lobby.getPlayerStatus(mover) == Lobby.PlayerStatus.IN_COMBAT)
                .findFirst()
                .orElse(null);

        if (foundLobby == null) {
            return;
        }

        if (foundLobby.getCurrentCombat().getCurrentCombatMap().isInRegion(event.getTo())) {
            return;
        }

        event.setCancelled(true);
    }
}
