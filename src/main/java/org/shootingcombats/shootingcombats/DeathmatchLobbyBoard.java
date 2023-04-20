package org.shootingcombats.shootingcombats;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.UUID;

public class DeathmatchLobbyBoard {

    private final Scoreboard scoreboard;
    private final Objective winsObjective;
    private final Team ready, notReady, inCombat;
    private UUID owner;

    public DeathmatchLobbyBoard() {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        this.ready = this.scoreboard.registerNewTeam("Ready");
        this.notReady = this.scoreboard.registerNewTeam("Not ready");
        this.inCombat = this.scoreboard.registerNewTeam("In combat");

        ready.setPrefix(ChatColor.GREEN.toString());
        notReady.setPrefix(ChatColor.RED.toString());
        inCombat.setPrefix(ChatColor.GRAY.toString());

        this.winsObjective = scoreboard.registerNewObjective("Players / host", "dummy");
        winsObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void addPlayerToBoard(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        player.setScoreboard(this.scoreboard);
        scoreboard.getObjective(winsObjective.getName()).getScore(Bukkit.getPlayer(uuid).getName()).setScore(0);

    }

    public void removePlayerFromBoard(UUID uuid) {
        scoreboard.resetScores(Bukkit.getPlayer(uuid).getName());
    }

    public void setOwner(UUID uuid) {
        scoreboard.getObjective(winsObjective.getName()).getScore(Bukkit.getPlayer(owner).getName()).setScore(0);
        scoreboard.getObjective(winsObjective.getName()).getScore(Bukkit.getPlayer(uuid).getName()).setScore(1);
        this.owner = uuid;
    }

    public void setPlayerStatus(UUID uuid, Lobby.PlayerStatus playerStatus) {
        if (!scoreboard.getEntries().contains(Bukkit.getPlayer(uuid).getName())) {
            return;
        }
        switch (playerStatus) {
            case READY: {
                ready.addEntry(Bukkit.getPlayer(uuid).getName());
                break;
            }
            case NOT_READY: {
                notReady.addEntry(Bukkit.getPlayer(uuid).getName());
                break;
            }
            case IN_COMBAT: {
                inCombat.addEntry(Bukkit.getPlayer(uuid).getName());
                break;
            }
        }
    }

}
