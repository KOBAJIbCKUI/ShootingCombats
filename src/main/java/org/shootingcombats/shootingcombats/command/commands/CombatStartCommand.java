package org.shootingcombats.shootingcombats.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shootingcombats.shootingcombats.ShootingCombats;
import org.shootingcombats.shootingcombats.command.abstraction.AbstractSingleCommand;
import org.shootingcombats.shootingcombats.lobby.Lobby;
import org.shootingcombats.shootingcombats.manager.LobbiesManager;
import org.shootingcombats.shootingcombats.map.CombatMap;
import org.shootingcombats.shootingcombats.util.Util;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

public final class CombatStartCommand extends AbstractSingleCommand {
    public CombatStartCommand() {
        super("Start", "start <name>", "sc.combat.start");
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

        LobbiesManager lobbiesManager = ShootingCombats.getLobbiesManager();
        Lobby foundLobby = lobbiesManager.getLobbies().stream()
                .filter(lobby -> lobby.isPlayerInLobby(executor))
                .findFirst()
                .orElse(null);
        if (foundLobby == null) {
            Util.sendMessage(executor, "This command can be used only in lobby!");
            return false;
        }

        CombatMap combatMap = ShootingCombats.getMapsManager().getMap(args[0]).orElse(null);
        if (combatMap == null) {
            Util.sendMessage(commandSender, "Map with name " + args[0] + " not found!");
        }

        foundLobby.startCombat(executor, combatMap);

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
