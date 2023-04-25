package org.shootingcombats.shootingcombats.command.commands;

import org.bukkit.command.CommandSender;
import org.shootingcombats.shootingcombats.ShootingCombats;
import org.shootingcombats.shootingcombats.command.abstraction.AbstractParentCommand;
import org.shootingcombats.shootingcombats.command.abstraction.ICommand;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class CombatParentCommand extends AbstractParentCommand {
    public CombatParentCommand() {
        super("Combat", CommandType.WITHOUT_TARGET, "combat", "sc.combat", Collections.unmodifiableList(Arrays.asList(
                new CombatStartCommand(),
                new CombatStopCommand(),
                new CombatLeaveCommand(),
                new CombatSpectateCommand()
        )));
    }

    @Override
    protected List<String> getTargets(ShootingCombats plugin) {
        return Collections.emptyList();
    }

    @Override
    protected Object getTarget(ShootingCombats plugin, String target, CommandSender commandSender) {
        return "";
    }

    @Override
    public void sendUsage(CommandSender commandSender, String label) {
        commandSender.sendMessage("Combat sub commands: (" + getUsage() + " ...)");
        for (ICommand subCommand : getChildren()) {
            if (commandSender.hasPermission(subCommand.getPermission())) {
                commandSender.sendMessage(subCommand.getName().toLowerCase(Locale.ROOT));
            }
        }
    }
}
