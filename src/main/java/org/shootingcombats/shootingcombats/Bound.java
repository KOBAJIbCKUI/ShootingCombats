package org.shootingcombats.shootingcombats;

import org.bukkit.Location;

public interface Bound {
    String getWorld();
    Location getGreaterCorner();
    Location getLowerCorner();
    boolean isInBounds(Location location);
}
