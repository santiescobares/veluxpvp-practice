package club.veluxpvp.practice.match.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.item.ItemManager;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.match.MatchManager;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.PlayerUtil;

public class MatchSpectateListener implements Listener {

	private MatchManager mm;
	private Map<UUID, Long> hiderCooldown;
	
	public MatchSpectateListener() {
		this.mm = Practice.getInstance().getMatchManager();
		this.hiderCooldown = new HashMap<>();
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		
		if(match == null || !match.isSpectating(player)) return;

		event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		
		if(match == null || !match.isSpectating(player)) return;
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		
		if(match == null || !match.isSpectating(player)) return;
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		
		if(match == null || !match.isSpectating(player)) return;
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(event.getEntity() != null && event.getDamager() != null && event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			Match match = mm.getPlayerMatch(player);
			
			if(match == null || !match.isSpectating(player)) return;
			
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.getEntity() != null && event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Match match = mm.getPlayerMatch(player);
			
			if(match == null || !match.isSpectating(player)) return;
			
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Match match = mm.getPlayerMatch(player);
		
		if(match == null || !match.isSpectating(player)) return;
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		
		if(match == null || !match.isSpectating(player)) return;
		
		if(event.getAction().name().startsWith("RIGHT_CLICK")) {
			ItemStack item = event.getItem();
			
			event.setCancelled(true);
			event.setUseInteractedBlock(Result.DENY);
			event.setUseItemInHand(Result.DENY);
			
			if(item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return;
			
			// Hide Spectators
			if(item.equals(ItemManager.getSpectatorHideSpectators())) {
				long timeleft = this.hiderCooldown.getOrDefault(player.getUniqueId(), System.currentTimeMillis());
				
				if(timeleft > System.currentTimeMillis()) {
					player.sendMessage(ChatUtil.TRANSLATE("&cPlease wait before do this again!"));
					return;
				}
				
				match.getShowingSpectators().put(player.getUniqueId(), false);
				
				player.setItemInHand(ItemManager.getSpectatorShowSpectators());
				player.updateInventory();
				player.sendMessage(ChatUtil.TRANSLATE("&cAll spectators are now hidden!"));
				
				PlayerUtil.updateVisibility(player);
				
				this.hiderCooldown.put(player.getUniqueId(), System.currentTimeMillis() + (2 * 1000));
			}
			
			// Show Spectators
			if(item.equals(ItemManager.getSpectatorShowSpectators())) {
				long timeleft = this.hiderCooldown.getOrDefault(player.getUniqueId(), 0L);
				
				if(timeleft > System.currentTimeMillis()) {
					player.sendMessage(ChatUtil.TRANSLATE("&cPlease wait before do this again!"));
					return;
				}
				
				match.getShowingSpectators().put(player.getUniqueId(), true);
				
				player.setItemInHand(ItemManager.getSpectatorHideSpectators());
				player.updateInventory();
				player.sendMessage(ChatUtil.TRANSLATE("&aAll spectators are now visible!"));
				
				PlayerUtil.updateVisibility(player);
				
				this.hiderCooldown.put(player.getUniqueId(), System.currentTimeMillis() + (2 * 1000));
			}
			
			// Teleport to Last PvP
			if(item.equals(ItemManager.getSpectatorLastPvP())) {
				Location l = match.getLastPvPLocation();
				
				if(l == null) {
					player.sendMessage(ChatUtil.TRANSLATE("&cLast PvP location not found!"));
				} else {
					player.teleport(l);
				}
			}
			
			// Back to Spawn
			if(item.equals(ItemManager.getSpectatorBackToSpawn())) {
				match.removeSpectator(player, false);
			}
		}
	}
}
