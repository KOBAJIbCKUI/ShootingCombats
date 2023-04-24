package org.shootingcombats.shootingcombats.command.abstraction;

import org.bukkit.command.CommandSender;
import org.shootingcombats.shootingcombats.ShootingCombats;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractParentCommand extends AbstractCommand {
    private final List<ICommand> children;
    private final CommandType commandType;

    public AbstractParentCommand(String commandName, CommandType commandType, String usageString, String permission, List<ICommand> children) {
        super(commandName, usageString, permission);
        this.commandType = commandType;
        this.children = Collections.unmodifiableList(new ArrayList<>(children));
    }

    @Override
    public boolean execute(ShootingCombats plugin, CommandSender commandSender, String target, String label, String[] args) {
        if (args.length < commandType.minArgs) {
            sendUsage(commandSender, label);
            return false;
        }

        ICommand subCommand = children.stream()
                .filter(sub -> sub.getName().equalsIgnoreCase(args[this.commandType.commandIndex]))
                .findFirst()
                .orElse(null);

        if (subCommand == null) {
            commandSender.sendMessage("ICommand is not recognized!");
            return false;
        }

        if (!subCommand.hasPermission(commandSender)) {
            commandSender.sendMessage("You have no permission to use this command!");
        }

        return subCommand.execute(plugin, commandSender, target, label, Arrays.copyOfRange(args, commandType.minArgs, args.length));
    }

    @Override
    public List<String> tabComplete(ShootingCombats plugin, CommandSender commandSender, String[] args) {
        String partialArg;
        int lastIndex = 0;
        switch (this.commandType) {
            case WITH_TARGET: {
                if (args.length == 0 || (partialArg = args[lastIndex = args.length - 1]).trim().isEmpty() ) {
                    switch (lastIndex) {
                        case 0: {
                            return getTargets(plugin);
                        }
                        case 1: {
                            return getChildren().stream()
                                    .filter(command -> command.hasPermission(commandSender))
                                    .map(command -> command.getName().toLowerCase(Locale.ROOT))
                                    .collect(Collectors.toList());
                        }
                        case 2: {
                            return getChildren().stream()
                                    .filter(command -> command.hasPermission(commandSender))
                                    .filter(command -> command.getName().equalsIgnoreCase(args[1]))
                                    .findFirst()
                                    .map(command -> command.tabComplete(plugin, commandSender, Arrays.copyOfRange(args, 2, args.length)))
                                    .orElse(Collections.emptyList());
                        }
                        default: throw new AssertionError("Reached not reachable code");
                    }
                }
                switch (lastIndex) {
                    case 0: {
                        return getTargets(plugin).stream()
                                .filter(target -> target.startsWith(partialArg))
                                .collect(Collectors.toList());
                    }
                    case 1: {
                        return getChildren().stream()
                                .filter(command -> command.hasPermission(commandSender))
                                .filter(command -> command.getName().toLowerCase(Locale.ROOT).startsWith(partialArg.toLowerCase(Locale.ROOT)))
                                .map(command -> command.getName().toLowerCase(Locale.ROOT))
                                .collect(Collectors.toList());
                    }
                    case 2: {
                        return getChildren().stream()
                                .filter(command -> command.hasPermission(commandSender))
                                .filter(command -> command.getName().equalsIgnoreCase(args[1]))
                                .findFirst()
                                .map(command -> command.tabComplete(plugin, commandSender, Arrays.copyOfRange(args, 2, args.length)))
                                .orElse(Collections.emptyList());
                    }
                    default: throw new AssertionError("Reached not reachable code");
                }
            }
            case WITHOUT_TARGET: {
                if (args.length == 0 || (partialArg = args[lastIndex = args.length - 1]).trim().isEmpty() ) {
                    switch (lastIndex) {
                        case 0: {
                            return getChildren().stream()
                                    .filter(command -> command.hasPermission(commandSender))
                                    .map(command -> command.getName().toLowerCase(Locale.ROOT))
                                    .collect(Collectors.toList());
                        }
                        case 1: {
                            return getChildren().stream()
                                    .filter(command -> command.hasPermission(commandSender))
                                    .filter(command -> command.getName().equalsIgnoreCase(args[0]))
                                    .findFirst()
                                    .map(command -> command.tabComplete(plugin, commandSender, Arrays.copyOfRange(args, 2, args.length)))
                                    .orElse(Collections.emptyList());
                        }
                        default: throw new AssertionError("Reached not reachable code");
                    }
                }
                switch (lastIndex) {
                    case 0: {
                        return getChildren().stream()
                                .filter(command -> command.hasPermission(commandSender))
                                .filter(command -> command.getName().toLowerCase(Locale.ROOT).startsWith(partialArg.toLowerCase(Locale.ROOT)))
                                .map(command -> command.getName().toLowerCase(Locale.ROOT))
                                .collect(Collectors.toList());
                    }
                    case 1: {
                        return getChildren().stream()
                                .filter(command -> command.hasPermission(commandSender))
                                .filter(command -> command.getName().equalsIgnoreCase(args[0]))
                                .findFirst()
                                .map(command -> command.tabComplete(plugin, commandSender, Arrays.copyOfRange(args, 2, args.length)))
                                .orElse(Collections.emptyList());
                    }
                    default: throw new AssertionError("Reached not reachable code");
                }
            }
            default: throw new AssertionError(this.commandType);
        }
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return children.stream().anyMatch(sub -> sub.hasPermission(commandSender));
    }

    public List<ICommand> getChildren() {
        return children;
    }

    protected abstract List<String> getTargets(ShootingCombats plugin);
    protected abstract Object getTarget(ShootingCombats plugin, String target, CommandSender commandSender);

    public enum CommandType {
        WITHOUT_TARGET(0),
        WITH_TARGET(1);

        private final int commandIndex;
        private final int minArgs;

        CommandType(int cmdIndex) {
            this.commandIndex = cmdIndex;
            this.minArgs = cmdIndex + 1;
        }
    }
}



