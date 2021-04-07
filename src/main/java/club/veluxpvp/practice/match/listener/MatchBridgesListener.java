package club.veluxpvp.practice.match.listener;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.Maps;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Claim;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.kit.Kit;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.match.MatchEndReason;
import club.veluxpvp.practice.match.MatchManager;
import club.veluxpvp.practice.match.MatchState;
import club.veluxpvp.practice.match.MatchTeam;
import club.veluxpvp.practice.match.TeamType;
import club.veluxpvp.practice.match.event.MatchResetEvent;
import club.veluxpvp.practice.match.event.MatchStartEvent;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.PlayerUtil;

public class MatchBridgesListener implements Listener {

	private MatchManager mm;
	public static Map<UUID, Integer> goals = Maps.newConcurrentMap();
	public static Map<Match, Integer> round = Maps.newConcurrentMap();
	public static Map<Match, Boolean> startingRound = Maps.newConcurrentMap();
	public static Map<MatchTeam, Integer> teamScore = Maps.newConcurrentMap();
	public static Map<Match, BukkitTask> roundTask = Maps.newConcurrentMap();
	public static Map<UUID, Long> arrowCooldown = Maps.newConcurrentMap();
	public static Map<UUID, BukkitTask> arrowCooldownTask = Maps.newConcurrentMap();
	private final long ARROW_COOLDOWN = TimeUnit.SECONDS.toMillis(7);
	
	public MatchBridgesListener() {
		this.mm = Practice.getInstance().getMatchManager();
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler
	public void onMatchStart(MatchStartEvent event) {
		Match match = event.getMatch();
		
		if(match.getLadder() == Ladder.BRIDGES) {
			round.putIfAbsent(match, 1);
			startingRound.putIfAbsent(match, false);
			
			for(Player p : match.getAlivePlayers()) {
				goals.putIfAbsent(p.getUniqueId(), 0);
			}
			
			teamScore.putIfAbsent(match.getTeam1(), 0);
			teamScore.putIfAbsent(match.getTeam2(), 0);
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		
		if(match == null || match.isSpectating(player) || match.getLadder() != Ladder.BRIDGES) return;
		
		Location from = event.getFrom();
		Location to = event.getTo();
		
		Claim claimTo = match.getArena().getClaimAt(to);
		
		if(!match.isStarted() || isStartingBridgesRound(match)) {
			if(to.getBlockX() != from.getBlockX() || to.getBlockZ() != from.getBlockZ()) {
				player.teleport(from);
			}
		} else {
			if(match.getState() == MatchState.ENDING) return;
			
			Block blockAtTo = to.getBlock();
			
			if(blockAtTo == null || blockAtTo.getType() == Material.AIR) return;
			if(blockAtTo.getType() == Material.WATER || blockAtTo.getType() == Material.STATIONARY_WATER) {
				MatchTeam playerTeam = match.getPlayerTeam(player);
				
				// Fell into their own score
				if(claimTo.getOwnerTeam() == playerTeam.getType()) {
					List<ItemStack> drops = new ArrayList<>();
					for(ItemStack i : player.getInventory().getContents()) {
						if(i != null && i.getType() != Material.AIR) drops.add(i);
					}
					
					for(ItemStack i : player.getInventory().getArmorContents()) {
						if(i != null && i.getType() != Material.AIR) drops.add(i);
					}
					
					PlayerDeathEvent playerDeathEvent = new PlayerDeathEvent(player, drops, 0, null);
					Bukkit.getPluginManager().callEvent(playerDeathEvent);
					return;
				}
				
				int score = teamScore.get(playerTeam);
				
				teamScore.put(playerTeam, score + 1);
				goals.put(player.getUniqueId(), getGoals(player, match) + 1);
				match.getPlayers().stream().forEach(p -> p.sendMessage(ChatUtil.TRANSLATE(getBridgesTeamColor(match, playerTeam) + " Team &7scored! &a(" + (score + 1) + "/3)")));
				
				player.teleport((playerTeam.getType() == TeamType.TEAM_1 ? match.getArena().getCorner1() : match.getArena().getCorner2()));
				
				match.tryFinishGame(MatchEndReason.BRIDGES_TEAM_WINS);
				
				if(match.getState() != MatchState.ENDING) {
					this.startNewRound(match);
					updateArrowCooldowns(match);
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.getEntity() != null && event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Match match = mm.getPlayerMatch(player);
			
			if(match == null || match.isSpectating(player) || match.getLadder() != Ladder.BRIDGES) return;
			
			if(event.getCause() == DamageCause.FALL) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		
		if(match == null || match.isSpectating(player) || match.getLadder() != Ladder.BRIDGES) return;
		
		final Block block = event.getBlock();
		
		if(block == null || block.getType() == Material.AIR) return;
		if(!block.hasMetadata("PlacedOnMatch")) {
			event.setCancelled(true);
			return;
		}
		
		event.setExpToDrop(0);
		Bukkit.getScheduler().runTaskLater(Practice.getInstance(), () -> block.getDrops().clear(), 100L);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		
		if(match == null || match.isSpectating(player) || match.getLadder() != Ladder.BRIDGES) return;
		
		if(match.getArena().getClaimAt(event.getBlock().getLocation()) != null) {
			event.setCancelled(true);
			player.sendMessage(ChatUtil.TRANSLATE("&cYou can't place blocks here!"));
			return;
		}
		
		if(event.getBlock().getLocation().getBlockY() > match.getArena().getMaxBuildHeight()) {
			event.setCancelled(true);
			player.sendMessage(ChatUtil.TRANSLATE("&cYou can't build above y=" + match.getArena().getMaxBuildHeight() + "."));
			return;
		}
		
		event.getBlock().setMetadata("PlacedOnMatch", new FixedMetadataValue(Practice.getInstance(), true));
	}
	
	@EventHandler
	public void onBlockFromTo(BlockFromToEvent event) {
		event.getToBlock().setMetadata("PlacedOnMatch", new FixedMetadataValue(Practice.getInstance(), true));
		event.getBlock().setMetadata("PlacedOnMatch", new FixedMetadataValue(Practice.getInstance(), true));
	}
	
	@EventHandler
	public void onBlockForm(BlockFormEvent event) {
		event.getBlock().setMetadata("PlacedOnMatch", new FixedMetadataValue(Practice.getInstance(), true));
	}
	
	@EventHandler
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		
		if(match == null || match.isSpectating(player) || match.getLadder() != Ladder.BRIDGES) return;
		
		if(event.getBlockClicked().getLocation().getBlockY() > match.getArena().getMaxBuildHeight()) {
			event.setCancelled(true);
			player.sendMessage(ChatUtil.TRANSLATE("&cYou can't build above y=" + match.getArena().getMaxBuildHeight() + "."));
			return;
		}
	}
	
	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		
		if(match == null || match.isSpectating(player) || match.getLadder() != Ladder.BRIDGES) return;
		
		ItemStack item = event.getItem();
		
		if(item == null || item.getType() == Material.AIR) return;
		if(item.getType() == Material.GOLDEN_APPLE) {
			player.removePotionEffect(PotionEffectType.REGENERATION);
			player.setHealth(20);
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		
		if(match == null || match.isSpectating(player) || match.getLadder() != Ladder.BRIDGES) return;
		if(match.getState() != MatchState.STARTING && !isStartingBridgesRound(match)) return;
		
		if(!event.getAction().name().startsWith("RIGHT_CLICK")) return;
		if(event.getItem() == null) return;
		
		ItemStack item = event.getItem();
		
		if(item.getType() == Material.GOLDEN_APPLE) return;
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		if(event.getEntity() != null && event.getEntity() instanceof Arrow) {
			Arrow arrow = (Arrow) event.getEntity();
			
			if(arrow.getShooter() != null && arrow.getShooter() instanceof Player) {
				Player player = (Player) arrow.getShooter();
				Match match = mm.getPlayerMatch(player);
				
				if(match == null || match.isSpectating(player) || match.getLadder() != Ladder.BRIDGES) return;
				
				arrowCooldown.put(player.getUniqueId(), System.currentTimeMillis() + this.ARROW_COOLDOWN);
		        arrowCooldownTask.put(player.getUniqueId(), new BukkitRunnable() {

		            public void run() {
		                long cooldownExpires = arrowCooldown.getOrDefault(player.getUniqueId(), 0L);

		                if(cooldownExpires < System.currentTimeMillis()) {
		                	Match currentMatch = mm.getPlayerMatch(player);
							
							if(match != currentMatch || isStartingBridgesRound(currentMatch) || currentMatch.getState() == MatchState.ENDING) {
								this.cancel();
								player.setExp(0.0F);
								return;
							}
							
							player.getInventory().addItem(new ItemStack(Material.ARROW));
		                    this.cancel();
		                    return;
		                }

		                int millisLeft = (int) (cooldownExpires - System.currentTimeMillis());
		                float percentLeft = (float) millisLeft / ARROW_COOLDOWN;

		                player.setExp(percentLeft);
		                player.setLevel(millisLeft / 1_000);
		            }

		        }.runTaskTimer(Practice.getInstance(), 1L, 1L));
			}
		}
	}
	
	@EventHandler
	public void onMatchReset(MatchResetEvent event) {
		Match match = event.getMatch();
		
		if(match.getLadder() == Ladder.BRIDGES) {
			for(Player p : match.getPlayers()) {
				goals.remove(p.getUniqueId());
			}
			
			round.remove(match);
			teamScore.remove(match.getTeam1());
			teamScore.remove(match.getTeam2());
			startingRound.remove(match);
			
			BukkitTask task = roundTask.get(match);
			if(task != null) task.cancel();
			roundTask.remove(match);
			
			boolean delaySetInUse = false;
			List<Block> placedBlocks = match.getArena().getBounds().getBlocks().stream().filter(b -> b != null && b.hasMetadata("PlacedOnMatch")).collect(Collectors.toList());
			
			if(placedBlocks.size() > 0) {
				delaySetInUse = true;
				
				if(match.getEndReason() == MatchEndReason.CANCELLED) {
					placedBlocks.stream().forEach(b -> b.setType(Material.AIR));
					return;
				} else {
					new BukkitRunnable() {

						@Override
						public void run() {
							if(placedBlocks.size() == 0) this.cancel();
							
							try {
								Block b1 = placedBlocks.get(new Random().nextInt(placedBlocks.size()));
								Block b2 = placedBlocks.get(new Random().nextInt(placedBlocks.size()));
								Block b3 = placedBlocks.get(new Random().nextInt(placedBlocks.size()));
								Block b4 = placedBlocks.get(new Random().nextInt(placedBlocks.size()));
								
								b1.removeMetadata("PlacedOnMatch", Practice.getInstance());
								b2.removeMetadata("PlacedOnMatch", Practice.getInstance());
								b3.removeMetadata("PlacedOnMatch", Practice.getInstance());
								b4.removeMetadata("PlacedOnMatch", Practice.getInstance());
								
								b1.setType(Material.AIR);
								b2.setType(Material.AIR);
								b3.setType(Material.AIR);
								b4.setType(Material.AIR);
								
								try {
									placedBlocks.remove(b1);
									placedBlocks.remove(b2);
									placedBlocks.remove(b3);
									placedBlocks.remove(b4);
								} catch(ConcurrentModificationException ignored) {}
							} catch(IllegalArgumentException ignored) {}
						}
						
					}.runTaskTimer(Practice.getInstance(), 1L, 1L);
				}
			}
			
			if(delaySetInUse) {
				Bukkit.getScheduler().runTaskLater(Practice.getInstance(), () -> match.getArena().setInUse(false), 50L);
			} else {
				match.getArena().setInUse(false);
			}
		}
	}
	
	public static int getGoals(Player player, Match match) {
		if(goals.containsKey(player.getUniqueId())) {
			return goals.get(player.getUniqueId());
		}
		
		return 0;
	}
	
	public static int getTeamScore(Match match, MatchTeam team) {
		for(Map.Entry<MatchTeam, Integer> scoresEntry : teamScore.entrySet()) {
			if(scoresEntry.getKey() == team && scoresEntry.getKey().getMatch() == match) {
				return scoresEntry.getValue();
			}
		}
		
		return 0;
	}
	
	public static boolean isStartingBridgesRound(Match match) {
		return startingRound.getOrDefault(match, false);
	}
	
	public static void updateArrowCooldowns(Match match) {
		MatchManager mm = Practice.getInstance().getMatchManager();
		
		for(UUID uuid : arrowCooldownTask.keySet()) {
			Player p = Bukkit.getPlayer(uuid);
			
			if(p == null || mm.getPlayerMatch(p) != match) continue;
			
			BukkitTask task = arrowCooldownTask.get(uuid);
			if(task != null) {
				task.cancel();
				p.setExp(0.0F);
			}
		}
	}
	
	public static String getBridgesTeamColor(Match match, MatchTeam team) {
		if(team == match.getTeam1()) {
			return ChatColor.RED + "Red";
		} else {
			return ChatColor.BLUE + "Blue";
		}
	}
	
	public void startNewRound(Match match) {
		round.put(match, round.get(match) + 1);
		startingRound.put(match, true);
		
		updateArrowCooldowns(match);
		
		for(Player p : match.getTeam1().getPlayers()) {
			PlayerUtil.reset(p, GameMode.SURVIVAL, true);
			
			Kit choosedKit = MatchKitListener.choosedKit.get(p.getUniqueId());
			if(choosedKit != null) choosedKit.apply(p, true);
			
			p.getInventory().setHeldItemSlot(0);
			p.setHealth(20);
			p.teleport(match.getArena().getCorner1());
		}
		
		for(Player p : match.getTeam2().getPlayers()) {
			PlayerUtil.reset(p, GameMode.SURVIVAL, true);
			
			Kit choosedKit = MatchKitListener.choosedKit.get(p.getUniqueId());
			if(choosedKit != null) choosedKit.apply(p, true);
			
			p.getInventory().setHeldItemSlot(0);
			p.setHealth(20);
			p.teleport(match.getArena().getCorner2());
		}
		
		roundTask.put(match, new BukkitRunnable() {
			int startingTime = 5;
			
			@Override
			public void run() {
				int Round = round.get(match);
				
				if(startingTime > 1) {
					match.getPlayers().stream().forEach(p -> p.sendMessage(ChatUtil.TRANSLATE("Round &b#" + Round + " &fstarts in &b" + startingTime + " &fseconds.")));
					match.getPlayers().stream().forEach(p -> p.playSound(p.getLocation(), Sound.NOTE_PLING, 6F, 1.0F));
				} else if(startingTime == 1) {
					match.getPlayers().stream().forEach(p -> p.sendMessage(ChatUtil.TRANSLATE("Round &b#" + Round + " &fstarts in &b" + startingTime + " &fsecond.")));
					match.getPlayers().stream().forEach(p -> p.playSound(p.getLocation(), Sound.NOTE_PLING, 6F, 1.0F));
				} else {
					match.getPlayers().stream().forEach(p -> p.sendMessage(ChatUtil.TRANSLATE("&aRound #" + Round + " started!")));
					match.getPlayers().stream().forEach(p -> p.playSound(p.getLocation(), Sound.NOTE_PLING, 6F, 2.4F));
					
					this.cancel();
					
					startingRound.put(match, false);
					return;
				}
				
				startingTime--;
			}
			
		}.runTaskTimerAsynchronously(Practice.getInstance(), 20L, 20L));
	}
}
