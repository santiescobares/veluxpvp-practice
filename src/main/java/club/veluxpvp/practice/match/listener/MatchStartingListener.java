package club.veluxpvp.practice.match.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.match.MatchManager;

public class MatchStartingListener implements Listener {

	private MatchManager mm;
	
	public MatchStartingListener() {
		this.mm = Practice.getInstance().getMatchManager();
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(event.getEntity() != null && event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Match match = mm.getPlayerMatch(player);
			
			if(match == null || match.isStarted()) return;
			if(match.isSpectating(player)) return;
			
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.getEntity() != null && event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Match match = mm.getPlayerMatch(player);
			
			if(match == null || match.isStarted()) return;
			if(match.isSpectating(player)) return;
			
			event.setCancelled(true);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		
		if(match == null || match.isStarted()) return;
		if(match.isSpectating(player)) return;
		
		if(event.getAction().name().startsWith("RIGHT_CLICK")) {
			ItemStack item = event.getItem();
			
			if(item == null || item.getType() == Material.AIR) return;
			if(item.getType() == Material.ENDER_PEARL) {
				event.setCancelled(true);
				player.updateInventory();
				return;
			}
			
			byte[] potionValues = {(byte) 16421, (byte) 16453, (byte) 16388, (byte) 16420, (byte) 16452, (byte) 16426, (byte) 16458};
			boolean potionMatch = false;
			
			for(int i = 0; i < potionValues.length; i++) {
				if(item.getType() == Material.POTION && item.getData().getData() == potionValues[i]) {
					potionMatch = true;
					break;
				}
			}
			
			if(potionMatch) {
				event.setCancelled(true);
				player.updateInventory();
			}
		}
	}
}
