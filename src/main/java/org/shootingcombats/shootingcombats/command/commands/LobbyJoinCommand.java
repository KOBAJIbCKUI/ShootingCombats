package org.shootingcombats.shootingcombats.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shootingcombats.shootingcombats.ShootingCombats;
import org.shootingcombats.shootingcombats.command.abstraction.AbstractSingleCommand;
import org.shootingcombats.shootingcombats.lobby.Lobby;
import org.shootingcombats.shootingcombats.manager.LobbiesManager;
import org.shootingcombats.shootingcombats.util.Util;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

public final class LobbyJoinCommand extends AbstractSingleCommand {

    public LobbyJoinCommand() {
        super("Join", "join <name>", "sc.lobby.join");
    }

    @Override
    public boolean execute(ShootingCombats plugin, CommandSender commandSender, String target, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("This command can be executed only by players!");
            return false;
        }

        UUID executor = ((Player) commandSender).getUniqueId();
        if (args.length != 1) {
            sendUsage(commandSender, label);
            return false;
        }

        String lobbyName = args[0];
        LobbiesManager lobbiesManager = ShootingCombats.getLobbiesManager();
        if (!lobbiesManager.containsLobby(lobbyName)) {
            Util.sendMessage(executor, "Lobby with name " + lobbyName + " not found!");
            return false;
        }

        Lobby foundLobby = lobbiesManager.getLobbies().stream()
                .filter(lobby -> lobby.isPlayerInLobby(executor))
                .findFirst()
                .orElse(null);
        if (foundLobby != null) {
            Util.sendMessage(executor, "You cannot join this lobby because you are in another one!");
            return false;
        }

        lobbiesManager.getLobby(lobbyName).get().joinLobby(executor);

        return true;
    }

    @Override
    public List<String> tabComplete(ShootingCombats plugin, CommandSender commandSender, String[] args) {
        String partialArg;
        int lastIndex = 0;

        if (args.length == 0 || (partialArg = args[lastIndex = args.length - 1]).trim().isEmpty()) {

            return ShootingCombats.getLobbiesManager().getLobbies().stream()
                    .map(Lobby::getName)
                    .collect(Collectors.toList());
        }
        return ShootingCombats.getLobbiesManager().getLobbies().stream()
                .map(Lobby::getName)
                .filter(lobbyName -> lobbyName.toLowerCase(Locale.ROOT).startsWith(partialArg.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
    }

    @Override
    public void sendUsage(CommandSender commandSender, String label) {
        commandSender.sendMessage("Usage for command " + getName() + ": " + getUsage());
    }
}
