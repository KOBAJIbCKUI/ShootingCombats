package org.shootingcombats.shootingcombats.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.shootingcombats.shootingcombats.ShootingCombats;
import org.shootingcombats.shootingcombats.util.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class PluginConfig {
    private FileConfiguration pluginConfig;
    private Path configPath;

    //Global settings
    public static int gbMaxLobbiesNumber;
    public static int gbMaxMapsNumber;
    public static int gbMaxRewardSize;
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

    public PluginConfig() {
        loadConfig();
    }

    public void loadConfig() {
        if (this.configPath == null) {
            this.configPath = Paths.get(ShootingCombats.getPlugin().getDataFolder().getPath(), "config.yml");
        }
        if (!Files.exists(configPath)) {
            ShootingCombats.getPlugin().saveResource("config.yml", false);
            Util.log("New config.yml has been successfully created!");
        }
        pluginConfig = YamlConfiguration.loadConfiguration(configPath.toFile());
        loadConfigFromFile();
        Util.log("config.yml has been successfully loaded!");
    }

    public void saveConfig() {
        try {
            pluginConfig.save(configPath.toFile());
        } catch (IOException e) {
            Util.warning("Unable to save configurations to " + configPath);
            Util.debug(e);
        }
    }

    public void loadConfigFromFile() {

        String currentSection = "global."; //Global

        gbDebug = pluginConfig.getBoolean(currentSection + "debug");

        gbMaxLobbiesNumber = pluginConfig.getInt(currentSection + "max_lobbies_number");
        gbMaxLobbiesNumber = gbMaxLobbiesNumber < 0 ? 8 : gbMaxLobbiesNumber;

        gbMaxMapsNumber = pluginConfig.getInt(currentSection + "max_maps_number");
        gbMaxMapsNumber = gbMaxMapsNumber < 0 ? 16 : gbMaxMapsNumber;

        gbMaxRewardSize = pluginConfig.getInt(currentSection + "max_reward_size");
        gbMaxRewardSize = gbMaxRewardSize < 0 || gbMaxRewardSize > 64 ? 64 : gbMaxRewardSize;

        gbMaxCombatDurMinutes = pluginConfig.getInt(currentSection + "max_combat_duration_minutes");
        gbMaxCombatDurMinutes = gbMaxCombatDurMinutes < 0 || gbMaxCombatDurMinutes > 30 ? 15 : gbMaxCombatDurMinutes;

        currentSection = currentSection.replace("global.", "deathmatch.default_properties."); //Deathmatch default_properties

        dmDeathSpectate = pluginConfig.getBoolean(currentSection + "spectate_after_death");
        dmEndgameTags = pluginConfig.getBoolean(currentSection + "endgame_tags");
        dmFinalTeleport = pluginConfig.getBoolean(currentSection + "final_teleport");

        dmCombatDurMinutes = pluginConfig.getInt(currentSection + "combat_duration_minutes");
        dmCombatDurMinutes = dmCombatDurMinutes < 1 || dmCombatDurMinutes > gbMaxCombatDurMinutes ? 15 : dmCombatDurMinutes;

        dmMinutesToTags = pluginConfig.getInt(currentSection + "minutes_to_tags");
        dmMinutesToTags = Math.min(dmMinutesToTags, dmCombatDurMinutes);

        dmMinutesToFinalTp = pluginConfig.getInt(currentSection + "minutes_to_final_tp");
        dmMinutesToFinalTp = Math.min(dmMinutesToFinalTp, dmCombatDurMinutes);

        currentSection = currentSection.replace("default_properties.", "rewards."); //Deathmatch rewards

        dmWinReward = pluginConfig.getInt(currentSection + "win");
        dmWinReward = dmWinReward < 1 || dmWinReward > gbMaxRewardSize ? Math.min(dmWinReward, gbMaxRewardSize) : dmWinReward;

        dmLooseReward = pluginConfig.getInt(currentSection + "loose");
        dmLooseReward = dmLooseReward < 1 || dmLooseReward > gbMaxRewardSize ? Math.min(dmLooseReward, gbMaxRewardSize) : dmLooseReward;

        dmKillReward = pluginConfig.getInt(currentSection + "kill");
        dmKillReward = dmKillReward < 1 || dmKillReward > gbMaxRewardSize ? Math.min(dmKillReward, gbMaxRewardSize) : dmKillReward;

        dmBonus = pluginConfig.getInt("bonus");
        dmBonus = dmBonus < 1 || dmBonus > gbMaxRewardSize ? Math.min(dmBonus, gbMaxRewardSize) : dmBonus;
        //dmKillstreakLength = config.getInt("killstreak_length", 3);

        //currentSection = currentSection.replace("deathmatch.rewards.", ""); //Deathmatch rewards
    }

    public void saveConfigToFile() {

        String currentSection = "global."; //Global

        pluginConfig.set(currentSection + "debug", gbDebug);
        pluginConfig.set(currentSection + "max_lobbies_number", gbMaxLobbiesNumber);
        pluginConfig.set(currentSection + "max_maps_number", gbMaxMapsNumber);
        pluginConfig.set(currentSection + "max_reward_size", gbMaxRewardSize);
        pluginConfig.set(currentSection + "max_combat_duration_minutes", gbMaxCombatDurMinutes);

        currentSection = currentSection.replace("global.", "deathmatch.default_properties."); //Deathmatch default properties

        pluginConfig.set(currentSection + "combat_duration_minutes", dmCombatDurMinutes);
        pluginConfig.set(currentSection + "endgame_tags", dmEndgameTags);
        pluginConfig.set(currentSection + "minutes_to_tags", dmMinutesToTags);
        pluginConfig.set(currentSection + "final_teleport", dmFinalTeleport);
        pluginConfig.set(currentSection + "minutes_to_final_tp", dmMinutesToFinalTp);
        pluginConfig.set(currentSection + "spectate_after_death", dmDeathSpectate);

        //Rewards from config
        currentSection = currentSection.replace("default_properties.", "rewards."); //Deathmatch rewards

        pluginConfig.set(currentSection + "win",dmWinReward);
        pluginConfig.set(currentSection + "loose", dmLooseReward);
        pluginConfig.set(currentSection + "kill", dmKillReward);
        pluginConfig.set(currentSection + "bonus", dmBonus);
        //config.set("killstreak_length", killstreakLength);

        saveConfig();
    }

}
