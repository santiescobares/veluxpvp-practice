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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Arena;
import club.veluxpvp.practice.utilities.ChatUtil;

public class ArenaCheckpointsListener implements Listener {

	public static Map<UUID, Arena> makingCheckpoints = new ConcurrentHashMap<>();
	
	public ArenaCheckpointsListener() {
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) { makingCheckpoints.remove(event.getPlayer().getUniqueId()); }
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) { makingCheckpoints.remove(event.getPlayer().getUniqueId()); }
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Arena arena = makingCheckpoints.get(player.getUniqueId());
		
		if(arena == null) return;
		
		Block block = event.getBlock();
		
		if(block != null && block.getType() == Material.IRON_PLATE) {
			if(arena.removeCheckpoint(block.getLocation())) {
				player.sendMessage(ChatUtil.TRANSLATE("&cCheckpoint removed!"));
			} else {
				player.sendMessage(ChatUtil.TRANSLATE("&cThis block is not a checkpoint!"));
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Arena arena = makingCheckpoints.get(player.getUniqueId());
		
		if(arena == null) return;
		
		Block block = event.getBlock();
		
		if(block != null && block.getType() == Material.IRON_PLATE) {
			if(arena.addCheckpoint(block.getLocation())) {
				player.sendMessage(ChatUtil.TRANSLATE("&aCheckpoint added!"));
			} else {
				player.sendMessage(ChatUtil.TRANSLATE("&cThere's already a checkpoint set on this position!"));
			}
		}
	}
}
