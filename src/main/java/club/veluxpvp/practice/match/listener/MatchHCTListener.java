package club.veluxpvp.practice.match.listener;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.match.MatchManager;
import club.veluxpvp.practice.match.event.MatchResetEvent;
import club.veluxpvp.practice.party.pvpclass.BardClass;
import club.veluxpvp.practice.party.pvpclass.HCFClassManager;
import club.veluxpvp.practice.party.pvpclass.HCFClassType;
import club.veluxpvp.practice.party.pvpclass.BardClass.BardEffectType;
import club.veluxpvp.practice.utilities.ChatUtil;

public class MatchHCTListener implements Listener {

	private MatchManager mm;
	private HCFClassManager classManager;
	public static Map<UUID, Long> archerMark = Maps.newConcurrentMap();
	private static final long ARCHER_MARK_MILLIS = TimeUnit.SECONDS.toMillis(10);
	private static Map<UUID, BukkitTask> updateNametagTask = Maps.newHashMap();
	
	public MatchHCTListener() {
		this.mm = Practice.getInstance().getMatchManager();
		this.classManager = Practice.getInstance().getHcfClassManager();
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		
		if(match == null || match.isSpectating(player)) return;
		if(match.getLadder() != Ladder.HCT_NO_DEBUFF && match.getLadder() != Ladder.HCT_DEBUFF) return;
		if(!event.getAction().name().startsWith("RIGHT_CLICK")) return;
		if(event.getItem() == null) return;
		
		ItemStack item = event.getItem();
		HCFClassType activeClass = this.classManager.getActiveClass(player);
		
		if(activeClass == null) return;
		switch(activeClass) {
		case BARD:
			BardEffectType effect = BardEffectType.getByItem(item);
			if(effect == null) return;
			BardClass.bardEffect(player, effect, getNearbyTeammates(player, match));
			break;
		default:
			break;
		}
	}
	
	@EventHandler
	public void onPlayerItemHeld(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		
		if(match == null || match.isSpectating(player)) return;
		if(match.getLadder() != Ladder.HCT_NO_DEBUFF && match.getLadder() != Ladder.HCT_DEBUFF) return;
		
		ItemStack item = player.getItemInHand();
		HCFClassType activeClass = this.classManager.getActiveClass(player);
		BardEffectType effect = BardEffectType.getByItem(item);
		
		if(activeClass == null || activeClass != HCFClassType.BARD || effect == null) return;
		
		BardClass.bardHoldableEffect(player, effect, getNearbyTeammates(player, match));
		
		new BukkitRunnable() {

			@Override
			public void run() {
				if(match == null || match.isSpectating(player) || match.getLadder() != Ladder.HCT_NO_DEBUFF && match.getLadder() != Ladder.HCT_DEBUFF) {
					this.cancel();
					return;
				}

				BardEffectType newEffect = BardEffectType.getByItem(item);
				if(newEffect == null || newEffect != effect) {
					this.cancel();
					return;
				}
				
				Bukkit.getScheduler().runTaskAsynchronously(Practice.getInstance(), () -> BardClass.bardHoldableEffect(player, effect, getNearbyTeammates(player, match)));
			}
			
		}.runTaskTimerAsynchronously(Practice.getInstance(), 20L, 20L);
	}
	
	private List<Player> getNearbyTeammates(Player player, Match match) {
		List<Player> teammates = Lists.newArrayList();
		List<Entity> nearbyEntities = player.getWorld().getNearbyEntities(player.getLocation(), 25.0D, 25.0D, 25.0D)
				.stream()
				.filter(e -> e instanceof Player)
				.collect(Collectors.toList());
		
		for(Entity e : nearbyEntities) {
			Player p = (Player) e;
			Match targetMatch = Practice.getInstance().getMatchManager().getPlayerMatch(p);
			
			if(targetMatch != match || targetMatch.isSpectating(p)) continue;
			if(match.getPlayerTeam(p) != match.getPlayerTeam(player)) continue;
			
			teammates.add(p);
		}
		
		return teammates;
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(event.getEntity() != null && event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Match match = mm.getPlayerMatch(player);
			
			if(match == null || match.isSpectating(player)) return;
			if(match.getLadder() != Ladder.HCT_NO_DEBUFF && match.getLadder() != Ladder.HCT_DEBUFF) return;
			
			if(event.getDamager() != null && event.getDamager() instanceof Projectile) {
				Projectile projectile = (Projectile) event.getDamager();
				
				if(!(projectile instanceof Arrow)) return;
				
				if(projectile.getShooter() != null && projectile.getShooter() instanceof Player) {
					Player shooter = (Player) projectile.getShooter();
					Match targetMatch = mm.getPlayerMatch(shooter);
					
					if(targetMatch == null || targetMatch.isSpectating(shooter) || match != targetMatch) return;
					/*
					PvPClass activeClass = Practice.getInstance().getHcfClassManager().getActiveClass(shooter);
					
					if(activeClass != null && activeClass instanceof ArcherClass) {
						PvPClass otherClass = Practice.getInstance().getHcfClassManager().getActiveClass(player);
						if(otherClass != null && otherClass instanceof ArcherClass) return;
						
						setArcherMarked(player);
						shooter.sendMessage(ChatUtil.TRANSLATE("You have archer marked &b" + player.getName() + "&f! &7[&b" + PlayerUtil.getDistance(shooter, player) + " blocks&7]"));
					}
					*/
				}
			}
			
			if(isArcherMarked(player)) {
				double damage = event.getDamage() * 0.25D;
				double newDamage = event.getDamage() + damage;
				
				event.setDamage(newDamage);
			}
		}
	}
	
	@EventHandler
	public void onMatchReset(MatchResetEvent event) {
		Match match = event.getMatch();
		
		if(match.getLadder() != Ladder.HCT_NO_DEBUFF && match.getLadder() != Ladder.HCT_DEBUFF) return;
		
		for(Player p : match.getPlayers()) {
			archerMark.remove(p.getUniqueId());
		}
	}
	
	public static boolean isArcherMarked(Player player) {
		if(archerMark.containsKey(player.getUniqueId())) {
			return System.currentTimeMillis() < archerMark.get(player.getUniqueId());
		}
		
		return false;
	}
	
	public static void setArcherMarked(Player player) {
		long timeleft = archerMark.getOrDefault(player.getUniqueId(), System.currentTimeMillis());
		BukkitTask task = updateNametagTask.get(player.getUniqueId());
		
		if(task != null) task.cancel();
		
		if(System.currentTimeMillis() >= timeleft) {
			player.sendMessage(ChatUtil.TRANSLATE("You have been &cArcher Marked &ffor &b" + (ARCHER_MARK_MILLIS / 1000) + " &fseconds! You will receive &c+%25 &fmore damage."));
		}
		
		archerMark.put(player.getUniqueId(), System.currentTimeMillis() + ARCHER_MARK_MILLIS);
		Practice.getInstance().getNametagManager().updateNametag(player);
		
		updateNametagTask.put(player.getUniqueId(), new BukkitRunnable() {

			@Override
			public void run() {
				Practice.getInstance().getNametagManager().updateNametag(player);
			}
			
		}.runTaskLater(Practice.getInstance(), (ARCHER_MARK_MILLIS / 1000) * 20L));
	}
}
