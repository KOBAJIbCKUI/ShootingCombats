package org.shootingcombats.shootingcombats.manager;

import org.bukkit.command.*;
import org.shootingcombats.shootingcombats.ShootingCombats;
import org.shootingcombats.shootingcombats.command.abstraction.ICommand;
import org.shootingcombats.shootingcombats.command.commands.CreateLobbyCommand;
import org.shootingcombats.shootingcombats.command.commands.LobbiesCommand;
import org.shootingcombats.shootingcombats.command.commands.LobbyParentCommand;
import org.shootingcombats.shootingcombats.command.commands.RemoveLobbyCommand;

import java.util.Arrays;
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
                new LobbiesCommand(),
                new CreateLobbyCommand(),
                new RemoveLobbyCommand(),
                new LobbyParentCommand()
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

        if (args.length == 0 || (partialArg = args[lastIndex = args.length - 1]).trim().isEmpty()) {

            return mainCommands.stream()
                    .map(ICommand::getName)
                    .collect(Collectors.toList());
        }
        return mainCommands.stream()
                .map(ICommand::getName)
                .filter(commandName -> commandName.toLowerCase(Locale.ROOT).startsWith(partialArg.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
    }

    private void sendUsage(CommandSender commandSender, String label) {
        commandSender.sendMessage("Lobby sub commands: (sc ...)");
        for (ICommand subCommand : mainCommands) {
            commandSender.sendMessage(subCommand.getName());
        }
    }
}