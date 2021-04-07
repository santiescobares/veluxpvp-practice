package club.veluxpvp.practice.staffmode.listener;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.item.ItemManager;
import club.veluxpvp.practice.staffmode.StaffModeManager;
import club.veluxpvp.practice.utilities.ChatUtil;

public class StaffModeListener implements Listener {

	private StaffModeManager sm;
	
	public StaffModeListener() {
		this.sm = Practice.getInstance().getStaffModeManager();
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		if(sm.isFreezed(player)) {
			for(Player staff : Bukkit.getOnlinePlayers()) {
				if(!staff.hasPermission("core.staff")) return;
				staff.sendMessage(ChatUtil.TRANSLATE("&9[S] &4" + player.getName() + " &cleft the server whilst frozen!"));
			}
			
			sm.setFreezed(player, false);
		}
		
		if(sm.isInStaffMode(player)) {
			sm.disableStaffMode(player);
		}
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		Player player = event.getPlayer();
		
		if(sm.isInStaffMode(player)) {
			sm.disableStaffMode(player);
		}
		
		if(sm.isFreezed(player)) sm.setFreezed(player, false);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		
		if(sm.isInStaffMode(player) || sm.isVanished(player)) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		
		if(sm.isInStaffMode(player) || sm.isVanished(player)) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		
		if(sm.isInStaffMode(player) || sm.isVanished(player)) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		
		if(sm.isInStaffMode(player) || sm.isVanished(player)) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		
		if(sm.isInStaffMode(player)) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(event.getDamager() != null && event.getDamager() instanceof Player) {
			Player damager = (Player) event.getDamager();
			
			if(sm.isInStaffMode(damager) || sm.isVanished(damager)) {
				event.setCancelled(true);
				return;
			}
			
			if(sm.isFreezed(damager)) {
				event.setCancelled(true);
				return;
			}
		}
		
		if(event.getEntity() != null && event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			
			if(sm.isInStaffMode(player) || sm.isVanished(player)) {
				event.setCancelled(true);
				return;
			}
			
			if(sm.isFreezed(player)) {
				event.setCancelled(true);
				
				if(event.getDamager() != null && event.getDamager() instanceof Player) {
					((Player) event.getDamager()).sendMessage(ChatUtil.TRANSLATE("&c" + player.getName() + " is frozen!"));
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if(!sm.isInStaffMode(player)) return;
		
		event.setCancelled(true);
		event.setUseInteractedBlock(Result.DENY);
		event.setUseItemInHand(Result.DENY);
		
		ItemStack item = event.getItem();
		
		if(item == null || item.getType() == Material.AIR) return;
		if(item.equals(ItemManager.getSMVanishOn())) {
			sm.setVanished(player, false);
			return;
		}
		
		if(item.equals(ItemManager.getSMVanishOff())) {
			sm.setVanished(player, true);
			return;
		}
		
		if(item.equals(ItemManager.getSMRandomTeleporter())) {
			List<Player> players = Bukkit.getOnlinePlayers().stream().filter(p -> !sm.isInStaffMode(p) && !sm.isVanished(p)).collect(Collectors.toList());
		
			if(players.size() == 0) {
				player.sendMessage(ChatUtil.TRANSLATE("&cThere are no enough players online!"));
				return;
			}
			
			Player target = players.get(new Random().nextInt(players.size()));
			
			player.teleport(target.getLocation());
			player.sendMessage(ChatUtil.TRANSLATE("You have been randomly teleported to &b" + target.getName() + "&f!"));
		}
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		
		if(!sm.isInStaffMode(player)) return;
		
		event.setCancelled(true);
		Entity entity = event.getRightClicked();
		
		if(entity != null && entity instanceof Player) {
			Player target = (Player) entity;
			ItemStack item = player.getItemInHand();
			
			if(item == null || item.getType() == Material.AIR) return;
			if(item.equals(ItemManager.getSMInventoryInspector())) {
				player.openInventory(target.getInventory());
				player.sendMessage(ChatUtil.TRANSLATE("Examining &b" + target.getName() + "&f's inventory."));
				return;
			}
			
			if(item.equals(ItemManager.getSMFreeze())) {
				if(!sm.isFreezed(target)) {
					sm.setFreezed(target, true);
					player.sendMessage(ChatUtil.TRANSLATE("You have frozen &b" + target.getName() + "&f!"));
					
					for(Player staff : Bukkit.getOnlinePlayers()) {
						if(!staff.hasPermission("core.freeze.notify")) return;
						staff.sendMessage(ChatUtil.TRANSLATE("&9[S] &b" + target.getName() + " &7has been frozen by &b" + player.getName() + "&7."));
					}
				} else {
					sm.setFreezed(target, false);
					player.sendMessage(ChatUtil.TRANSLATE("You have unfrozen &b" + target.getName() + "&f!"));
					
					for(Player staff : Bukkit.getOnlinePlayers()) {
						if(!staff.hasPermission("core.freeze.notify")) return;
						staff.sendMessage(ChatUtil.TRANSLATE("&9[S] &b" + target.getName() + " &7has been unfrozen by &b" + player.getName() + "&7."));
					}
				}
			}
		}
	}
}
