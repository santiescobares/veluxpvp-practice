package club.veluxpvp.practice.match.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCCooldown;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.match.MatchManager;
import club.veluxpvp.practice.utilities.ChatUtil;

public class MatchEnderPearlListener implements Listener {

	private MatchManager mm;
	
	public static Map<UUID, Long> pearlCooldown = new HashMap<>();
	private final long COOLDOWN = TimeUnit.SECONDS.toMillis(16);
	
	public MatchEnderPearlListener() {
		this.mm = Practice.getInstance().getMatchManager();
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		if(event.getEntity() != null && event.getEntity() instanceof EnderPearl) {
			EnderPearl pearl = (EnderPearl) event.getEntity();
			
			if(pearl.getShooter() != null && pearl.getShooter() instanceof Player) {
				Player player = (Player) pearl.getShooter();
				Match match = mm.getPlayerMatch(player);
				
				if(match == null) return;
				if(match.isSpectating(player)) return;
				
				pearlCooldown.put(player.getUniqueId(), System.currentTimeMillis() + COOLDOWN);
				LunarClientAPI.getInstance().sendCooldown(player, new LCCooldown("Enderpearl", this.COOLDOWN / 1000, TimeUnit.SECONDS, Material.ENDER_PEARL));
				
		        new BukkitRunnable() {

		            public void run() {
		                long cooldownExpires = pearlCooldown.getOrDefault(player.getUniqueId(), 0L);

		                if(cooldownExpires < System.currentTimeMillis()) {
		                    this.cancel();
		                    return;
		                }

		                int millisLeft = (int) (cooldownExpires - System.currentTimeMillis());
		                float percentLeft = (float) millisLeft / COOLDOWN;

		                player.setExp(percentLeft);
		                player.setLevel(millisLeft / 1_000);
		            }

		        }.runTaskTimer(Practice.getInstance(), 1L, 1L);
				
				new BukkitRunnable() {

					@Override
					public void run() {
						if(pearl == null || pearl.isDead()) {
							this.cancel();
							return;
						}
						
						if(mm.getPlayerMatch(player) == null) {
							this.cancel();
							pearl.remove();
						}
					}
					
				}.runTaskTimerAsynchronously(Practice.getInstance(), 1L, 1L);
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		
		if(match == null) return;
		if(match.isSpectating(player)) return;
		
		if(event.getAction().name().startsWith("RIGHT_CLICK")) {
			if(event.getItem() != null && event.getItem().getType() == Material.ENDER_PEARL) {
				if(!match.getArena().isEnderpearls()) {
					event.setCancelled(true);
					event.setUseInteractedBlock(Result.DENY);
					event.setUseItemInHand(Result.DENY);
					
					player.sendMessage(ChatUtil.TRANSLATE("&cEnderpearls are disabled in this arena!"));
			        player.updateInventory();
			        return;
				}
				
				long timeleft = pearlCooldown.getOrDefault(player.getUniqueId(), 0L);
				
				if(timeleft < System.currentTimeMillis()) {
					pearlCooldown.remove(player.getUniqueId());
					return;
				}
				
				int millisLeft = (int) (timeleft - System.currentTimeMillis());
		        double secondsLeft = millisLeft / 1000D;
		        secondsLeft = Math.round(10D * secondsLeft) / 10D;
		        
		        event.setCancelled(true);
		        player.sendMessage(ChatUtil.TRANSLATE("&cYou can't use this for another &l" + secondsLeft + " &cseconds!"));
		        player.updateInventory();
			}
		}
	}
}
