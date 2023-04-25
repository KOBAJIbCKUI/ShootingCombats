package org.shootingcombats.shootingcombats.manager;

import org.shootingcombats.shootingcombats.map.CombatMap;

import java.util.Map;
import java.util.Optional;

public interface CombatMapManager {
    boolean addMap(CombatMap combatMap);
    boolean removeMap(CombatMap combatMap);
    boolean removeMap(String name);
    boolean containsMap(CombatMap combatMap);
    boolean containsMap(String name);
    int getMapsNumber();
    Map<CombatMap, CombatMapStatus> getMaps();
    void setMapOccupation(String name, CombatMapStatus mapStatus);
    void setMapOccupation(CombatMap combatMap, CombatMapStatus mapStatus);
    CombatMapStatus getMapStatus(CombatMap combatMap);
    CombatMapStatus getMapStatus(String name);
    Optional<CombatMap> getMap(String name);

    enum CombatMapStatus {
        OCCUPIED("occupied"),
        FREE("free"),
        NA("na");

        private final String name;

        CombatMapStatus(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
