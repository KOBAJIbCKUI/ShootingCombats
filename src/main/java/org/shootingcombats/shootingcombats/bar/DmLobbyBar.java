package org.shootingcombats.shootingcombats.bar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.shootingcombats.shootingcombats.lobby.Lobby;

import java.util.UUID;

public final class DmLobbyBar {
    private final BossBar bar;
    private final String titleFormat;
    private String lobbyName, ownerName, statusName;

    public DmLobbyBar(String lobbyName, UUID owner, Lobby.LobbyStatus status) {
        this.lobbyName = "" + ChatColor.GREEN + ChatColor.BOLD + lobbyName;
        this.ownerName = "" + ChatColor.GREEN + ChatColor.BOLD + Bukkit.getPlayer(owner).getName();
        this.statusName = status.toString();
        this.titleFormat = ""
                + ChatColor.WHITE  + ChatColor.BOLD + "Lobby - %s"
                + ChatColor.WHITE + ChatColor.BOLD +  " | "
                + ChatColor.WHITE + ChatColor.BOLD + "Owner - %s"
                + ChatColor.WHITE + ChatColor.BOLD +  " | "
                + ChatColor.WHITE + ChatColor.BOLD + "Status - %s";
        this.bar = Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SOLID);
        this.bar.setProgress(0.0);
        update();

    }

    public void addPlayerToBar(UUID uuid) {
        bar.addPlayer(Bukkit.getPlayer(uuid));
    }

    public void removePlayerFromBar(UUID uuid) {
        bar.removePlayer(Bukkit.getPlayer(uuid));
    }

    public void setLobbyName(String lobbyName) {
        this.lobbyName = "" + ChatColor.GREEN + ChatColor.BOLD + lobbyName;
        update();
    }

    public void setOwner(UUID newOwner) {
        this.ownerName = "" + ChatColor.GREEN + ChatColor.BOLD + Bukkit.getPlayer(newOwner).getName();
        update();
    }

    public void setLobbyStatus(Lobby.LobbyStatus status) {
        this.statusName = status.toString();
        update();
    }

    public void update() {
        bar.setTitle(String.format(titleFormat, lobbyName, ownerName, statusName));
    }
}
