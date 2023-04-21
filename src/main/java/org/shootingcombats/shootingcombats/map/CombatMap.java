package org.shootingcombats.shootingcombats.map;

import org.bukkit.Location;

import java.util.List;

public interface CombatMap {
    String getName();
    void setName(String name);
    boolean addSpawn(Location location);
    boolean removeSpawn(Location location);
    boolean removeSpawn(int index);
    int spawnsNumber();
    List<Location> getSpawns();
    Bound getBound();
    void setBound(Bound bound);
    boolean isInRegion(Location location);
}
