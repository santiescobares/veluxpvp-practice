package club.veluxpvp.practice.match;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Arena;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.item.ItemManager;
import club.veluxpvp.practice.match.event.MatchEndEvent;
import club.veluxpvp.practice.match.event.MatchPreStartEvent;
import club.veluxpvp.practice.match.event.MatchResetEvent;
import club.veluxpvp.practice.match.event.MatchStartEvent;
import club.veluxpvp.practice.match.listener.MatchBridgesListener;
import club.veluxpvp.practice.match.listener.MatchEnderPearlListener;
import club.veluxpvp.practice.match.listener.MatchHCTListener;
import club.veluxpvp.practice.match.listener.MatchKitListener;
import club.veluxpvp.practice.match.listener.MatchParkourListener;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.MatchUtil;
import club.veluxpvp.practice.utilities.PlayerUtil;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Match {

	private Practice plugin;
	
	private Arena arena;
	private Ladder ladder;
	private boolean ranked;
	private HealingType healingType;
	
	private String id;
	private MatchState state;
	private List<MatchTeam> teams;
	private List<Player> alivePlayers, spectators;
	private boolean ffa;
	private MatchEndReason endReason;
	
	private int duration, startingTime;
	private final int MAX_MATCH_TIME = 20 * 60; // 20 minutes
	
	private BukkitTask task, bridgesRoundTask;
	
	private Map<UUID, Integer> totalHits, currentCombo, longestCombo, kills, thrownPots, missedPots;
	private Map<UUID, PostMatchPlayer> postMatchPlayers;
	private List<UUID> playersWhoPlayedCache;
	private Location lastPvPLocation;
	
	private Map<UUID, Boolean> showingSpectators;
	private Map<UUID, UUID> swapInventoriesMap;
	
	// Ranked
	private int winnerElo, loserElo, eloUpdate;

	public Match(Arena arena, Ladder ladder, boolean ranked) {
		this.plugin = Practice.getInstance();
		
		this.arena = arena;
		this.ladder = ladder;
		this.ranked = ranked;
		this.healingType = ladder == Ladder.NO_DEBUFF || ladder == Ladder.DEBUFF || ladder == Ladder.HCF || ladder == Ladder.HCT_NO_DEBUFF || ladder == Ladder.HCT_DEBUFF ? HealingType.HEALTH_POTION : ladder == Ladder.BUILD_UHC || ladder == Ladder.FINAL_UHC ? HealingType.GOLDEN_APPLE : ladder == Ladder.GAPPLE || ladder == Ladder.COMBO_FLY ? HealingType.GAPPLE : ladder == Ladder.SOUP || ladder == Ladder.HG ? HealingType.SOUP : HealingType.NONE;
		
		this.id = UUID.randomUUID().toString().substring(0, 7);
		this.state = MatchState.NONE;
		this.teams = new ArrayList<>();
		
		this.alivePlayers = new ArrayList<>();
		this.spectators = new ArrayList<>();
		this.ffa = false;
		this.endReason = null;
		
		this.duration = 0;
		this.startingTime = 5;
		
		this.task = null;
		this.bridgesRoundTask = null;
		
		this.totalHits = Maps.newConcurrentMap();
		this.currentCombo = Maps.newConcurrentMap();
		this.longestCombo = Maps.newConcurrentMap();
		this.kills = Maps.newConcurrentMap();
		this.thrownPots = Maps.newConcurrentMap();
		this.missedPots = Maps.newConcurrentMap();
		this.postMatchPlayers = Maps.newConcurrentMap();
		this.playersWhoPlayedCache = Lists.newArrayList();
		this.lastPvPLocation = null;
		
		this.showingSpectators = Maps.newConcurrentMap();
		this.swapInventoriesMap = Maps.newHashMap();

		this.winnerElo = 0;
		this.loserElo = 0;
		this.eloUpdate = 0;
		
		this.teams.add(new MatchTeam(this, TeamType.TEAM_1));
		this.teams.add(new MatchTeam(this, TeamType.TEAM_2));
		
		if(ladder == Ladder.BUILD_UHC || ladder == Ladder.FINAL_UHC || ladder == Ladder.BRIDGES) arena.setInUse(true);
		
		plugin.getMatchManager().getMatches().add(this);
	}
	
	public void addSpectator(Player player) {
		this.spectators.add(player);
		this.getPlayers().stream().forEach(p -> p.sendMessage(ChatUtil.TRANSLATE("&b" + player.getName() + " &7is now spectating.")));
		this.plugin.getMatchManager().getPlayerLastMatch().put(player.getUniqueId(), this);
		this.showingSpectators.put(player.getUniqueId(), true);
		
		plugin.getNametagManager().updateNametag(player);
		
		player.getInventory().setHeldItemSlot(0);
		player.teleport(this.arena.getSpectatorsSpawn());
		player.setMaximumNoDamageTicks(19);
		
		PlayerUtil.reset(player, GameMode.CREATIVE, false);
		player.setFlying(true);
		ItemManager.loadSpectatorItems(player);
		PlayerUtil.updateVisibility(player);
		PlayerUtil.updateKnockback(player);
	}
	
	public void removeSpectator(Player player, boolean otherSpectate) {
		this.getPlayers().stream().forEach(p -> p.sendMessage(ChatUtil.TRANSLATE("&b" + player.getName() + " &7is no longer spectating.")));
		this.spectators.remove(player);
		
		plugin.getNametagManager().updateNametag(player);

		this.plugin.getMatchManager().getPlayerLastMatch().put(player.getUniqueId(), this);
		this.showingSpectators.remove(player.getUniqueId());
		
		player.setMaximumNoDamageTicks(20);
		PlayerUtil.updateVisibility(player);
		PlayerUtil.updateKnockback(player);
		
		MatchBridgesListener.goals.remove(player.getUniqueId());
		MatchEnderPearlListener.pearlCooldown.remove(player.getUniqueId());
		MatchHCTListener.archerMark.remove(player.getUniqueId());
		MatchKitListener.choosedKit.remove(player.getUniqueId());
		MatchParkourListener.lastCheckpoint.remove(player.getUniqueId());
		MatchParkourListener.reachedCheckpoints.remove(player.getUniqueId());
		
		if(!otherSpectate) {
			boolean disableFly = player.hasPermission("practice.flyonjoin");
			PlayerUtil.reset(player, GameMode.SURVIVAL, disableFly);
			PlayerUtil.sendToSpawn(player);
			
			if(this.plugin.getPartyManager().getPlayerParty(player) != null) {
				ItemManager.loadPartyItems(player);
			} else {
				ItemManager.loadLobbyItems(player);
			}
		}
	}
	
	public void setAsSpectator(Player player) {
		this.spectators.add(player);
		this.alivePlayers.remove(player);
		
		plugin.getNametagManager().updateNametag(player);
		player.setMaximumNoDamageTicks(20);
		
		PlayerUtil.updateVisibility(player);
		PlayerUtil.updateKnockback(player);
		
		PostMatchPlayer postMatchPlayer = new PostMatchPlayer(player, this.healingType, this.thrownPots.getOrDefault(player.getUniqueId(), 0), this.missedPots.getOrDefault(player.getUniqueId(), 0));
		postMatchPlayer.setHealth(0.0D);
		this.postMatchPlayers.put(player.getUniqueId(), postMatchPlayer);

		PlayerUtil.reset(player, GameMode.CREATIVE, false);
		player.setAllowFlight(true);
		player.setFlying(true);
	}
	
	public MatchTeam getTeam1() {
		return this.teams.stream().filter(t -> t.getType() == TeamType.TEAM_1).findFirst().orElse(new MatchTeam(this, TeamType.TEAM_1));
	}
	
	public MatchTeam getTeam2() {
		return this.teams.stream().filter(t -> t.getType() == TeamType.TEAM_2).findFirst().orElse(new MatchTeam(this, TeamType.TEAM_2));
	}
	
	public MatchTeam getPlayerTeam(Player player) {
		if(this.getTeam1().getPlayers().contains(player)) return this.getTeam1();
		if(this.getTeam2().getPlayers().contains(player)) return this.getTeam2();
		
		return null;
	}
	
	public List<Player> getAliveTeamMembers(MatchTeam team) {
		return team.getPlayers().stream().filter(p -> !this.isSpectating(p)).collect(Collectors.toList());
	}
	
	public List<Player> getPlayers() {
		List<Player> players = new ArrayList<>(this.alivePlayers);
		
		for(Player p : this.spectators) {
			players.add(p);
		}
		
		return players;
	}
	
	// Null means that twice teams are playing
	public MatchTeam getLastAliveTeam() {
		int aliveTeam1Players = 0;
		int aliveTeam2Players = 0;
		
		for(Player p : this.getTeam1().getPlayers()) {
			if(this.alivePlayers.contains(p)) aliveTeam1Players++;
		}
		
		for(Player p : this.getTeam2().getPlayers()) {
			if(this.alivePlayers.contains(p)) aliveTeam2Players++;
		}
		
		if(aliveTeam1Players == 0) return this.getTeam2();
		if(aliveTeam2Players == 0) return this.getTeam1();
		
		return null;
	}
	
	// Null means that the match didn't finish yet
	public Player getLastAlivePlayer() {
		if(this.getAlivePlayers().size() > 1) return null;
		
		return this.getAlivePlayers().get(0);
	}
	
	public boolean isParty() {
		return !this.ffa && this.getTeam1().getFIRST_TOTAL_MEMBERS() > 1 || this.getTeam2().getFIRST_TOTAL_MEMBERS() > 1;
	}
	
	public boolean isStarted() {
		return this.state == MatchState.PLAYING || this.state == MatchState.ENDING;
	}
	
	public boolean isSpectating(Player player) {
		return this.spectators.contains(player);
	}
	
	// GAME LOGIC
	
	public void startCountdown() {
		this.state = MatchState.STARTING;
		
		MatchPreStartEvent event = new MatchPreStartEvent(this);
		Bukkit.getPluginManager().callEvent(event);
		
		// Sorting players
		for(Player p : this.alivePlayers) {
			this.totalHits.put(p.getUniqueId(), 0);
			this.currentCombo.put(p.getUniqueId(), 0);
			this.longestCombo.put(p.getUniqueId(), 0);
			this.kills.put(p.getUniqueId(), 0);
			this.thrownPots.put(p.getUniqueId(), 0);
			this.missedPots.put(p.getUniqueId(), 0);
			this.showingSpectators.put(p.getUniqueId(), true);
			
			this.plugin.getMatchManager().getPlayerLastMatch().put(p.getUniqueId(), this);
			PlayerUtil.updateVisibility(p);
			PlayerUtil.updateKnockback(p);
			
			this.playersWhoPlayedCache.add(p.getUniqueId());
			if(this.getPlayerTeam(p) != null) continue;
			
			if(this.ffa) {
				this.getTeam1().getPlayers().add(p);
				continue;
			}
			
			if(this.getTeam1().getPlayers().size() == 0) {
				this.getTeam1().getPlayers().add(p);
				continue;
			}
			
			if(this.getTeam2().getPlayers().size() > this.getTeam1().getPlayers().size()) {
				this.getTeam1().getPlayers().add(p);
			} else {
				this.getTeam2().getPlayers().add(p);
			}
		}
		
		// Team 1 players logic
		for(Player p : this.getTeam1().getPlayers()) {
			PlayerUtil.reset(p, GameMode.SURVIVAL, true);
			
			p.teleport(this.arena.getCorner1());
			p.getInventory().setHeldItemSlot(0);
			
			p.sendMessage(" ");
			
			if(!this.ffa) {
				p.sendMessage(ChatUtil.TRANSLATE(" &7* &f" + (this.getTeam2().getPlayers().size() > 1 ? "Opponents" : "Opponent") + "&7: &b" + MatchUtil.getTeamPlayerNames(this.getTeam2().getPlayers(), "&b")));
			} else {
				List<Player> otherPlayers = new ArrayList<>(this.getTeam1().getPlayers());
				otherPlayers.remove(p);
				
				p.sendMessage(ChatUtil.TRANSLATE(" &7* &f" + (otherPlayers.size() > 1 ? "Opponents" : "Opponent") + "&7: &b" + MatchUtil.getTeamPlayerNames(otherPlayers, "&b")));
			}
			
			p.sendMessage(ChatUtil.TRANSLATE(" &7* &fArena&7: &b" + this.arena.getName()));
			if(this.ranked) p.sendMessage(ChatUtil.TRANSLATE(" &7* &fELO&7: &b" + this.plugin.getProfileManager().getProfile(this.getTeam2().getFirstPlayer()).getElo(this.ladder)));
			p.sendMessage(" ");
		}
		
		// Team 2 players logic
		for(Player p : this.getTeam2().getPlayers()) {
			PlayerUtil.reset(p, GameMode.SURVIVAL, true);

			p.teleport(this.arena.getCorner2());
			p.getInventory().setHeldItemSlot(0);
			
			p.sendMessage(" ");
			p.sendMessage(ChatUtil.TRANSLATE(" &7* &f" + (this.getTeam1().getPlayers().size() > 1 ? "Opponents" : "Opponent") + "&7: &b" + MatchUtil.getTeamPlayerNames(this.getTeam1().getPlayers(), "&b")));
			if(this.ranked) p.sendMessage(ChatUtil.TRANSLATE(" &7* &fELO&7: &b" + this.plugin.getProfileManager().getProfile(this.getTeam1().getFirstPlayer()).getElo(this.ladder)));
			p.sendMessage(ChatUtil.TRANSLATE(" &7* &fArena&7: &b" + this.arena.getName()));
			p.sendMessage(" ");
		}
		
		this.getTeam1().setFIRST_TOTAL_MEMBERS(this.getTeam1().getPlayers().size());
		this.getTeam2().setFIRST_TOTAL_MEMBERS(this.getTeam2().getPlayers().size());
		
		this.getPlayers().stream().forEach(p -> plugin.getNametagManager().updateNametag(p));
		this.getPlayers().stream().forEach(p -> PlayerUtil.updateVisibilityFlicker(p));
		this.getPlayers().stream().forEach(p -> p.setMaximumNoDamageTicks((this.ladder == Ladder.COMBO_FLY ? 3 : 20)));
		
		this.task = new BukkitRunnable() {

			@Override
			public void run() {
				if(startingTime > 1) {
					getPlayers().stream().forEach(p -> p.sendMessage(ChatUtil.TRANSLATE("Match starts in &b" + startingTime + " &fseconds.")));
					getPlayers().stream().forEach(p -> p.playSound(p.getLocation(), Sound.NOTE_PLING, 6F, 1.0F));
				} else if(startingTime == 1) {
					getPlayers().stream().forEach(p -> p.sendMessage(ChatUtil.TRANSLATE("Match starts in &b" + startingTime + " &fsecond.")));
					getPlayers().stream().forEach(p -> p.playSound(p.getLocation(), Sound.NOTE_PLING, 6F, 1.0F));
				} else {
					getPlayers().stream().forEach(p -> p.sendMessage(ChatUtil.TRANSLATE("&aThe match has started!")));
					getPlayers().stream().forEach(p -> p.playSound(p.getLocation(), Sound.NOTE_PLING, 6F, 2.4F));
					
					this.cancel();
					
					getPlayers().stream().forEach(p -> p.sendMessage(ChatUtil.TRANSLATE("&c&lWARNING: &7Butterfly clicking is highly discouraged and may result in a ban! Use it under your own risk.")));
					
					start();
					return;
				}
				
				startingTime--;
			}
			
		}.runTaskTimerAsynchronously(this.plugin, 20L, 20L);
	}
	
	public void start() {
		this.state = MatchState.PLAYING;
		
		MatchStartEvent event = new MatchStartEvent(this);
		Bukkit.getPluginManager().callEvent(event);
		
		this.task = new BukkitRunnable() {

			@Override
			public void run() {
				duration++;
				tryFinishGame(MatchEndReason.TIMEOUT);
			}
			
		}.runTaskTimerAsynchronously(this.plugin, 20L, 20L);
	}
	
	public void tryFinishGame(MatchEndReason endReason) {
		if(endReason == MatchEndReason.CANCELLED) {
			finish(endReason);
			return;
		}
		
		if(endReason == MatchEndReason.TIMEOUT) {
			if(this.duration == this.MAX_MATCH_TIME) {
				finish(endReason);
			}
			
			return;
		}
		
		if(endReason == MatchEndReason.ONE_TEAM_ALIVE) {
			MatchTeam lastTeam = this.getLastAliveTeam();
			
			if(lastTeam != null) {
				finish(endReason);
			}
			
			return;
		}
		
		if(endReason == MatchEndReason.ONE_PLAYER_ALIVE) {
			Player player = this.getLastAlivePlayer();
			
			if(player != null) {
				finish(endReason);
			}
			
			return;
		}
		
		if(endReason == MatchEndReason.PARKOUR_FINISHED) {
			if(this.ffa) {
				Player player = Bukkit.getPlayer(MatchParkourListener.winnerPlayer.get(this));
				
				if(player != null) {
					finish(endReason);
				}
			} else {
				MatchTeam team = MatchParkourListener.winnerTeam.get(this);
				
				if(team != null) {
					finish(endReason);
				}
			}
			
			return;
		}
		
		if(endReason == MatchEndReason.BRIDGES_TEAM_WINS) {
			int scoreTeam1 = MatchBridgesListener.teamScore.get(this.getTeam1());
			int scoreTeam2 = MatchBridgesListener.teamScore.get(this.getTeam2());
			
			if(scoreTeam1 == 3 || scoreTeam2 == 3) {
				finish(endReason);
			}
			
			return;
		}
	}
	
	public void finish(MatchEndReason endReason) {
		if(this.state == MatchState.ENDING) return;
		
		this.state = MatchState.ENDING;
		this.endReason = endReason;
		if(this.task != null) this.task.cancel();
		if(this.bridgesRoundTask != null) this.bridgesRoundTask.cancel();
		
		this.getPlayers().stream().forEach(p -> p.sendMessage(ChatUtil.TRANSLATE("&aThe match has ended!")));
		this.getPlayers().stream().forEach(p -> this.plugin.getMatchManager().getPlayerLastMatch().put(p.getUniqueId(), this));
		this.getAlivePlayers().stream().forEach(p -> this.postMatchPlayers.put(p.getUniqueId(), new PostMatchPlayer(p, this.healingType, this.thrownPots.getOrDefault(p.getUniqueId(), 0), this.missedPots.getOrDefault(p.getUniqueId(), 0))));
		
		for(Player p : this.getAlivePlayers()) {
			PlayerUtil.reset(p, p.getGameMode(), false);
		}
		
		MatchEndEvent matchEndEvent = new MatchEndEvent(this, endReason);
		Bukkit.getPluginManager().callEvent(matchEndEvent);
		
		if(endReason == MatchEndReason.CANCELLED) {
			reset(true);
			return;
		}
		
		Bukkit.getScheduler().runTaskLater(this.plugin, () -> reset(false), 5 * 20L);
	}
	
	public void reset(boolean cancelled) {
		MatchResetEvent event = new MatchResetEvent(this);
		Bukkit.getPluginManager().callEvent(event);
		
		this.getTeam1().getPlayers().clear();
		this.getTeam2().getPlayers().clear();
		
		for(Player p : this.getPlayers()) {
			plugin.getNametagManager().updateNametag(p);
			
			boolean fly = p.hasPermission("practice.flyonjoin");
			PlayerUtil.reset(p, GameMode.SURVIVAL, fly);
			PlayerUtil.sendToSpawn(p);
			PlayerUtil.updateVisibilityFlicker(p);
			p.setMaximumNoDamageTicks(20);
			if(fly) {
				p.setAllowFlight(true);
				p.setFlying(true);
			}
			
			if(this.plugin.getPartyManager().getPlayerParty(p) != null) {
				ItemManager.loadPartyItems(p);
			} else {
				ItemManager.loadLobbyItems(p);
			}
			
			MatchEnderPearlListener.pearlCooldown.remove(p.getUniqueId());
		}
		
		this.alivePlayers.clear();
		this.spectators.clear();
		this.playersWhoPlayedCache.clear();
		this.showingSpectators.clear();
		this.totalHits.clear();
		this.currentCombo.clear();
		this.longestCombo.clear();
		this.thrownPots.clear();
		this.missedPots.clear();
	}
}
