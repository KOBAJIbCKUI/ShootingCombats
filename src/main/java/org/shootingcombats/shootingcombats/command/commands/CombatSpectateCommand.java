package org.shootingcombats.shootingcombats.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shootingcombats.shootingcombats.ShootingCombats;
import org.shootingcombats.shootingcombats.combat.Combat;
import org.shootingcombats.shootingcombats.command.abstraction.AbstractSingleCommand;
import org.shootingcombats.shootingcombats.lobby.Lobby;
import org.shootingcombats.shootingcombats.manager.LobbiesManager;
import org.shootingcombats.shootingcombats.util.Util;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CombatSpectateCommand extends AbstractSingleCommand {
    public CombatSpectateCommand() {
        super("Spectate", "spectate", "sc.combat.spectate");
    }

    @Override
    public boolean execute(ShootingCombats plugin, CommandSender commandSender, String target, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("This command can be executed only by players!");
            return false;
        }

        UUID executor = ((Player) commandSender).getUniqueId();
        if (args.length != 0) {
            sendUsage(commandSender, label);
            return false;
        }

        LobbiesManager lobbiesManager = ShootingCombats.getLobbiesManager();
        Lobby foundLobby = lobbiesManager.getLobbies().stream()
                .filter(lobby -> lobby.isPlayerInLobby(executor))
                .findFirst()
                .orElse(null);
        if (foundLobby == null) {
            Util.sendMessage(executor, "This command can be used only in lobby!");
            return false;
        }

        if (foundLobby.getLobbyStatus() != Lobby.LobbyStatus.RUNNING) {
            Util.sendMessage(executor, "There is no combat in this lobby!");
            return false;
        }

        if (foundLobby.getPlayerStatus(executor) == Lobby.PlayerStatus.IN_COMBAT) {
            Util.sendMessage(executor, "You are already in combat!");
            return false;
        }

        Combat combat = foundLobby.getCurrentCombat();
        combat.joinAsSpectator(executor);

        Util.sendMessage(commandSender, "You successfully started spectating combat!");

        return true;
    }

    @Override
    public List<String> tabComplete(ShootingCombats plugin, CommandSender commandSender, String[] args) {
       return Collections.emptyList();
    }

    @Override
    public void sendUsage(CommandSender commandSender, String label) {
        commandSender.sendMessage("Usage for command " + getName() + ": " + getUsage());
    }
}
