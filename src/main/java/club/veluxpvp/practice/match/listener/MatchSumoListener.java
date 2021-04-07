package club.veluxpvp.practice.match.listener;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.match.MatchManager;
import club.veluxpvp.practice.match.MatchState;

public class MatchSumoListener implements Listener {

	private MatchManager mm;
	
	public MatchSumoListener() {
		this.mm = Practice.getInstance().getMatchManager();
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		
		if(match == null || match.isSpectating(player) || match.getLadder() != Ladder.SUMO) return;
		if(match.getState() == MatchState.ENDING) return;
		
		Location from = event.getFrom();
		Location to = event.getTo();
		
		if(!match.isStarted()) {
			if(to.getBlockX() != from.getBlockX() || to.getBlockZ() != from.getBlockZ()) {
				player.teleport(from);
			}
		} else {
			Block blockAtTo = to.getBlock();
			
			if(blockAtTo == null || blockAtTo.getType() == Material.AIR) return;
			if(blockAtTo.getType() == Material.WATER || blockAtTo.getType() == Material.STATIONARY_WATER) {
				List<ItemStack> drops = new ArrayList<>();
				
				for(ItemStack i : player.getInventory().getContents()) {
					if(i != null && i.getType() != Material.AIR) drops.add(i);
				}
				
				for(ItemStack i : player.getInventory().getArmorContents()) {
					if(i != null && i.getType() != Material.AIR) drops.add(i);
				}
				
				PlayerDeathEvent playerDeathEvent = new PlayerDeathEvent(player, drops, 0, null);
				Bukkit.getPluginManager().callEvent(playerDeathEvent);
				
				player.teleport(match.getArena().getSpectatorsSpawn());
			}
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(event.getEntity() != null && event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Match match = mm.getPlayerMatch(player);
			
			if(match == null || match.isSpectating(player) || match.getLadder() != Ladder.SUMO) return;
			
			event.setDamage(0.0D);
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.getEntity() != null && event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Match match = mm.getPlayerMatch(player);
			
			if(match == null || match.isSpectating(player) || match.getLadder() != Ladder.SUMO) return;
			
			event.setDamage(0.0D);
		}
	}
}
