package club.veluxpvp.practice.staffmode;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Maps;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.item.ItemManager;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.PlayerUtil;

public class StaffModeManager {

	private Map<UUID, SavedStuff> playerStuff;
	
	public StaffModeManager() {
		this.playerStuff = Maps.newConcurrentMap();
	}
	
	public boolean isInStaffMode(Player player) {
		return player.hasMetadata("StaffMode");
	}
	
	public boolean isVanished(Player player) {
		return player.hasMetadata("Vanished");
	}
	
	public boolean isFreezed(Player player) {
		return player.hasMetadata("Frozen");
	}
	
	public void onDisable() {
		Bukkit.getOnlinePlayers().stream()
		.filter(p -> this.isInStaffMode(p))
		.forEach(p -> this.disableStaffMode(p));
	}
	
	public void enableStaffMode(Player player) {
		player.setMetadata("StaffMode", new FixedMetadataValue(Practice.getInstance(), true));
		this.playerStuff.put(player.getUniqueId(), new SavedStuff(player));
		this.setVanished(player, true);
		PlayerUtil.reset(player, GameMode.CREATIVE, false);
		ItemManager.loadStaffModeItems(player);
		PlayerUtil.updateVisibility(player);
	}
	
	public void disableStaffMode(Player player) {
		player.removeMetadata("StaffMode", Practice.getInstance());
		
		PlayerUtil.reset(player, player.getGameMode(), false);
		Bukkit.getScheduler().runTaskLater(Practice.getInstance(), () -> {
			this.playerStuff.get(player.getUniqueId()).applyBack(player);
			this.playerStuff.remove(player.getUniqueId());
		}, 2L);
		
		this.setVanished(player, false);
		PlayerUtil.updateVisibility(player);
	}
	
	public void setVanished(Player player, boolean vanished) {
		if(vanished) {
			player.setMetadata("Vanished", new FixedMetadataValue(Practice.getInstance(), true));
			PlayerUtil.updateVisibility(player);
			
			if(this.isInStaffMode(player)) {
				player.setItemInHand(ItemManager.getSMVanishOn());
				player.updateInventory();
				return;
			}
		} else {
			player.removeMetadata("Vanished", Practice.getInstance());
			PlayerUtil.updateVisibility(player);
			
			if(this.isInStaffMode(player)) {
				player.setItemInHand(ItemManager.getSMVanishOff());
				player.updateInventory();
				return;
			}
		}
	}
	
	public void setFreezed(Player player, boolean freezed) {
		if(freezed) {
			player.setMetadata("Frozen", new FixedMetadataValue(Practice.getInstance(), true));
			
			player.setWalkSpeed(0.0F);
			player.setFlySpeed(0.0F);
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 200, true), true);
			player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, Integer.MAX_VALUE, 200, true), true);
			
			sendFrozenMessage(player);
			
			new BukkitRunnable() {

				@Override
				public void run() {
					if(player == null || !isFreezed(player)) {
						this.cancel();
						return;
					}
					
					sendFrozenMessage(player);
				}
				
			}.runTaskTimerAsynchronously(Practice.getInstance(), 8 * 20L, 8 * 20L);
		} else {
			player.removeMetadata("Frozen", Practice.getInstance());
			player.setWalkSpeed(0.2F);
			player.setFlySpeed(0.2F);
			player.removePotionEffect(PotionEffectType.JUMP);
			player.removePotionEffect(PotionEffectType.HUNGER);
			player.setFoodLevel(20);
			player.sendMessage(ChatUtil.TRANSLATE("&aYou have been unfrozen!"));
		}
	}
	
	private void sendFrozenMessage(Player player) {
		player.sendMessage(" ");
		player.sendMessage(ChatUtil.TRANSLATE("&cYou have been frozen by a staff member!"));
		player.sendMessage(ChatUtil.TRANSLATE("&4Don't logout &cor you will get banned."));
		player.sendMessage(ChatUtil.TRANSLATE("&bPlease do all the staff says!"));
		player.sendMessage(" ");
	}
}
