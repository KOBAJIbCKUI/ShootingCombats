package org.shootingcombats.shootingcombats.bar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DmCombatBar {
    private final BossBar bar;
    private final String titleFormat;
    private final long secondsToEnd;
    private long secondsLeft;

    public DmCombatBar(long minutesToEnd) {
        this.titleFormat = "" + ChatColor.WHITE + ChatColor.BOLD + "Time to end - %02d:%02d";
        this.secondsToEnd = TimeUnit.MINUTES.toSeconds(minutesToEnd);
        this.bar = Bukkit.createBossBar(String.format(titleFormat, 0, 0), BarColor.WHITE, BarStyle.SOLID);
        this.bar.setProgress(1.0);
    }

    public void addPlayerToBar(UUID uuid) {
        bar.addPlayer(Bukkit.getPlayer(uuid));
    }

    public void removePlayerFromBar(UUID uuid) {
        bar.removePlayer(Bukkit.getPlayer(uuid));
    }

    public void setSecondsLeft(long secondsLeft) {
        this.secondsLeft = secondsLeft;
        updateBar();
    }
    private void updateBar() {
        if (secondsLeft % 10 == 0) {
            bar.setProgress(1D -  ((double) secondsLeft / secondsToEnd));
        }
        bar.setTitle(String.format(titleFormat, (secondsToEnd - secondsLeft)/60, (secondsToEnd - secondsLeft) % 60));
    }
}
