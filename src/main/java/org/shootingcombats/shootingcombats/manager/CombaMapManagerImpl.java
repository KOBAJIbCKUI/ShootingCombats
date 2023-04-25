package org.shootingcombats.shootingcombats.manager;

import org.shootingcombats.shootingcombats.map.CombatMap;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class CombaMapManagerImpl implements CombatMapManager {

    private final Map<CombatMap, CombatMapStatus> maps;

    public CombaMapManagerImpl() {
        this.maps = new LinkedHashMap<>();
    }

    @Override
    public boolean addMap(CombatMap combatMap) {
        if (!containsMap(combatMap)) {
            maps.put(combatMap, CombatMapStatus.FREE);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeMap(CombatMap combatMap) {
        if (containsMap(combatMap) && getMapStatus(combatMap) == CombatMapStatus.FREE) {
            maps.remove(combatMap);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeMap(String name) {
        if (containsMap(name) && getMapStatus(name) == CombatMapStatus.FREE) {
            maps.remove(getMap(name).get());
            return true;
        }
        return false;
    }

    @Override
    public boolean containsMap(CombatMap combatMap) {
        return maps.containsKey(combatMap);
    }

    @Override
    public boolean containsMap(String name) {
        return maps.keySet().stream().anyMatch(map -> map.getName().equals(name));
    }

    @Override
    public int getMapsNumber() {
        return maps.size();
    }

    @Override
    public Map<CombatMap, CombatMapStatus> getMaps() {
        return Collections.unmodifiableMap(maps);
    }

    @Override
    public void setMapOccupation(String name, CombatMapStatus mapStatus) {
        for (CombatMap combatMap : maps.keySet()) {
            if (combatMap.getName().equals(name)) {
                maps.put(combatMap, mapStatus);
                break;
            }
        }
    }

    @Override
    public void setMapOccupation(CombatMap combatMap, CombatMapStatus mapStatus) {
        if (containsMap(combatMap)) {
            maps.put(combatMap, mapStatus);
        }
    }

    @Override
    public CombatMapStatus getMapStatus(CombatMap combatMap) {
        if (containsMap(combatMap)) {
            return maps.get(combatMap);
        }
        return CombatMapStatus.NA;
    }

    @Override
    public CombatMapStatus getMapStatus(String name) {
        Optional<CombatMap> combatMap = getMap(name);
        return combatMap.isPresent() ? maps.get(combatMap.get()) : CombatMapStatus.NA;
    }


    @Override
    public Optional<CombatMap> getMap(String name) {
        for (Map.Entry<CombatMap, CombatMapStatus> entry : maps.entrySet()) {
            if (entry.getKey().getName().equals(name)) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }
}
