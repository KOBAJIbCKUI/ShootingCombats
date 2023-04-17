package org.shootingcombats.shootingcombats;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.shootingcombats.shootingcombats.util.Util;

public final class ShootingCombats extends JavaPlugin {

    //Plugin reference
    private static ShootingCombats plugin;

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

        Util.log("Plugin enabled");
    }

    @Override
    public void onDisable() {
        Util.log("Plugin disabled");
    }

    public static ShootingCombats getPlugin() {
        return plugin;
    }
}
