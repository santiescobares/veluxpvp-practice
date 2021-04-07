package club.veluxpvp.practice.arena.listener;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Arena;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.Cuboid;

public class ArenaBoundsListener implements Listener {

	public static Map<UUID, Cuboid> makingBounds = new ConcurrentHashMap<>();
	public static Map<UUID, Arena> arenaMap = new ConcurrentHashMap<>();
	
	public ArenaBoundsListener() {
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		makingBounds.remove(player.getUniqueId());
		arenaMap.remove(player.getUniqueId());
		player.getInventory().remove(Material.DIAMOND_HOE);
	}
	
	@EventHandler
	public void onPlayerKick(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		makingBounds.remove(player.getUniqueId());
		arenaMap.remove(player.getUniqueId());
		player.getInventory().remove(Material.DIAMOND_HOE);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Cuboid bounds = makingBounds.get(player.getUniqueId());
		
		if(bounds == null) return;
		
		ItemStack item = event.getItem();
		
		if(item != null && item.getType() == Material.DIAMOND_HOE && item.hasItemMeta()) {
			Block block = event.getClickedBlock();
			
			if(block == null || block.getType() == Material.AIR) {
				player.sendMessage(ChatUtil.TRANSLATE("&cYou can't select air!"));
				return;
			}
			
			if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
				event.setCancelled(true);
				event.setUseInteractedBlock(Result.DENY);
				event.setUseItemInHand(Result.DENY);
				
				bounds.setLocation1(block.getLocation());
				
				player.sendMessage(ChatUtil.TRANSLATE("&fFirst position &asetted &fon &b" + block.getLocation().getBlockX() + ", " + block.getLocation().getBlockY() + ", " + block.getLocation().getBlockZ() + "&f!"));
				return;
			} else {
				event.setCancelled(true);
				event.setUseInteractedBlock(Result.DENY);
				event.setUseItemInHand(Result.DENY);
				
				bounds.setLocation2(block.getLocation());
				
				player.sendMessage(ChatUtil.TRANSLATE("&fSecond position &asetted &fon &b" + block.getLocation().getBlockX() + ", " + block.getLocation().getBlockY() + ", " + block.getLocation().getBlockZ() + "&f!"));
				return;
			}
		}
	}
}
