package org.shootingcombats.shootingcombats.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shootingcombats.shootingcombats.ShootingCombats;
import org.shootingcombats.shootingcombats.command.abstraction.AbstractSingleCommand;
import org.shootingcombats.shootingcombats.manager.CombatMapManager;
import org.shootingcombats.shootingcombats.map.Bound;
import org.shootingcombats.shootingcombats.map.SimpleBound;
import org.shootingcombats.shootingcombats.map.SimpleCombatMap;
import org.shootingcombats.shootingcombats.util.Util;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class CreateMapCommand extends AbstractSingleCommand {

    public CreateMapCommand() {
        super("CreateMap", "createmap <name> <x1> <y1> <z1> <x2> <y2> <z2>", "sc.createmap");
    }
    @Override
    public boolean execute(ShootingCombats plugin, CommandSender commandSender, String target, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("This command can be executed only by players!");
            return false;
        }

        UUID executor = ((Player) commandSender).getUniqueId();
        if (args.length != 7) {
            sendUsage(commandSender, label);
            return false;
        }

        if (args[0].trim().isEmpty()) {
            Util.sendMessage(executor, "Map name cannot be empty!");
            return false;
        }

        CombatMapManager mapManager = ShootingCombats.getMapsManager();
        if (mapManager.containsMap(args[0])) {
            Util.sendMessage(executor, "Map with name " + args[0] + " already exists!");
            return false;
        }

        Location greaterCorner;
        Location lowerCorner;

        try {
            greaterCorner = new Location(Bukkit.getPlayer(executor).getWorld(),
                    Integer.parseInt(args[1]),
                    Integer.parseInt(args[2]),
                    Integer.parseInt(args[3]));
            lowerCorner = new Location(Bukkit.getPlayer(executor).getWorld(),
                    Integer.parseInt(args[4]),
                    Integer.parseInt(args[5]),
                    Integer.parseInt(args[6]));
        } catch (NumberFormatException e) {
            sendUsage(commandSender, label);
            return false;
        }
        Bound bound = new SimpleBound(greaterCorner, lowerCorner);
        if (mapManager.getMaps().keySet().stream().anyMatch(map -> map.getBound().checkCollision(bound))) {
            Util.sendMessage(executor, "This map bounds is overlapping with another map bounds!");
            return false;
        }
        mapManager.addMap(new SimpleCombatMap(args[0], bound));
        ShootingCombats.getMapsConfig().saveToFile();
        Util.sendMessage(executor, "Map " + args[0] + " successfully created!");
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
