package org.shootingcombats.shootingcombats.data;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.Scoreboard;
import org.shootingcombats.shootingcombats.util.Util;

import java.util.UUID;

public final class DmState implements PlayerState {
    private final UUID uuid;
    private double health;
    private int food;
    private float saturation;
    private int expLevel;
    private float expPoints;
    private boolean isInvulnerable;
    private boolean allowFlight;
    private Scoreboard scoreboard;
    private GameMode gamemode;
    private ItemStack[] storageContents;
    private ItemStack[] extraStorageContents;
    private ItemStack[] armourContents;
    private ItemStack itemInOffHand;

    public DmState(UUID uuid) {
        this.uuid = uuid;
    }
    @Override
    public void store() {
        Player player = Bukkit.getPlayer(uuid);

        if (player == null) {
            return;
        }

        this.scoreboard = player.getScoreboard();
        this.gamemode = player.getGameMode();
        this.food = player.getFoodLevel();
        this.saturation = player.getSaturation();
        this.expLevel = player.getLevel();
        this.expPoints = player.getExp();
        this.health = player.getHealth();
        this.isInvulnerable = player.isInvulnerable();
        this.allowFlight = player.getAllowFlight();
        player.setLevel(0);
        player.setExp(0);

        storeInventory();

        Util.log(Bukkit.getPlayer(uuid).getName() + " stored parameters to combat");
    }

    @Override
    public void restore() {
        Player player = Bukkit.getPlayer(uuid);

        if (player == null) {
            return;
        }

        player.setLevel(this.expLevel);
        player.setExp(this.expPoints);
        player.setFoodLevel(this.food);
        player.setSaturation(this.saturation);
        player.setGameMode(this.gamemode);
        player.setScoreboard(this.scoreboard);
        player.setHealth(this.health);
        player.setInvulnerable(this.isInvulnerable);
        player.setAllowFlight(this.allowFlight);

        restoreInventory();

        Util.log(Bukkit.getPlayer(uuid).getName() + " restored parameters from combat");
    }

    private void storeInventory() {
        Player player = Bukkit.getPlayer(this.uuid);
        PlayerInventory playerInventory = player.getInventory();

        this.storageContents = cloneItemStacks(playerInventory.getStorageContents());
        this.extraStorageContents = cloneItemStacks(playerInventory.getExtraContents());
        this.armourContents = cloneItemStacks(playerInventory.getArmorContents());
        this.itemInOffHand = new ItemStack(playerInventory.getItemInOffHand());
        ItemStack[] hotbarContents = new ItemStack[storageContents.length];
        for (int i = 0; i < 9; i++) {
            hotbarContents[i] = storageContents[i] == null ? null : new ItemStack(storageContents[i]);
        }
        playerInventory.clear();
        playerInventory.setStorageContents(hotbarContents);
        playerInventory.setArmorContents(this.armourContents);
        playerInventory.setItemInOffHand(this.itemInOffHand);

        Util.log("Contents storage size: " + storageContents.length);
        Util.log("Armour storage size: " + armourContents.length);
        Util.log(player.getName() + " stored inventory");
    }

    private void restoreInventory() {
        Player player = Bukkit.getPlayer(uuid);
        PlayerInventory playerInventory = player.getInventory();
        playerInventory.setStorageContents(this.storageContents);
        playerInventory.setExtraContents(this.extraStorageContents);
        playerInventory.setArmorContents(this.armourContents);
        playerInventory.setItemInOffHand(this.itemInOffHand);

        Util.log(player.getName() + " restored inventory");
    }

    private ItemStack[] cloneItemStacks(ItemStack[] original) {
        ItemStack[] copy = new ItemStack[original.length];
        for (int i = 0; i < original.length; i++) {
            if (original[i] != null) {
                copy[i] = new ItemStack(original[i]);
            }
        }
        return copy;
    }
}
