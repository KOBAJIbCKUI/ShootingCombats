package org.shootingcombats.shootingcombats.manager;

import org.bukkit.command.*;
import org.shootingcombats.shootingcombats.ShootingCombats;
import org.shootingcombats.shootingcombats.command.abstraction.ICommand;
import org.shootingcombats.shootingcombats.command.commands.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class PluginCommandExecutor implements CommandExecutor, TabCompleter {

    private final ShootingCombats plugin;
    private final PluginCommand mainPluginCommand;
    private final List<ICommand> mainCommands;

    public PluginCommandExecutor(ShootingCombats plugin, PluginCommand  mainPluginCommand) {
        this.plugin = plugin;
        this.mainPluginCommand =  mainPluginCommand;

        mainCommands = Arrays.asList(
                new ReadyCommand(),
                new ListLobbiesCommand(),
                new CreateLobbyCommand(),
                new RemoveLobbyCommand(),
                new ListMapsCommand(),
                new CreateMapCommand(),
                new RemoveMapCommand(),
                new LobbyParentCommand(),
                new MapParentCommand(),
                new MapSpawnParentCommand(),
                new CombatParentCommand()
        );
    }

    public void register() {
        mainPluginCommand.setExecutor(this);
        mainPluginCommand.setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Running " + plugin.getDescription().getName() + " v" + plugin.getDescription().getVersion());
            sender.sendMessage("Use /sc help to see all available commands");
            return true;
        }
        ICommand subCommand = mainCommands.stream()
                .filter(com -> com.getName().equalsIgnoreCase(args[0]))
                .findFirst()
                .orElse(null);
        if (subCommand == null) {
            sendUsage(sender, label);
            return true;
        }
        subCommand.execute(plugin, sender, "", label, Arrays.copyOfRange(args, 1, args.length));

        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String partialArg;
        int lastIndex = 0;

        if (args.length == 0 || (partialArg = args[lastIndex = args.length - 1]).trim().isEmpty() ) {
            if (lastIndex == 0) {
                return mainCommands.stream()
                        .filter(cmd -> cmd.hasPermission(sender))
                        .map(cmd -> cmd.getName().toLowerCase(Locale.ROOT))
                        .collect(Collectors.toList());
            }
            return mainCommands.stream()
                    .filter(cmd -> cmd.hasPermission(sender))
                    .filter(cmd -> cmd.getName().equalsIgnoreCase(args[0]))
                    .findFirst()
                    .map(cmd -> cmd.tabComplete(plugin, sender, Arrays.copyOfRange(args, 1, args.length)))
                    .orElse(Collections.emptyList());
        }
        if (lastIndex == 0) {
            return mainCommands.stream()
                    .filter(cmd -> cmd.hasPermission(sender))
                    .filter(cmd -> cmd.getName().toLowerCase(Locale.ROOT).startsWith(partialArg.toLowerCase(Locale.ROOT)))
                    .map(cmd -> cmd.getName().toLowerCase(Locale.ROOT))
                    .collect(Collectors.toList());
        }
        return mainCommands.stream()
                .filter(cmd -> cmd.hasPermission(sender))
                .filter(cmd -> cmd.getName().equalsIgnoreCase(args[0]))
                .findFirst()
                .map(cmd -> cmd.tabComplete(plugin, sender, Arrays.copyOfRange(args, 1, args.length)))
                .orElse(Collections.emptyList());
    }

    private void sendUsage(CommandSender commandSender, String label) {
        commandSender.sendMessage("Lobby sub commands: (sc ...)");
        for (ICommand subCommand : mainCommands) {
            if (commandSender.hasPermission(subCommand.getPermission())) {
                commandSender.sendMessage(subCommand.getName().toLowerCase(Locale.ROOT));
            }
        }
    }
}
