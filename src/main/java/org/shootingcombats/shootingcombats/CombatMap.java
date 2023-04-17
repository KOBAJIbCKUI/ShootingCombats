package org.shootingcombats.shootingcombats;

import org.bukkit.Location;

import java.util.List;

public interface CombatMap {
    String getName();
    boolean addSpawn(Location location);
    boolean removeSpawn(Location location);
    boolean removeSpawn(int index);
    int spawnsNumber();
    List<Location> getSpawns();
    Bound getBound();
    boolean isInRegion(Location location);
}
