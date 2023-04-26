package org.shootingcombats.shootingcombats.command.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.shootingcombats.shootingcombats.ShootingCombats;
import org.shootingcombats.shootingcombats.command.abstraction.AbstractSingleCommand;
import org.shootingcombats.shootingcombats.manager.CombatMapManager;
import org.shootingcombats.shootingcombats.map.CombatMap;
import org.shootingcombats.shootingcombats.util.Util;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class MapInfoCommand extends AbstractSingleCommand {
    public MapInfoCommand() {
        super("Info", "info", "sc.map.info");
    }

    @Override
    public boolean execute(ShootingCombats plugin, CommandSender commandSender, String target, String label, String[] args) {
        if (args.length != 0) {
            sendUsage(commandSender, label);
            return false;
        }
        CombatMapManager mapManager = ShootingCombats.getMapsManager();
        if (!mapManager.containsMap(target)) {
            Util.sendMessage(commandSender, "Map with name " + target + " not found!");
            return false;
        }
        CombatMap combatMap = mapManager.getMap(target).get();
        Location greaterCorner = combatMap.getBound().getGreaterCorner();
        Location lowerCorner = combatMap.getBound().getLowerCorner();

        Util.sendMessage(commandSender, "Map name: " + combatMap.getName());
        Util.sendMessage(commandSender, "World: " + combatMap.getBound().getWorld());
        Util.sendMessage(commandSender, "Bounds: "
                + "[" + greaterCorner.getBlockX() + " " + greaterCorner.getBlockY() + " " + greaterCorner.getBlockZ() + "] "
                + "[" + lowerCorner.getBlockX() + " " + lowerCorner.getBlockY() + " " + lowerCorner.getBlockZ() + "]");
        Util.sendMessage(commandSender, "Status: " + mapManager.getMapStatus(combatMap));
        Util.sendMessage(commandSender, "Spawns:");
        int index = 1;
        for (Location spawn : combatMap.getSpawns()) {
            Util.sendMessage(commandSender, index + ". {" + spawn.getBlockX() + ", " + spawn.getBlockY() + ", " + spawn.getBlockZ() + "}");
            index++;
        }
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
                .filter(lobbyName -> lobbyName.toLowerCase(Locale.ROOT).startsWith(partialArg.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
    }

    @Override
    public void sendUsage(CommandSender commandSender, String label) {
        commandSender.sendMessage("Usage for command " + getName() + ": " + getUsage());
    }
}
