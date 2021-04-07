package club.veluxpvp.practice.match.listener;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.match.MatchEndReason;
import club.veluxpvp.practice.match.MatchManager;
import club.veluxpvp.practice.match.MatchState;
import club.veluxpvp.practice.match.MatchTeam;
import club.veluxpvp.practice.match.TeamType;
import club.veluxpvp.practice.match.event.MatchResetEvent;
import club.veluxpvp.practice.match.event.MatchStartEvent;
import club.veluxpvp.practice.utilities.ChatUtil;

public class MatchParkourListener implements Listener {

	private MatchManager mm;
	public static Map<UUID, Location> lastCheckpoint = Maps.newConcurrentMap();
	public static Map<UUID, Set<Location>> reachedCheckpoints = Maps.newConcurrentMap();
	public static Map<Match, MatchTeam> winnerTeam = Maps.newConcurrentMap();
	public static Map<Match, UUID> winnerPlayer = Maps.newConcurrentMap();
	
	public MatchParkourListener() {
		this.mm = Practice.getInstance().getMatchManager();
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler
	public void onMatchStart(MatchStartEvent event) {
		Match match = event.getMatch();
		
		if(match.getLadder() == Ladder.PARKOUR) {
			for(Player p : match.getAlivePlayers()) {
				reachedCheckpoints.put(p.getUniqueId(), Sets.newHashSet());
			}
		}
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		
		if(match == null || match.isSpectating(player) || match.getLadder() != Ladder.PARKOUR) return;
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		
		if(match == null || match.isSpectating(player) || match.getLadder() != Ladder.PARKOUR) return;
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(event.getEntity() != null && event.getDamager() != null && event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			Match match = mm.getPlayerMatch(player);
			
			if(match == null || match.isSpectating(player) || match.getLadder() != Ladder.PARKOUR) return;
			
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.getEntity() != null && event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Match match = mm.getPlayerMatch(player);
			
			if(match == null || match.isSpectating(player) || match.getLadder() != Ladder.PARKOUR) return;
			
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		
		if(match == null || match.isSpectating(player) || match.getLadder() != Ladder.PARKOUR) return;
		
		Location from = event.getFrom();
		Location to = event.getTo();
		
		if(!match.isStarted()) {
			if(to.getBlockX() != from.getBlockX() || to.getBlockZ() != from.getBlockZ()) {
				player.teleport(from);
			}
		} else {
			if(match.getState() == MatchState.ENDING) return;
			
			if(to.getBlockY() <= 0) {
				Location corner = match.getPlayerTeam(player).getType() == TeamType.TEAM_1 ? match.getArena().getCorner1() : match.getArena().getCorner2();
				
				player.teleport((lastCheckpoint.get(player.getUniqueId()) != null ? lastCheckpoint.get(player.getUniqueId()) : corner));
				return;
			}
			
			// Is checkpoint
			if(to.getBlock().getType() == Material.IRON_PLATE && match.getArena().isCheckpoint(to.getBlock().getLocation())) {
				Location checkpoint = to.getBlock().getLocation();
				
				if(!this.isCheckpointReached(player, checkpoint)) {
					reachedCheckpoints.get(player.getUniqueId()).add(checkpoint);
					lastCheckpoint.put(player.getUniqueId(), checkpoint);
					
					player.sendMessage(ChatUtil.TRANSLATE("&aNew Checkpoint: &l" + reachedCheckpoints.get(player.getUniqueId()).size() + "/" + match.getArena().getParkourCheckpoints().size()));
				}
			}
			
			if(to.getBlock().getType() == Material.GOLD_PLATE) {
				if(match.isFfa()) {
					winnerPlayer.put(match, player.getUniqueId());
					match.tryFinishGame(MatchEndReason.PARKOUR_FINISHED);
				} else {
					winnerTeam.put(match, match.getPlayerTeam(player));
					match.tryFinishGame(MatchEndReason.PARKOUR_FINISHED);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		
		if(match == null || !match.isStarted() || match.isSpectating(player) || match.getLadder() != Ladder.PARKOUR) return;
		
		if(event.getAction().name().startsWith("RIGHT_CLICK")) {
			ItemStack item = event.getItem();
			
			if(item == null || item.getType() == Material.AIR) return;
			
			// Back to last checkpoint
			if(item.getType() == Material.BEACON) {
				if(match.getState() == MatchState.STARTING) return;
				
				Location lastCheckpoint = MatchParkourListener.lastCheckpoint.get(player.getUniqueId());
				
				if(lastCheckpoint == null) {
					player.teleport(match.getArena().getCorner1());
					return;
				}
				
				lastCheckpoint.setYaw(player.getLocation().getYaw());
				lastCheckpoint.setPitch(player.getLocation().getPitch());
				
				player.teleport(lastCheckpoint);
				player.sendMessage(ChatUtil.TRANSLATE("&aTeleported to your last checkpoint!"));
			}
		}
	}
	
	@EventHandler
	public void onMatchReset(MatchResetEvent event) {
		Match match = event.getMatch();
		
		if(match.getLadder() == Ladder.PARKOUR) {
			winnerTeam.remove(match);
			winnerPlayer.remove(match);
			
			for(Player p : match.getPlayers()) {
				lastCheckpoint.remove(p.getUniqueId());
				reachedCheckpoints.remove(p.getUniqueId());
			}
		}
	}
	
	public boolean isCheckpointReached(Player player, Location location) {
		for(Map.Entry<UUID, Set<Location>> checkpointsEntry : reachedCheckpoints.entrySet()) {
			if(checkpointsEntry.getKey().equals(player.getUniqueId())) {
				for(Location l : checkpointsEntry.getValue()) {
					if(l.getBlockX() == location.getBlockX() && l.getBlockY() == location.getBlockY() && l.getBlockZ() == location.getBlockZ()) {
						return true;
					}
				}
			}
		}

		return false;
	}
}
