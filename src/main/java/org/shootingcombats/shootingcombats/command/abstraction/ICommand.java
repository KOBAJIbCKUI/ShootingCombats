package org.shootingcombats.shootingcombats.command.abstraction;

import org.bukkit.command.CommandSender;
import org.shootingcombats.shootingcombats.ShootingCombats;

import java.util.List;

public interface ICommand {

    String getName();
    String getUsage();
    String getPermission();
    boolean execute(ShootingCombats plugin, CommandSender commandSender, String target, String label, String[] args);
    List<String> tabComplete(ShootingCombats plugin, CommandSender commandSender, String[] args);
    void sendUsage(CommandSender commandSender, String label);
    boolean hasPermission(CommandSender commandSender);
}
