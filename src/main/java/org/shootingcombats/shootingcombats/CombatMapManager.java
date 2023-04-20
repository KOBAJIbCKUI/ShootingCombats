package org.shootingcombats.shootingcombats;

import java.util.Map;
import java.util.Optional;

public interface CombatMapManager {
    boolean addMap(CombatMap combatMap);
    boolean removeMap(CombatMap combatMap);
    boolean containsMap(CombatMap combatMap);
    boolean containsMap(String name);
    Map<CombatMap, CombatMapStatus> getMaps();
    void setMapOccupation(String name, CombatMapStatus mapStatus);
    void setMapOccupation(CombatMap combatMap, CombatMapStatus mapStatus);
    CombatMapStatus getMapStatus(CombatMap combatMap);
    CombatMapStatus getMapStatus(String name);
    Optional<CombatMap> getMap(String name);

    enum CombatMapStatus {
        OCCUPIED,
        FREE,
        NA
    }
}
