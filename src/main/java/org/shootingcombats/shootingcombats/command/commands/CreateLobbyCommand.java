package org.shootingcombats.shootingcombats.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shootingcombats.shootingcombats.ShootingCombats;
import org.shootingcombats.shootingcombats.command.abstraction.AbstractSingleCommand;
import org.shootingcombats.shootingcombats.lobby.DeathmatchLobby;
import org.shootingcombats.shootingcombats.lobby.Lobby;
import org.shootingcombats.shootingcombats.manager.LobbiesManager;
import org.shootingcombats.shootingcombats.util.Util;

import java.util.*;
import java.util.stream.Collectors;

public final class CreateLobbyCommand extends AbstractSingleCommand {
    public CreateLobbyCommand() {
        super("CreateLobby", "createlobby <type> <name>", "sc.createlobby");
    }

    @Override
    public boolean execute(ShootingCombats plugin, CommandSender commandSender, String target, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("This command can be executed only by players!");
            return false;
        }

        UUID executor = ((Player) commandSender).getUniqueId();
        if (hasPermission(commandSender)) {
            Util.sendMessage(executor, "You have no permission to use this command!");
            return false;
        }

        if (args.length != 1) {
            sendUsage(commandSender, label);
            return false;
        }

        String lobbyName = args[0];
        if (lobbyName.trim().isEmpty()) {
            Util.sendMessage(executor, "Lobby name cannot be empty!");
            return false;
        }

        LobbiesManager lobbiesManager = ShootingCombats.getLobbiesManager();

        if (lobbiesManager.containsLobby(lobbyName)) {
            Util.sendMessage(executor, "Lobby with name " + lobbyName + " already exists!");
            return false;
        }

        new DeathmatchLobby(lobbyName, executor, lobbiesManager);

        return true;
    }

    @Override
    public List<String> tabComplete(ShootingCombats plugin, CommandSender commandSender, String[] args) {

        String partialArg;
        int lastIndex = 0;

        if (args.length == 0 || (partialArg = args[lastIndex = args.length - 1]).trim().isEmpty() ) {
            switch (lastIndex) {
                case 0: {
                    return Arrays.stream(Lobby.LobbyType.values())
                            .map(Lobby.LobbyType::toString)
                            .collect(Collectors.toList());
                }
                case 1: {
                    return ShootingCombats.getLobbiesManager().getLobbies().stream()
                            .map(Lobby::getName)
                            .collect(Collectors.toList());
                }
                default: throw new AssertionError("Reached not reachable code");
            }
        }
        switch (lastIndex) {
            case 0: {
                return Arrays.stream(Lobby.LobbyType.values())
                        .map(Lobby.LobbyType::toString)
                        .filter(typeName -> typeName.toLowerCase(Locale.ROOT).startsWith(partialArg.toLowerCase(Locale.ROOT)))
                        .collect(Collectors.toList());
            }
            case 1: {
                return ShootingCombats.getLobbiesManager().getLobbies().stream()
                        .map(Lobby::getName)
                        .filter(lobbyName -> lobbyName.toLowerCase(Locale.ROOT).startsWith(partialArg.toLowerCase(Locale.ROOT)))
                        .collect(Collectors.toList());
            }
            default: throw new AssertionError("Reached not reachable code");
        }
    }

    @Override
    public void sendUsage(CommandSender commandSender, String label) {
        commandSender.sendMessage("Usage for command " + getName() + ": " + getUsage());
    }
}
