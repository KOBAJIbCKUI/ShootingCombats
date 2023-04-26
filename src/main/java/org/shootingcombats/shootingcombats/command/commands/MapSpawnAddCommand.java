package org.shootingcombats.shootingcombats.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shootingcombats.shootingcombats.ShootingCombats;
import org.shootingcombats.shootingcombats.command.abstraction.AbstractSingleCommand;
import org.shootingcombats.shootingcombats.manager.CombatMapManager;
import org.shootingcombats.shootingcombats.map.CombatMap;
import org.shootingcombats.shootingcombats.util.Util;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class MapSpawnAddCommand extends AbstractSingleCommand {
    public MapSpawnAddCommand() {
        super("Add", "spawn add [<world> <x> <y> <z>]", "sc.map.spawn.add");
    }


    @Override
    public boolean execute(ShootingCombats plugin, CommandSender commandSender, String target, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("This command can be executed only by players!");
            return false;
        }

        if (args.length != 0 && args.length != 4) {
            sendUsage(commandSender, label);
            return false;
        }

        CombatMapManager mapManager = ShootingCombats.getMapsManager();
        if (!mapManager.containsMap(target)) {
            Util.sendMessage(commandSender, "Map with name " + target + " not found!");
            return false;
        }

        CombatMap combatMap = mapManager.getMap(target).get();
        int x, y, z;
        String worldName;

        if (args.length == 0) {
            Location playerLocation = ((Player) commandSender).getLocation();
            x = playerLocation.getBlockX();
            y = playerLocation.getBlockY() + 1;
            z = playerLocation.getBlockZ();
            worldName = playerLocation.getWorld().getName();
        } else {
            if (Bukkit.getWorlds().stream().anyMatch(world -> world.getName().equals(args[0]))) {
                Util.sendMessage(commandSender, "World with name " + args[0] + " not found!");
                return false;
            }

            try {
                x = Integer.parseInt(args[1]);
                y = Integer.parseInt(args[2]);
                z = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                sendUsage(commandSender, label);
                return false;
            }
            worldName = args[0];
        }

        Location spawn = new Location(Bukkit.getWorld(worldName), x, y, z);

        if (!combatMap.isInRegion(spawn)) {
            Util.sendMessage(commandSender, "Spawn is out of bounds for map " + combatMap.getName() + "!");
            return false;
        }

        combatMap.addSpawn(spawn);
        Util.sendMessage(commandSender, "Spawn {" + spawn.getWorld().getName() + "; " + x + ", "+ y + ", " + z + "} for map " + combatMap.getName()  + " successfully added!");

        return true;
    }

    @Override
    public List<String> tabComplete(ShootingCombats plugin, CommandSender commandSender, String[] args) {
        String partialArg;
        int lastIndex = 0;

        if (args.length == 0 || (partialArg = args[lastIndex = args.length - 1]).trim().isEmpty()) {

            return Bukkit.getWorlds().stream()
                    .map(World::getName)
                    .collect(Collectors.toList());
        }
        if (args.length == 1) {
            return Bukkit.getWorlds().stream()
                    .map(World::getName)
                    .filter(worldName -> worldName.toLowerCase(Locale.ROOT).startsWith(partialArg.toLowerCase(Locale.ROOT)))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public void sendUsage(CommandSender commandSender, String label) {
        commandSender.sendMessage("Usage for command " + getName() + ": " + getUsage());
    }
}
