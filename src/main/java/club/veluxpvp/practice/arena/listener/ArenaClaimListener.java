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
import club.veluxpvp.practice.arena.Claim;
import club.veluxpvp.practice.match.TeamType;
import club.veluxpvp.practice.utilities.ChatUtil;

public class ArenaClaimListener implements Listener {

	public static Map<UUID, Arena> arenaClaimMap = new ConcurrentHashMap<>();
	public static Map<UUID, Claim> makingClaim = new ConcurrentHashMap<>();
	public static Map<UUID, TeamType> teamClaimMap = new ConcurrentHashMap<>();
	
	public ArenaClaimListener() {
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		arenaClaimMap.remove(player.getUniqueId());
		makingClaim.remove(player.getUniqueId());
		teamClaimMap.remove(player.getUniqueId());
		player.getInventory().remove(Material.DIAMOND_HOE);
	}
	
	@EventHandler
	public void onPlayerKick(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		arenaClaimMap.remove(player.getUniqueId());
		makingClaim.remove(player.getUniqueId());
		teamClaimMap.remove(player.getUniqueId());
		player.getInventory().remove(Material.DIAMOND_HOE);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Claim claim = makingClaim.get(player.getUniqueId());
		TeamType team = teamClaimMap.get(player.getUniqueId());
		
		if(claim == null || team == null) return;
		
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
				
				claim.setCorner1(block.getLocation());
				
				player.sendMessage(ChatUtil.TRANSLATE("&fFirst position &asetted &fon &b" + block.getLocation().getBlockX() + ", " + block.getLocation().getBlockY() + ", " + block.getLocation().getBlockZ() + "&f!"));
				return;
			} else {
				event.setCancelled(true);
				event.setUseInteractedBlock(Result.DENY);
				event.setUseItemInHand(Result.DENY);
				
				claim.setCorner2(block.getLocation());
				
				player.sendMessage(ChatUtil.TRANSLATE("&fSecond position &asetted &fon &b" + block.getLocation().getBlockX() + ", " + block.getLocation().getBlockY() + ", " + block.getLocation().getBlockZ() + "&f!"));
				return;
			}
		}
	}
}
