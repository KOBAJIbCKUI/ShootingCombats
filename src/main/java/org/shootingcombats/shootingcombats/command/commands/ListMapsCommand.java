package org.shootingcombats.shootingcombats.command.commands;

import org.bukkit.command.CommandSender;
import org.shootingcombats.shootingcombats.ShootingCombats;
import org.shootingcombats.shootingcombats.command.abstraction.AbstractSingleCommand;
import org.shootingcombats.shootingcombats.manager.CombatMapManager;
import org.shootingcombats.shootingcombats.map.CombatMap;
import org.shootingcombats.shootingcombats.util.Util;

import java.util.Collections;
import java.util.List;

public final class ListMapsCommand extends AbstractSingleCommand {
    public ListMapsCommand() {
        super("Maps", "maps", "sc.maps");
    }


    @Override
    public boolean execute(ShootingCombats plugin, CommandSender commandSender, String target, String label, String[] args) {

        if (args.length != 0) {
            sendUsage(commandSender, label);
            return false;
        }
        CombatMapManager mapManager = ShootingCombats.getMapsManager();
        if (mapManager.getMapsNumber() == 0) {
            Util.sendMessage(commandSender, "No maps found!");
            return true;
        }
        Util.sendMessage(commandSender, "Maps list:");
        for (CombatMap combatMap : mapManager.getMaps().keySet()) {
            Util.sendMessage(commandSender, combatMap.getName() + " - " + mapManager.getMapStatus(combatMap));
        }

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
