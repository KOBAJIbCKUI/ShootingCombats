package org.shootingcombats.shootingcombats;

import java.util.UUID;

public interface Combat {
    void joinAsPlayer(UUID uuid);
    void leaveAsPlayer(UUID uuid);
    void joinAsSpectator(UUID uuid);
    void leaveAsSpectator(UUID uuid);
    Iterable<UUID> getPlayers();
    Iterable<UUID> getSpectators();
    void onKill(UUID killer, UUID killed);
    void onQuit(UUID uuid);
    void start();
    void forcedStop();
    void normalStop();
    void callPreCombatActions();
    void callInnerCombatActions();
    void callPostCombatActions();
}
