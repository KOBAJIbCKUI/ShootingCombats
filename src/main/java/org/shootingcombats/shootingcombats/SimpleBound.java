package org.shootingcombats.shootingcombats;

import org.bukkit.Location;

import java.util.Objects;

public final class SimpleBound implements Bound {
    private final int x1, y1, z1; //Lesser corner
    private final int x2, y2, z2; //Greater corner
    private final String world; //World name
    //private Set<Entity> entities;

    public SimpleBound(String world, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.world = world;
        this.x1 = Math.min(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.z1 = Math.min(z1, z2);
        this.x2 = Math.max(x1, x2);
        this.y2 = Math.max(y1, y2);
        this.z2 = Math.max(z1, z2);
    }

    public SimpleBound(Location location1, Location location2) {
        if (!Objects.requireNonNull(location1.getWorld()).getName().equals(Objects.requireNonNull(location2.getWorld()).getName())) {
            throw new IllegalArgumentException(String.format("Different worlds: location1 world - %s, location2 world - %s", location1.getWorld().getName(), location2.getWorld().getName()));
        }
        this.world = location1.getWorld().getName();
        this.x1 = Math.min(location1.getBlockX(), location2.getBlockX());
        this.y1 = Math.min(location1.getBlockY(), location2.getBlockY());
        this.z1 = Math.min(location1.getBlockZ(), location2.getBlockZ());
        this.x2 = Math.max(location1.getBlockX(), location2.getBlockX());
        this.y2 = Math.max(location1.getBlockY(), location2.getBlockY());
        this.z2 = Math.max(location1.getBlockZ(), location2.getBlockZ());
    }

    @Override
    public boolean isInBounds(Location location) {
        if (!Objects.requireNonNull(location.getWorld()).getName().equals(world)) {
            return false;
        }
        int nx = location.getBlockX();
        int ny = location.getBlockY();
        int nz = location.getBlockZ();
        return (nx >= x1 && nx <= x2) && (ny >= x1 && ny <= y2) && (nz >= z1 && nz <= z2);
    }

    @Override
    public String toString() {
        return "SimpleBound{" +
                "x1=" + x1 +
                ", y1=" + y1 +
                ", z1=" + z1 +
                ", x2=" + x2 +
                ", y2=" + y2 +
                ", z2=" + z2 +
                ", world='" + world + '\'' +
                '}';
    }
}
