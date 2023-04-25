package org.shootingcombats.shootingcombats.map;

import org.bukkit.Location;

public interface Bound {
    String getWorld();
    Location getGreaterCorner();
    Location getLowerCorner();
    boolean isInBounds(Location location);
    boolean checkCollision(Bound other);
}
