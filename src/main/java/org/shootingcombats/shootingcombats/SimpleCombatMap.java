package org.shootingcombats.shootingcombats;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public final class SimpleCombatMap implements CombatMap {

    private final List<Location> spawns;
    private Bound bound;
    private String name;

    public SimpleCombatMap(String name, Bound bound) {
        this.name = name;
        this.bound = bound;

        this.spawns = new ArrayList<>();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean addSpawn(Location location) {
        if (spawns.contains(location)) {
            return false;
        }

        spawns.add(new Location(location.getWorld(), location.getX(), location.getY(), location.getZ()));

        return true;
    }

    @Override
    public boolean removeSpawn(Location location) {
        return spawns.remove(location);
    }

    @Override
    public boolean removeSpawn(int index) {
        if (index >= spawns.size() || index < 0) {
            return false;
        }

        spawns.remove(index);

        return true;
    }

    @Override
    public int spawnsNumber() {
        return spawns.size();
    }

    @Override
    public List<Location> getSpawns() {
        return new ArrayList<>(spawns);
    }

    @Override
    public Bound getBound() {
        return this.bound;
    }

    @Override
    public void setBound(Bound bound) {
        this.bound = bound;
    }

    @Override
    public boolean isInRegion(Location location) {
        return bound.isInBounds(location);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("SimpleCombatMap{");
        sb.append("name='").append(name).append("'");
        sb.append(", bound=").append(bound);
        sb.append("spawns=[");
        for (Location spawn : spawns) {
            sb.append("\n[").append(spawn.getWorld().getName()).append(spawn.getBlockX()).append(spawn.getBlockY()).append(spawn.getBlockZ()).append("]");
        }
        sb.append("\n]}");
        return sb.toString();
    }
}
