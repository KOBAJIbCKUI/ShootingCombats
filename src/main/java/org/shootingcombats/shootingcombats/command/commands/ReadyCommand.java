package org.shootingcombats.shootingcombats.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shootingcombats.shootingcombats.ShootingCombats;
import org.shootingcombats.shootingcombats.command.abstraction.AbstractSingleCommand;
import org.shootingcombats.shootingcombats.lobby.Lobby;
import org.shootingcombats.shootingcombats.util.Util;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class ReadyCommand extends AbstractSingleCommand {
    public ReadyCommand() {
        super("Ready", "ready", "sc.ready");
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

        Lobby foundLobby = ShootingCombats.getLobbiesManager().getLobbies().stream()
                .filter(lobby -> lobby.isPlayerInLobby(executor))
                .findFirst()
                .orElse(null);

        if (foundLobby == null) {
            Util.sendMessage(executor, "This command can be used only in lobby!");
            return false;
        }

        if (foundLobby.getPlayerStatus(executor) == Lobby.PlayerStatus.READY) {
            foundLobby.setPlayerStatus(executor, Lobby.PlayerStatus.NOT_READY);
            return true;
        }
        if (foundLobby.getPlayerStatus(executor) == Lobby.PlayerStatus.NOT_READY) {
            foundLobby.setPlayerStatus(executor, Lobby.PlayerStatus.READY);
            return true;
        }
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
