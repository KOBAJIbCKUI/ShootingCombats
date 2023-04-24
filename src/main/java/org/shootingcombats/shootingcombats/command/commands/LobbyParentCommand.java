package org.shootingcombats.shootingcombats.command.commands;

import org.bukkit.command.CommandSender;
import org.shootingcombats.shootingcombats.ShootingCombats;
import org.shootingcombats.shootingcombats.command.abstraction.AbstractParentCommand;
import org.shootingcombats.shootingcombats.command.abstraction.ICommand;
import org.shootingcombats.shootingcombats.lobby.Lobby;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class LobbyParentCommand extends AbstractParentCommand {
    public LobbyParentCommand() {
        super("Lobby", CommandType.WITHOUT_TARGET, "lobby", "sc.lobby", Collections.unmodifiableList(Arrays.asList(
                new LobbyJoinCommand(),
                new LobbyLeaveCommand()
        )));
    }

    @Override
    protected List<String> getTargets(ShootingCombats plugin) {
        return ShootingCombats.getLobbiesManager().getLobbies().stream()
                .map(Lobby::getName)
                .collect(Collectors.toList());
    }

    @Override
    protected Object getTarget(ShootingCombats plugin, String target, CommandSender commandSender) {
        return ShootingCombats.getLobbiesManager().getLobby(target);
    }

    @Override
    public void sendUsage(CommandSender commandSender, String label) {
        commandSender.sendMessage("Lobby sub commands: (" + getUsage() + " ...)");
        for (ICommand subCommand : getChildren()) {
            if (commandSender.hasPermission(subCommand.getPermission())) {
                commandSender.sendMessage(subCommand.getName().toLowerCase(Locale.ROOT));
            }
        }
    }
}
