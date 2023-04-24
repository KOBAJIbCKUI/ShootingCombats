package org.shootingcombats.shootingcombats.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shootingcombats.shootingcombats.ShootingCombats;
import org.shootingcombats.shootingcombats.command.abstraction.AbstractSingleCommand;
import org.shootingcombats.shootingcombats.lobby.Lobby;
import org.shootingcombats.shootingcombats.util.Util;

import java.util.*;

public final class LobbiesCommand extends AbstractSingleCommand {
    public LobbiesCommand() {
        super("Lobbies", "lobbies", "sc.lobbies");
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

        List<Lobby> lobbiesList = ShootingCombats.getLobbiesManager().getLobbies();
        if (lobbiesList.isEmpty()) {
            Util.sendMessage(executor, "No lobbies found");
            return true;
        }

        Util.sendMessage(executor, "Lobbies list:");
        for (Lobby lobby : lobbiesList) {
            Util.sendMessage(executor, "Name: " + lobby.getName() + " Type: " + lobby.getType() + " Players: " + lobby.getPlayersNumber() + "/" + lobby.getMaxPlayers());
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
