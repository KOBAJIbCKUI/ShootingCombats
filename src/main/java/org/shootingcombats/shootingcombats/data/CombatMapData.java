package org.shootingcombats.shootingcombats.data;

import org.bukkit.Location;
import org.shootingcombats.shootingcombats.Bound;
import org.shootingcombats.shootingcombats.Combat;
import org.shootingcombats.shootingcombats.CombatMap;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class CombatMapData extends CombatData {
    private final List<Location> spawns;
    private final Bound bound;

    public CombatMapData(Combat combat, CombatMap combatMap) {
        super(combat);
        this.spawns = combatMap.getSpawns();
        this.bound = combatMap.getBound();
    }

    public boolean isInRegion(Location location) {
        return bound.isInBounds(location);
    }

    public Collection<Location> getSpawns() {
        return Collections.unmodifiableList(spawns);
    }
}
