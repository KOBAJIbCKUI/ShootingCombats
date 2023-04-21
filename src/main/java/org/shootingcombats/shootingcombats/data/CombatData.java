package org.shootingcombats.shootingcombats.data;

import org.shootingcombats.shootingcombats.combat.Combat;

public abstract class CombatData {
    private final Combat combat;

    public CombatData(Combat combat) {
        this.combat = combat;
    }

    public Combat getCombat() {
        return this.combat;
    }
}
