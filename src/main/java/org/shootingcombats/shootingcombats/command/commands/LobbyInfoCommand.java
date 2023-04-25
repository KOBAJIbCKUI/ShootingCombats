package org.shootingcombats.shootingcombats.command.commands;

import org.bukkit.command.CommandSender;
import org.shootingcombats.shootingcombats.ShootingCombats;
import org.shootingcombats.shootingcombats.command.abstraction.AbstractSingleCommand;
import org.shootingcombats.shootingcombats.lobby.Lobby;
import org.shootingcombats.shootingcombats.manager.LobbiesManager;
import org.shootingcombats.shootingcombats.util.TypedProperty;
import org.shootingcombats.shootingcombats.util.Util;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class LobbyInfoCommand extends AbstractSingleCommand {
    public LobbyInfoCommand() {
        super("Info", "info", "sc.lobby.info");
    }

    @Override
    public boolean execute(ShootingCombats plugin, CommandSender commandSender, String target, String label, String[] args) {
        if (args.length != 0) {
            sendUsage(commandSender, label);
            return false;
        }

        LobbiesManager lobbiesManager = ShootingCombats.getLobbiesManager();
        if (!lobbiesManager.containsLobby(target)) {
            Util.sendMessage(commandSender, "Lobby with name " + target + " not found!");
            return false;
        }

        Lobby lobby = lobbiesManager.getLobby(target).get();

        Util.sendMessage(commandSender, "Lobby name: " + lobby.getName());
        Util.sendMessage(commandSender, "Lobby type: " + lobby.getType());
        Util.sendMessage(commandSender, "Status: " + lobby.getLobbyStatus());
        Util.sendMessage(commandSender, "Players: " + lobby.getPlayers() + "/" + lobby.getMaxPlayers());
        Util.sendMessage(commandSender, "Settings:");
        for (Map.Entry<String, TypedProperty> entry : lobby.getProperties().entrySet()) {
            Util.sendMessage(commandSender, entry.getKey() + " - " + entry.getValue().getValue(entry.getValue().getValueClass()));
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
