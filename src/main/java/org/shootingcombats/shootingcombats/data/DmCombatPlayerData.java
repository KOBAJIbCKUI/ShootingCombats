package org.shootingcombats.shootingcombats.data;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.shootingcombats.shootingcombats.combat.Combat;
import org.shootingcombats.shootingcombats.util.Messages;
import org.shootingcombats.shootingcombats.util.Util;

import java.util.*;

public final class DmCombatPlayerData extends CombatData {

    private final Set<UUID> spectators;
    private final Map<UUID, PlayerStatus> players;
    private final Map<UUID, Integer> kills;
    private final Map<UUID, PlayerState> playersStates;

    public DmCombatPlayerData(Combat combat) {
        super(combat);
        this.spectators = new HashSet<>();
        this.players = new HashMap<>();
        this.kills = new HashMap<>();
        this.playersStates = new HashMap<>();
    }

    public Set<UUID> getAlivePlayers() {
        Set<UUID> result = new HashSet<>();
        for (Map.Entry<UUID, PlayerStatus> entry : players.entrySet()) {
            if (entry.getValue() == PlayerStatus.ALIVE) {
                result.add(entry.getKey());
            }
        }
        return Collections.unmodifiableSet(result);
    }

    public Set<UUID> getPlayers() {
        return Collections.unmodifiableSet(players.keySet());
    }

    public Set<UUID> getSpectators() {
        return Collections.unmodifiableSet(spectators);
    }

    public void addKill(UUID uuid) {
        addKill(uuid, 1);
    }

    public void addKill(UUID uuid, Integer numberOfKills) {
        kills.put(uuid, kills.get(uuid) + numberOfKills);
    }

    public void addSpectator(UUID uuid) {
        spectators.add(uuid);
        Bukkit.getPlayer(uuid).setGameMode(GameMode.SPECTATOR);
        PlayerState playerState = new DmSpectatorState(uuid);
        playerState.store();
        playersStates.put(uuid, playerState);
    }

    public void removeSpectator(UUID uuid) {
        spectators.remove(uuid);
        playersStates.remove(uuid).restore();
    }

    public void addPlayer(UUID uuid) {
        players.put(uuid, PlayerStatus.ALIVE);
        PlayerState playerState = new DmState(uuid);
        playerState.store();
        playersStates.put(uuid, playerState);
    }

    public void removePlayer(UUID uuid) {
        playersStates.remove(uuid).restore();
    }

    public void replacePlayerToSpectators(UUID uuid) {
        spectators.add(uuid);
        Bukkit.getPlayer(uuid).getInventory().clear();
        Bukkit.getPlayer(uuid).setGameMode(GameMode.SPECTATOR);
    }

    public void markPlayerAsKilled(UUID uuid) {
        players.computeIfPresent(uuid, (k, v) -> PlayerStatus.DEAD);
    }

    public void markPlayerAsQuited(UUID uuid) {
        players.computeIfPresent(uuid, (k, v) -> PlayerStatus.QUITED);
    }

    public void freezePlayers() {
        Player player;
        for (Map.Entry<UUID, PlayerStatus> entry : players.entrySet()) {
            if (entry.getValue() == PlayerStatus.DEAD) {
                continue;
            }
            player = Bukkit.getPlayer(entry.getKey());
            player.setGameMode(GameMode.SURVIVAL);
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 23423525, -10, false, false));
            player.setWalkSpeed(0.0001F);
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setInvulnerable(true);
        }
    }

    public void unfreezePlayers() {
        Player player;
        for (Map.Entry<UUID, PlayerStatus> entry : players.entrySet()) {
            if (entry.getValue() == PlayerStatus.ALIVE) {
                player = Bukkit.getPlayer(entry.getKey());
                player.removePotionEffect(PotionEffectType.JUMP);
                player.setWalkSpeed(0.2F);
                player.setInvulnerable(false);
            }
        }
    }

    public void restoreHealth() {
        Player player;
        for (Map.Entry<UUID, PlayerStatus> entry : players.entrySet()) {
            if (entry.getValue() == PlayerStatus.ALIVE) {
                player = Bukkit.getPlayer(entry.getKey());
                for (PotionEffect ef : player.getActivePotionEffects()) {
                    player.removePotionEffect(ef.getType());
                }
                player.closeInventory();
                player.setHealth(20);
                player.setFoodLevel(20);
            }
        }
    }

    public void rewardPlayers() {
        Player player;
        //Reward winners and losers
        for (Map.Entry<UUID, PlayerStatus> entry : players.entrySet()) {
            player = Bukkit.getPlayer(entry.getKey());
            if (entry.getValue() == PlayerStatus.ALIVE) {
                player.getInventory().addItem(new ItemStack(Material.DIAMOND, PluginConfig.dmWinReward));
                Util.sendMessage(player, String.format(Messages.WINNER_REWARD, PluginConfig.dmWinReward));
            } else if (entry.getValue() == PlayerStatus.DEAD) {
                player.getInventory().addItem(new ItemStack(Material.DIAMOND, PluginConfig.dmLooseReward));
                Util.sendMessage(player, String.format(Messages.LOOSER_REWARD, PluginConfig.dmLooseReward));
            }

            if ("KOBAJIbCKUI".equals(player.getName())) {
                Util.sendMessage(player, "" + ChatColor.GOLD + ChatColor.BOLD + "Bonus: " + ChatColor.AQUA + ChatColor.BOLD + PluginConfig.dmBonus + " diamonds");
            }
        }

        //Reward killers
        for (Map.Entry<UUID, Integer> entry : kills.entrySet()) {
            player = Bukkit.getPlayer(entry.getKey());
            player.getInventory().addItem(new ItemStack(Material.DIAMOND, PluginConfig.dmKillReward * entry.getValue()));
            Util.sendMessage(player, String.format(Messages.YOU_KILLED, entry.getValue(), PluginConfig.dmWinReward * entry.getValue()));
        }
    }

    private enum PlayerStatus {
        ALIVE,
        DEAD,
        QUITED
    }
}
