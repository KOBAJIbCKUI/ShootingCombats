package org.shootingcombats.shootingcombats.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Util {
    private static final Logger LOGGER = Bukkit.getLogger();

    private Util() {
        throw new AssertionError("Instantiation of utility class");
    }

    public static void log(String message) {
        sendMessage(Bukkit.getConsoleSender(),"[ShootingCombats] " + message);
        //LOGGER.log(Level.INFO, "[ShootingCombats] INFO: " + message);
    }

    public static void warning(String message) {
        if (message.length() > 0) {
            LOGGER.warning("[ShootingCombats] WARNING: " + message);
        }
    }

    public static void debug(Exception exception) {
        LOGGER.log(Level.SEVERE, "[ShootingCombats] ERROR: ");
        LOGGER.log(Level.SEVERE, exception.toString());
        for (StackTraceElement element : exception.getStackTrace()) {
            LOGGER.log(Level.SEVERE, element.toString());
        }
    }

    public static void sendMessage(CommandSender commandSender, String message) {
        if (message.length() > 0) {
            commandSender.sendMessage(message);
        }
    }

    public static boolean isRunningMinecraftVersion(int major, int minor) {
        return isRunningMinecraftVersion(major, minor, 0);
    }

    public static boolean isRunningMinecraftVersion(int major, int minor, int revision) {
        String[] version = Bukkit.getServer().getBukkitVersion().split("-")[0].split("\\.");
        int serverMajor = Integer.parseInt(version[0]);
        int serverMinor = Integer.parseInt(version[1]);
        int serverRevision;
        try {
            serverRevision = Integer.parseInt(version[2]);
        } catch (Exception ignore) {
            serverRevision = 0;
        }
        return serverMajor > major || serverMinor > minor || (serverMinor == minor && serverRevision >= revision);
    }

    public static void sendMessage(Collection<UUID> uuids, String message) {
        for (UUID uuid : uuids) {
            sendMessage(uuid, message);
        }
    }

    public static void sendMessage(Player player, String message) {
        sendMessage(player.getUniqueId(), message);
    }

    public static void sendMessage(UUID uuid, String message) {
        Bukkit.getPlayer(uuid).sendMessage("" + ChatColor.BLUE + ChatColor.BOLD + "[ShootingCombats] " + ChatColor.RESET + message);
    }

    public static void sendTitle(UUID uuid, String title) {
        sendTitle(uuid, title, null);
    }

    public static void sendTitle(UUID uuid, String title, String subTitle) {
        sendTitle(uuid, title, subTitle, 20, 60, 20);
    }

    public static void sendTitle(UUID uuid, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        Bukkit.getPlayer(uuid).sendTitle(title, subTitle, fadeIn, stay, fadeOut);
    }

    public static void sendTitle(Collection<UUID> uuids, String title) {
        sendTitle(uuids, title, null);
    }

    public static void sendTitle(Collection<UUID> uuids, String title, String subTitle) {
        sendTitle(uuids, title, subTitle, 20, 60, 20);
    }

    public static void sendTitle(Collection<UUID> uuids, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        for (UUID uuid : uuids) {
            sendTitle(uuid, title, subTitle, fadeIn, stay, fadeOut);
        }
    }
}
