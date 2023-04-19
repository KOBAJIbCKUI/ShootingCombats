package org.shootingcombats.shootingcombats.data;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.shootingcombats.shootingcombats.ShootingCombats;

public class Config {
    private FileConfiguration config;

    //Global settings
    public static int gbMaxLobbiesNumber;
    public static int gbMaxMapsNumber;
    public static int gbMaxRewardSize;
    public static int gbMaxLobbyPlayers;
    public static int gbMaxCombatDurMinutes;
    public static boolean gbDebug;

    //Deathmatch settings
    public static int dmCombatDurMinutes;
    public static boolean dmFinalTeleport;
    public static int dmMinutesToFinalTp;
    public static boolean dmEndgameTags;
    public static int dmMinutesToTags;
    public static boolean dmDeathSpectate;
    public static int dmWinReward;
    public static int dmLooseReward;
    public static int dmKillReward;
    //public static int dmKillstreakLength;
    public static int dmBonus;

    public Config() {
        loadConfig();
    }

    public void loadConfig() {

        config = ShootingCombats.getPlugin().getConfig();

        int intTemp;

        //.addDefault("default", "default");

        ConfigurationSection mainSection, subSection;

        //Global config
        mainSection = config.getConfigurationSection("global");

        intTemp = mainSection.getInt("max_lobbies_number", 8);
        gbMaxLobbiesNumber = intTemp < 0 || intTemp > 32 ? 8 : intTemp;

        intTemp = mainSection.getInt("max_maps_number", 16);
        gbMaxMapsNumber = intTemp < 0 ? 1 : intTemp;

        intTemp = mainSection.getInt("max_reward_size", 64);
        gbMaxRewardSize = intTemp < 0 || intTemp > 64 ? 64 : intTemp;

        intTemp = mainSection.getInt("max_lobby_players", Bukkit.getServer().getMaxPlayers());
        gbMaxLobbyPlayers = intTemp < 1 || intTemp > Bukkit.getServer().getMaxPlayers() ? Bukkit.getServer().getMaxPlayers() : intTemp;

        intTemp = mainSection.getInt("max_combat_duration_minutes", 30);
        gbMaxCombatDurMinutes = intTemp < 1 || intTemp > 30 ? 30 : intTemp;

        gbDebug = mainSection.getBoolean("debug", false);



        //Deathmatch default config
        mainSection = config.getConfigurationSection("deathmatch");

        //Default properties from config
        subSection = mainSection.getConfigurationSection("default_properties");

        intTemp = subSection.getInt("combat_duration_minutes", 15);
        dmCombatDurMinutes = intTemp < 1 || intTemp > gbMaxCombatDurMinutes ? 15 : intTemp;

        intTemp = subSection.getInt("minutes_to_tags", 10);
        dmMinutesToTags = Math.min(intTemp, dmCombatDurMinutes);

        intTemp = subSection.getInt("minutes_to_final_tp", 14);
        dmMinutesToFinalTp = Math.min(intTemp, dmCombatDurMinutes);

        dmDeathSpectate = subSection.getBoolean("spectate_after_death", true);
        dmEndgameTags = subSection.getBoolean("endgame_tags", true);
        dmFinalTeleport = subSection.getBoolean("final_teleport", false);

        //Rewards from config
        subSection = mainSection.getConfigurationSection("rewards");

        dmWinReward = subSection.getInt("win",8);
        dmLooseReward = subSection.getInt("loose", 3);
        dmKillReward = subSection.getInt("kill", 5);
        dmBonus = subSection.getInt("bonus", 5);
        //dmKillstreakLength = config.getInt("killstreak_length", 3);

        saveConfig();
    }

    public void saveConfig() {

        ConfigurationSection mainSection, subSection;

        //Global config
        mainSection = config.createSection("global");

        mainSection.set("max_lobbies_number", gbMaxLobbiesNumber);
        mainSection.set("max_maps_number", gbMaxMapsNumber);
        mainSection.set("max_reward_size", gbMaxRewardSize);
        mainSection.set("max_lobby_players", gbMaxLobbyPlayers);
        mainSection.set("max_combat_duration_minutes", gbMaxCombatDurMinutes);

        //Deathmatch default config
        mainSection = config.createSection("deathmatch");

        //Default properties from config
        subSection = mainSection.createSection("default_properties");

        subSection.set("combat_duration_minutes", dmCombatDurMinutes);
        subSection.set("endgame_tags", dmEndgameTags);
        subSection.set("minutes_to_tags", dmMinutesToTags);
        subSection.set("final_teleport", dmFinalTeleport);
        subSection.set("minutes_to_final_tp", dmMinutesToFinalTp);
        subSection.set("spectate_after_death", dmDeathSpectate);

        //Rewards from config
        subSection = mainSection.createSection("rewards");

        subSection.set("win",dmWinReward);
        subSection.set("loose", dmLooseReward);
        subSection.set("kill", dmKillReward);
        subSection.set("bonus", dmBonus);
        //config.set("killstreak_length", killstreakLength);

        ShootingCombats.getPlugin().saveConfig();
    }
}
