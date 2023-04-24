package org.shootingcombats.shootingcombats.command.abstraction;

import org.bukkit.command.CommandSender;

public abstract class AbstractSingleCommand extends AbstractCommand {
    public AbstractSingleCommand(String commandName, String usageString, String permission) {
        super(commandName, usageString, permission);
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.hasPermission(getPermission());
    }
}
