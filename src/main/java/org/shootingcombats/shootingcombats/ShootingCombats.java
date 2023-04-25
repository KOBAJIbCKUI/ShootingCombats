package org.shootingcombats.shootingcombats;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.shootingcombats.shootingcombats.data.MapsConfig;
import org.shootingcombats.shootingcombats.data.PluginConfig;
import org.shootingcombats.shootingcombats.manager.*;
import org.shootingcombats.shootingcombats.util.Util;

public final class ShootingCombats extends JavaPlugin {

    //Plugin reference
    private static ShootingCombats plugin;
    private static MapsConfig mapsConfig;
    private static PluginConfig pluginConfig;
    private static CombatMapManager mapsManager;
    private static LobbiesManager lobbiesManager;
    private static PluginCommandExecutor commandManager;

    @Override
    public void onEnable() {
        if (!Util.isRunningMinecraftVersion(1, 12)) {
            if (!Util.isRunningMinecraftVersion(1, 12, 2)) {
                Util.warning("Shooting fights supports only version 1.12.2!");
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
        }
        if (getDescription().getVersion().toLowerCase().contains("alpha")) {
            Util.log("YOU ARE RUNNING AN ALPHA VERSION, so a lot of issues and bugs may appear during use!");
        }

        plugin = this;
        mapsManager = new CombaMapManagerImpl();
        pluginConfig = new PluginConfig();
        mapsConfig = new MapsConfig();
        lobbiesManager = new SimpleLobbiesManager();

        registerCommands();
        addCommandPermissions();

        Util.log("Plugin enabled");
    }

    @Override
    public void onDisable() {
        pluginConfig.saveConfig();
        mapsConfig.saveConfig();
        Util.log("Plugin disabled");
    }

    private void registerCommands() {
        PluginCommand mainCommand = getCommand("sc");
        if (mainCommand == null) {
            Util.warning("Unable to register /sc command!");
            return;
        }
        commandManager = new PluginCommandExecutor(plugin, mainCommand);
        commandManager.register();
    }

    private void addCommandPermissions() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.addPermission(new Permission("sc"));
        pluginManager.addPermission(new Permission("sc.ready"));
        pluginManager.addPermission(new Permission("sc.createlobby"));
        pluginManager.addPermission(new Permission("sc.removelobby"));
        pluginManager.addPermission(new Permission("sc.createmap"));
        pluginManager.addPermission(new Permission("sc.removemap"));
        pluginManager.addPermission(new Permission("sc.lobbies"));
        pluginManager.addPermission(new Permission("sc.lobby"));
        pluginManager.addPermission(new Permission("sc.lobby.join"));
        pluginManager.addPermission(new Permission("sc.lobby.leave"));
        pluginManager.addPermission(new Permission("sc.lobby.info"));
        pluginManager.addPermission(new Permission("sc.map"));
        pluginManager.addPermission(new Permission("sc.map.info"));
        pluginManager.addPermission(new Permission("sc.map.spawn"));
        pluginManager.addPermission(new Permission("sc.map.spawn.add"));
        pluginManager.addPermission(new Permission("sc.map.spawn.remove"));
        pluginManager.addPermission(new Permission("sc.combat"));
        pluginManager.addPermission(new Permission("sc.combat.start"));
        pluginManager.addPermission(new Permission("sc.combat.stop"));
        pluginManager.addPermission(new Permission("sc.combat.leave"));
        pluginManager.addPermission(new Permission("sc.combat.spectate"));
    }

    public static ShootingCombats getPlugin() {
        return plugin;
    }

    public static CombatMapManager getMapsManager() {
        return mapsManager;
    }
    public static LobbiesManager getLobbiesManager() {
        return lobbiesManager;
    }

    public static MapsConfig getMapsConfig() {
        return mapsConfig;
    }

    public static PluginConfig getPluginConfig() {
        return pluginConfig;
    }
}
