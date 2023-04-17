package org.shootingcombats.shootingcombats;

import java.util.Set;
import java.util.UUID;

public interface Combat {
    void joinAsPlayer(UUID uuid);
    void leaveAsPlayer(UUID uuid);
    void joinAsSpectator(UUID uuid);
    void leaveAsSpectator(UUID uuid);
    Set<UUID> getPlayers();
    Set<UUID> getSpectators();
    void onKill(UUID killer, UUID killed);
    void onQuit(UUID uuid);
    void start();
    void forcedStop();
    void normalStop();
}
