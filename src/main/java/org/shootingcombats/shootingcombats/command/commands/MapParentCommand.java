package org.shootingcombats.shootingcombats.command.commands;

import org.bukkit.command.CommandSender;
import org.shootingcombats.shootingcombats.ShootingCombats;
import org.shootingcombats.shootingcombats.command.abstraction.AbstractParentCommand;
import org.shootingcombats.shootingcombats.command.abstraction.ICommand;
import org.shootingcombats.shootingcombats.map.CombatMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class MapParentCommand extends AbstractParentCommand {
    public MapParentCommand() {
        super("Map", CommandType.WITH_TARGET, "map <name>", "sc.map", Collections.unmodifiableList(Arrays.asList(
                new MapInfoCommand(),
                new MapSpawnParentCommand()
        )));
    }

    @Override
    protected List<String> getTargets(ShootingCombats plugin) {
        return ShootingCombats.getMapsManager().getMaps().keySet().stream()
                .map(CombatMap::getName)
                .collect(Collectors.toList());
    }

    @Override
    protected Object getTarget(ShootingCombats plugin, String target, CommandSender commandSender) {
        return ShootingCombats.getMapsManager().getMap(target);
    }

    @Override
    public void sendUsage(CommandSender commandSender, String label) {
        commandSender.sendMessage("Map sub commands: (" + getUsage() + " ...)");
        for (ICommand subCommand : getChildren()) {
            if (commandSender.hasPermission(subCommand.getPermission())) {
                commandSender.sendMessage(subCommand.getName().toLowerCase(Locale.ROOT));
            }
        }
    }
}
