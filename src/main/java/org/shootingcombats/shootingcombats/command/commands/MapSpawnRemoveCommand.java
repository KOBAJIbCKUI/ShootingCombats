package org.shootingcombats.shootingcombats.command.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shootingcombats.shootingcombats.ShootingCombats;
import org.shootingcombats.shootingcombats.command.abstraction.AbstractSingleCommand;
import org.shootingcombats.shootingcombats.manager.CombatMapManager;
import org.shootingcombats.shootingcombats.map.CombatMap;
import org.shootingcombats.shootingcombats.util.Util;

import java.util.Collections;
import java.util.List;

public final class MapSpawnRemoveCommand extends AbstractSingleCommand {
    public MapSpawnRemoveCommand() {
        super("Remove", "remove <index>", "sc.map.spawn.remove");
    }

    @Override
    public boolean execute(ShootingCombats plugin, CommandSender commandSender, String target, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("This command can be executed only by players!");
            return false;
        }

        if (args.length != 1) {
            sendUsage(commandSender, label);
            return false;
        }

        CombatMapManager mapManager = ShootingCombats.getMapsManager();
        if (!mapManager.containsMap(target)) {
            Util.sendMessage(commandSender, "Map with name " + target + " not found!");
            return false;
        }

        CombatMap combatMap = mapManager.getMap(target).get();
        int index;
        try {
            index = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sendUsage(commandSender, label);
            return false;
        }

        if ((index - 1) < 0 || (index - 1) >= combatMap.spawnsNumber()) {
            Util.sendMessage(commandSender, "Map " + combatMap.getName() + " doesn't contain spawn with index " + index + "!");
            return false;
        }

        Location spawn = combatMap.getSpawns().get(index - 1);
        combatMap.removeSpawn(index - 1);
        Util.sendMessage(commandSender, "Spawn {" + spawn.getWorld().getName() + "; " + spawn.getBlockX() + ", "+ spawn.getBlockY() + ", " + spawn.getBlockZ() + "} with index " + index + " successfully removed from map " + combatMap.getName() + "!");

        return true;
    }

    @Override
    public List<String> tabComplete(ShootingCombats plugin, CommandSender commandSender, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public void sendUsage(CommandSender commandSender, String label) {
        commandSender.sendMessage("Usage for command " + getName() + ": " + getUsage());
    }
}
