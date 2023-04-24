package org.shootingcombats.shootingcombats.command.abstraction;

public abstract class AbstractCommand implements ICommand {
    private final String commandName;
    private final String usageString;
    private final String permission;

    public AbstractCommand(String commandName, String usageString, String permission) {
        this.commandName = commandName;
        this.usageString = usageString;
        this.permission = permission;
    }

    @Override
    public String getName() {
        return this.commandName;
    }

    @Override
    public String getUsage() {
        return this.usageString;
    }

    @Override
    public String getPermission() {
        return this.permission;
    }

}
