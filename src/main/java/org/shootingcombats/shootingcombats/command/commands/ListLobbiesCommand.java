package org.shootingcombats.shootingcombats.command.commands;

import org.bukkit.command.CommandSender;
import org.shootingcombats.shootingcombats.ShootingCombats;
import org.shootingcombats.shootingcombats.command.abstraction.AbstractSingleCommand;
import org.shootingcombats.shootingcombats.lobby.Lobby;
import org.shootingcombats.shootingcombats.util.Util;

import java.util.*;

public final class ListLobbiesCommand extends AbstractSingleCommand {
    public ListLobbiesCommand() {
        super("Lobbies", "lobbies", "sc.lobbies");
    }

    @Override
    public boolean execute(ShootingCombats plugin, CommandSender commandSender, String target, String label, String[] args) {

        if (args.length != 0) {
            sendUsage(commandSender, label);
            return false;
        }

        List<Lobby> lobbiesList = ShootingCombats.getLobbiesManager().getLobbies();
        if (lobbiesList.isEmpty()) {
            Util.sendMessage(commandSender, "No lobbies found");
        } else {
            Util.sendMessage(commandSender, "Lobbies list:");
            for (Lobby lobby : lobbiesList) {
                Util.sendMessage(commandSender, lobby.getName() + " (" + lobby.getPlayersNumber() + "/" + lobby.getMaxPlayers() + ") - " + lobby.getType());
            }
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
