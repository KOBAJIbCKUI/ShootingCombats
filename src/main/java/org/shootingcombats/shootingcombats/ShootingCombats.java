package org.shootingcombats.shootingcombats;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.shootingcombats.shootingcombats.data.MapsConfig;
import org.shootingcombats.shootingcombats.data.PluginConfig;
import org.shootingcombats.shootingcombats.manager.CombaMapManagerImpl;
import org.shootingcombats.shootingcombats.manager.CombatMapManager;
import org.shootingcombats.shootingcombats.manager.LobbiesManager;
import org.shootingcombats.shootingcombats.manager.SimpleLobbiesManager;
import org.shootingcombats.shootingcombats.util.Util;

public final class ShootingCombats extends JavaPlugin {

    //Plugin reference
    private static ShootingCombats plugin;
    private static MapsConfig mapsConfig;
    private static PluginConfig pluginConfig;
    private static CombatMapManager mapsManager;
    private static LobbiesManager lobbiesManager;

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

        Util.log("Plugin enabled");
    }

    @Override
    public void onDisable() {
        Util.log("Plugin disabled");
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
