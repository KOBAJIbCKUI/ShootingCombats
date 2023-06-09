package org.shootingcombats.shootingcombats.map;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Objects;

public final class SimpleBound implements Bound {
    private final int x1, y1, z1; //Lesser corner
    private final int x2, y2, z2; //Greater corner
    private final String world; //World name
    //private Set<Entity> entities;

    public SimpleBound(String world, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.world = Objects.requireNonNull(world);
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
    public String getWorld() {
        return this.world;
    }

    @Override
    public Location getGreaterCorner() {
        return new Location(Bukkit.getWorld(world), x2, y2, z2);
    }

    @Override
    public Location getLowerCorner() {
        return new Location(Bukkit.getWorld(world), x1, y1, z1);
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
    public boolean checkCollision(Bound other) {
        return other.isInBounds(getGreaterCorner()) || other.isInBounds(getLowerCorner()) || isInBounds(other.getGreaterCorner()) || isInBounds(other.getLowerCorner());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleBound that = (SimpleBound) o;
        return x1 == that.x1 && y1 == that.y1 && z1 == that.z1 && x2 == that.x2 && y2 == that.y2 && z2 == that.z2 && world.equals(that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x1, y1, z1, x2, y2, z2, world);
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
