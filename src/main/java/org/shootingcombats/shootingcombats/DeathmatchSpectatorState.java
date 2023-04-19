package org.shootingcombats.shootingcombats;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.shootingcombats.shootingcombats.util.Util;

import java.util.UUID;

public final class DeathmatchSpectatorState implements PlayerState {
    private final UUID uuid;
    private double health;
    private int food;
    private float saturation;
    private int expLevel;
    private float expPoints;
    private GameMode gamemode;
    private ItemStack[] storageContents, extraStorageContents, armourContents;
    private ItemStack itemInOffHand;

    public DeathmatchSpectatorState(UUID uuid) {
        this.uuid = uuid;
    }
    @Override
    public void store() {
        Player player = Bukkit.getPlayer(uuid);

        if (player == null) {
            return;
        }

        this.gamemode = player.getGameMode();
        this.food = player.getFoodLevel();
        this.saturation = player.getSaturation();
        this.expLevel = player.getLevel();
        this.expPoints = player.getExp();
        this.health = player.getHealth();
        player.setLevel(0);
        player.setExp(0);

        storeInventory();

        Util.log(Bukkit.getPlayer(uuid).getName() + " stored parameters");
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
        player.setHealth(this.health);

        restoreInventory();

        Util.log(Bukkit.getPlayer(uuid).getName() + " restored parameters");
    }

    private void storeInventory() {
        Player player = Bukkit.getPlayer(this.uuid);
        PlayerInventory playerInventory = player.getInventory();

        this.storageContents = cloneItemStacks(playerInventory.getStorageContents());
        this.extraStorageContents = cloneItemStacks(playerInventory.getExtraContents());
        this.armourContents = cloneItemStacks(playerInventory.getArmorContents());
        this.itemInOffHand = new ItemStack(playerInventory.getItemInOffHand());

        playerInventory.clear();

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
