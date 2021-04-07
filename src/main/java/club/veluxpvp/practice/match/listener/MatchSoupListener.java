package club.veluxpvp.practice.match.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.match.MatchManager;

public class MatchSoupListener implements Listener {

	private MatchManager mm;
	
	public MatchSoupListener() {
		this.mm = Practice.getInstance().getMatchManager();
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		
		if(match == null || match.isSpectating(player) || match.getLadder() != Ladder.SOUP && match.getLadder() != Ladder.HG) return;
		
		if(event.getAction().name().startsWith("RIGHT_CLICK")) {
			ItemStack item = event.getItem();
			
			if(item != null && item.getType() == Material.MUSHROOM_SOUP) {
				if(player.getHealth() == player.getMaxHealth()) return;
				
				event.setCancelled(true);
				event.setUseItemInHand(Result.DENY);
				
				double newHealth = player.getHealth() + 7.0D;
				if(newHealth > player.getMaxHealth()) newHealth = player.getMaxHealth();
				
				player.setHealth(newHealth);
				player.setItemInHand(new ItemStack(Material.BOWL));
				player.updateInventory();
			}
		}
	}
}
