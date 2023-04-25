package org.shootingcombats.shootingcombats.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shootingcombats.shootingcombats.ShootingCombats;
import org.shootingcombats.shootingcombats.command.abstraction.AbstractSingleCommand;
import org.shootingcombats.shootingcombats.manager.CombatMapManager;
import org.shootingcombats.shootingcombats.map.CombatMap;
import org.shootingcombats.shootingcombats.util.Util;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

public final class RemoveMapCommand extends AbstractSingleCommand {

    public RemoveMapCommand() {
        super("RemoveMap", "removemap <name>", "sc.removemap");
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
        CombatMapManager mapManager = ShootingCombats.getMapsManager();
        if (!mapManager.containsMap(args[0])) {
            Util.sendMessage(executor, "Map with name " + args[0] + " not found!");
            return false;
        }

        if (mapManager.getMapStatus(args[0]) == CombatMapManager.CombatMapStatus.OCCUPIED) {
            Util.sendMessage(executor, "Unable to remove map " + args[0] + " because combat in progress on it!");
            return false;
        }

        mapManager.removeMap(args[0]);
        Util.sendMessage(executor, "Map " + args[0] + " successfully removed!");

        return true;
    }

    @Override
    public List<String> tabComplete(ShootingCombats plugin, CommandSender commandSender, String[] args) {
        String partialArg;
        int lastIndex = 0;

        if (args.length == 0 || (partialArg = args[lastIndex = args.length - 1]).trim().isEmpty()) {

            return ShootingCombats.getMapsManager().getMaps().keySet().stream()
                    .map(CombatMap::getName)
                    .collect(Collectors.toList());
        }
        return ShootingCombats.getMapsManager().getMaps().keySet().stream()
                .map(CombatMap::getName)
                .filter(mapName -> mapName.toLowerCase(Locale.ROOT).startsWith(partialArg.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
    }

    @Override
    public void sendUsage(CommandSender commandSender, String label) {
        commandSender.sendMessage("Usage for command " + getName() + ": " + getUsage());
    }
}
