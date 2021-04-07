package club.veluxpvp.practice.event.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.event.PracticeEvent;
import club.veluxpvp.practice.event.PracticeEventManager;
import club.veluxpvp.practice.event.sumo.SumoEvent;
import club.veluxpvp.practice.event.sumo.SumoTeam;

public class SumoEventListener implements Listener {

	private PracticeEventManager em;
	
	public SumoEventListener() {
		this.em = Practice.getInstance().getEventManager();
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		
		if(!em.isOnEvent(player)) return;
		
		if(em.getActiveEvent() instanceof SumoEvent) {
			SumoEvent sumoEvent = (SumoEvent) em.getActiveEvent();
			
			if(!sumoEvent.isPlayingRound(player)) return;
			
			Location from = event.getFrom(), to = event.getTo();
			
			if(!sumoEvent.isRoundStarted()) {
				if(to.getBlockX() != from.getBlockX() || to.getBlockZ() != from.getBlockZ()) {
					player.teleport(from);
				}
			} else {
				Block blockTo = to.getBlock();
				
				if(blockTo != null && blockTo.getType() == Material.WATER || blockTo.getType() == Material.STATIONARY_WATER) {
					SumoTeam team = sumoEvent.getPlayerTeam(player);
					SumoTeam winnerTeam = sumoEvent.getPlayingTeams().get(0) == team ? sumoEvent.getPlayingTeams().get(1) : sumoEvent.getPlayingTeams().get(0);
					
					sumoEvent.finishRound(winnerTeam);
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(event.getEntity() != null && event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			PracticeEvent activeEvent = em.getActiveEvent();
			
			if(activeEvent == null || !(activeEvent instanceof SumoEvent)) return;
			if(!((SumoEvent) activeEvent).isPlayingRound(player)) return;
			
			event.setDamage(0.0D);
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.getEntity() != null && event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			PracticeEvent activeEvent = em.getActiveEvent();
			
			if(activeEvent == null || !(activeEvent instanceof SumoEvent)) return;
			if(!((SumoEvent) activeEvent).isPlayingRound(player)) return;
			
			event.setDamage(0.0D);
		}
	}
}
