package org.shootingcombats.shootingcombats.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.shootingcombats.shootingcombats.*;
import org.shootingcombats.shootingcombats.manager.CombatMapManager;
import org.shootingcombats.shootingcombats.map.Bound;
import org.shootingcombats.shootingcombats.map.CombatMap;
import org.shootingcombats.shootingcombats.map.SimpleBound;
import org.shootingcombats.shootingcombats.map.SimpleCombatMap;
import org.shootingcombats.shootingcombats.util.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class MapsConfig {
    private final CombatMapManager mapsManager;
    private FileConfiguration mapsConfig;
    private Path configPath;

    public MapsConfig() {
        this.mapsManager = ShootingCombats.getMapsManager();
        loadConfig();
    }

    public void loadConfig() {
        if (this.configPath == null) {
            this.configPath = Paths.get(ShootingCombats.getPlugin().getDataFolder().getPath(), "maps.yml");
        }
        if (!Files.exists(configPath)) {
            try {
                Files.createFile(configPath);
                this.mapsConfig = YamlConfiguration.loadConfiguration(configPath.toFile());
                saveConfig();
                Util.log("New maps.yml has been successfully created!");
            } catch (IOException e) {
                Util.warning("Unable to create maps.yml!");
                Util.debug(e);
            }
        } else {
            this.mapsConfig = YamlConfiguration.loadConfiguration(configPath.toFile());
            loadFromFile();
            Util.log("maps.yml has been successfully loaded!");
        }
    }

    public void saveConfig() {
        try {
            mapsConfig.save(configPath.toFile());
        } catch (IOException e) {
            Util.warning("Unable to save maps to " + configPath);
            Util.debug(e);
        }
    }

    public void saveToFile() {
        Util.log("Saving maps...");
        ConfigurationSection mapsSection = mapsConfig.createSection("maps");
        ConfigurationSection mapSection, boundsSection, boundCornerSection, spawnPointsSection, spawnPointSection;
        Bound bound;
        Location corner;

        for (CombatMap combatMap : ShootingCombats.getMapsManager().getMaps().keySet()) {
            mapSection = mapsSection.createSection(combatMap.getName());

            boundsSection = mapSection.createSection("bound");
            bound = combatMap.getBound();
            boundsSection.set("world", bound.getWorld());

            boundCornerSection = boundsSection.createSection("greater_corner");
            corner = combatMap.getBound().getGreaterCorner();
            boundCornerSection.set("x", corner.getBlockX());
            boundCornerSection.set("y", corner.getBlockY());
            boundCornerSection.set("z", corner.getBlockZ());

            boundCornerSection = boundsSection.createSection("lower_corner");
            corner = combatMap.getBound().getLowerCorner();
            boundCornerSection.set("x", corner.getBlockX());
            boundCornerSection.set("y", corner.getBlockY());
            boundCornerSection.set("z", corner.getBlockZ());

            if (combatMap.spawnsNumber() == 0) {
                continue;
            }

            spawnPointsSection = mapSection.createSection("spawn_points");

            int index = 1;
            for (Location location : combatMap.getSpawns()) {
                spawnPointSection = spawnPointsSection.createSection(String.valueOf(index));
                spawnPointSection.set("x", location.getBlockX());
                spawnPointSection.set("y", location.getBlockY());
                spawnPointSection.set("z", location.getBlockZ());
                index++;
            }
        }
        saveConfig();
        Util.log("Maps successfully saved!");
    }

    public void loadFromFile() {
        Util.log("Loading maps...");

        if (!Files.exists(configPath)) {
            Util.log("No maps to load");
            return;
        }

        ConfigurationSection mapsSection = mapsConfig.getConfigurationSection("maps");

        if (mapsSection != null) {
            ConfigurationSection mapSection, boundsSection, boundCornerSection, spawnPointsSection, spawnPointSection;
            String world;
            Location greaterCorner, lowerCorner;
            CombatMap combatMap;

            for (String mapName : mapsSection.getKeys(false)) {
                try {
                    mapSection = mapsSection.getConfigurationSection(mapName);

                    boundsSection = mapSection.getConfigurationSection("bound");
                    world = boundsSection.getString("world");

                    boundCornerSection = boundsSection.getConfigurationSection("greater_corner");
                    greaterCorner = new Location(Bukkit.getWorld(world), boundCornerSection.getInt("x"), boundCornerSection.getInt("y"), boundCornerSection.getInt("z"));

                    boundCornerSection = boundsSection.getConfigurationSection("lower_corner");
                    lowerCorner = new Location(Bukkit.getWorld(world), boundCornerSection.getInt("x"), boundCornerSection.getInt("y"), boundCornerSection.getInt("z"));

                    combatMap = new SimpleCombatMap(mapName, new SimpleBound(greaterCorner, lowerCorner));

                    spawnPointsSection = mapSection.getConfigurationSection("spawn_points");

                    for (String index : spawnPointsSection.getKeys(false)) {
                        spawnPointSection = spawnPointsSection.getConfigurationSection(index);
                        combatMap.addSpawn(new Location(Bukkit.getWorld(world), spawnPointSection.getInt("x"), spawnPointSection.getInt("y"), spawnPointSection.getInt("z")));
                    }
                    mapsManager.addMap(combatMap);
                    Util.log("Map " + mapName + " has been successfully loaded");
                } catch (NullPointerException e) {
                    Util.log("Unable to load map " + mapName);
                    Util.debug(e);
                }
            }
        }
    }
}
