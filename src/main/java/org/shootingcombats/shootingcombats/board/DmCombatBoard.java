package org.shootingcombats.shootingcombats.board;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.UUID;

public class DmCombatBoard {
    private final Scoreboard scoreboard;
    private final Team deathmatch, spectator;

    public DmCombatBoard() {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        this.deathmatch = this.scoreboard.registerNewTeam("Deathmatch");
        this.spectator = this.scoreboard.registerNewTeam("Spectator");
        this.deathmatch.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
        this.spectator.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
    }

    public void addPlayerToBoard(UUID uuid) {
        Bukkit.getPlayer(uuid).setScoreboard(this.scoreboard);
    }

    public void removePlayerFromBoard(UUID uuid) {
        String playerName = Bukkit.getPlayer(uuid).getName();
        scoreboard.getEntryTeam(playerName).removeEntry(playerName);
        scoreboard.resetScores(playerName);

    }

    public void addSpectator(UUID uuid) {
        spectator.addEntry(Bukkit.getPlayer(uuid).getName());
    }

    public void addPlayer(UUID uuid) {
        deathmatch.addEntry(Bukkit.getPlayer(uuid).getName());
    }
}
