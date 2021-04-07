package club.veluxpvp.practice.event.sumo;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Arena;
import club.veluxpvp.practice.event.EventState;
import club.veluxpvp.practice.event.EventType;
import club.veluxpvp.practice.event.PracticeEvent;
import club.veluxpvp.practice.item.ItemManager;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.MatchUtil;
import club.veluxpvp.practice.utilities.PlayerUtil;
import club.veluxpvp.practice.utilities.TimeUtil;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SumoEvent extends PracticeEvent {

	private int teamSize;
	private List<SumoTeam> teams;
	
	private int startingTime;
	private int round, roundStartingTime, roundDuration;
	private boolean roundStarted;
	private BukkitTask roundTask;
	
	private Map<UUID, Integer> totalHits;
	private List<SumoTeam> playingTeams;
	
	public SumoEvent(EventType type, Arena arena, int slots, int teamSize) {
		super(type, arena, slots);
		
		this.teamSize = teamSize;
		this.teams = Lists.newArrayList();
		
		this.startingTime = 30;
		this.round = 0;
		this.roundStartingTime = 3;
		this.roundDuration = 0;
		this.roundTask = null;
		this.roundStarted = false;
		
		this.totalHits = Maps.newConcurrentMap();
		this.playingTeams = Lists.newArrayList();
		
		arena.setInUse(true);
	}
	
	public void createTeam(Player player) {
		SumoTeam team = new SumoTeam();
		team.getPlayers().add(player);
		this.teams.add(team);
	}
	
	public void deleteTeam(Player player) {
		SumoTeam team = this.getPlayerTeam(player);
		
		if(team != null) {
			team.getPlayers().clear();
			this.teams.remove(team);
		}
	}
	
	public SumoTeam getPlayerTeam(Player player) {
		return this.teams.stream().filter(t -> t.getPlayers().contains(player)).findFirst().orElse(null);
	}
	
	public List<SumoTeam> getAliveTeams() {
		return this.teams.stream().filter(t -> !t.isEliminated()).collect(Collectors.toList());
	}
	
	public boolean isPlayingRound(Player player) {
		SumoTeam team = this.getPlayerTeam(player);
		return team != null && this.playingTeams.contains(team);
	}
	
	@Override
	public void startCountdown() {
		this.state = EventState.STARTING;
		this.task = new BukkitRunnable() {

			@Override
			public void run() {
				if(startingTime == 30 || startingTime == 15 || startingTime <= 5 && startingTime > 1) {
					messageAll("&7The &bevent &7will start in &b" + startingTime + " &7seconds.");
				} else if(startingTime == 1) {
					messageAll("&7The &bevent &7will start in &b" + startingTime + " &7second.");
				} else if(startingTime == 0) {
					this.cancel();
					start();
					return;
				}
				
				startingTime--;
			}
			
		}.runTaskTimer(Practice.getInstance(), 0L, 20L);
	}
	
	@Override
	public void start() {
		this.state = EventState.PLAYING;
		this.startedAt = System.currentTimeMillis();
		
		startNewRound();
	}
	
	public void startNewRound() {
		this.round++;
		this.roundStartingTime = 3;
		this.roundStarted = false;
		
		// Sorting teams in the first round
		if(this.round == 1) {
			List<SumoTeam> readyTeams = this.teams.stream().filter(t -> t.getPlayers().size() == this.teamSize).collect(Collectors.toList());
			List<SumoTeam> unreadyTeams = this.teams.stream().filter(t -> !readyTeams.contains(t)).collect(Collectors.toList());
			List<Player> alonePlayers = this.aliveParticipants.stream().filter(p -> this.getPlayerTeam(p) == null).collect(Collectors.toList());
			
			int teamIndex = -1;
			while(unreadyTeams.size() > 0) {
				if((teamIndex + 1) == unreadyTeams.size()) break;
				
				for(SumoTeam t : unreadyTeams) {
					teamIndex++;
					
					if(unreadyTeams.size() >= (teamIndex + 1)) {
						SumoTeam otherTeam = unreadyTeams.get(teamIndex + 1);
						
						for(Player otherPlayer : otherTeam.getPlayers()) {
							if(t.getPlayers().size() == this.teamSize) break;
							
							t.getPlayers().add(otherPlayer);
							otherTeam.getPlayers().remove(0);
						}
						
						if(otherTeam.getPlayers().size() == 0) unreadyTeams.remove(teamIndex + 1);
					}
				}
			}
			
			int playerIndex = -1;
			while(alonePlayers.size() > 0) {
				if((playerIndex + 1) == alonePlayers.size()) break;
				playerIndex++;
				
				Player player = alonePlayers.get(playerIndex);
				SumoTeam avaiableTeam = unreadyTeams.stream().filter(t -> t.getPlayers().size() < this.teamSize).findFirst().orElse(null);
				
				if(avaiableTeam != null) {
					avaiableTeam.getPlayers().add(player);
					alonePlayers.remove(playerIndex);
					continue;
				}
				
				SumoTeam newTeam = new SumoTeam();
				newTeam.getPlayers().add(player);
				this.teams.add(newTeam);
			}
		}
		
		// Check for enough teams
		if(this.teams.size() < 2) {
			finish(true); // change for tryFinish()
			return;
		}
		
		List<SumoTeam> fakeTeams = Lists.newArrayList(this.getAliveTeams());
		int random1 = new Random().nextInt(fakeTeams.size());
		SumoTeam team1 = fakeTeams.get(random1);
		fakeTeams.remove(random1);
		this.playingTeams.add(team1);
		
		int random2 = new Random().nextInt(fakeTeams.size());
		SumoTeam team2 = fakeTeams.get(random2);
		fakeTeams.remove(random2);
		this.playingTeams.add(team2);
		
		// Start logic
		this.messageAll(" ");
		this.messageAll("&b&lRound " + this.round);
		this.messageAll(" &7* &c" + MatchUtil.getTeamPlayerNames(team1.getPlayers(), "&c") + " &fvs &9" + MatchUtil.getTeamPlayerNames(team2.getPlayers(), "&9"));
		this.messageAll(" ");
		
		for(Player p : team1.getPlayers()) {
			PlayerUtil.reset(p, GameMode.SURVIVAL, true);
			p.teleport(this.arena.getCorner1());
			Practice.getInstance().getNametagManager().updateNametag(p);
		}
		
		for(Player p : team2.getPlayers()) {
			PlayerUtil.reset(p, GameMode.SURVIVAL, true);
			p.teleport(this.arena.getCorner2());
			Practice.getInstance().getNametagManager().updateNametag(p);
		}
		
		this.task = new BukkitRunnable() {

			@Override
			public void run() {
				if(roundStartingTime > 1) {
					messageAll("Round starts in &b" + roundStartingTime + " &fseconds.");
					getPlayers().stream().forEach(p -> p.playSound(p.getLocation(), Sound.NOTE_PLING, 6F, 1.0F));
				} else if(roundStartingTime == 1) {
					messageAll("Round starts in &b" + roundStartingTime + " &fsecond.");
					getPlayers().stream().forEach(p -> p.playSound(p.getLocation(), Sound.NOTE_PLING, 6F, 1.0F));
				} else if(roundStartingTime == 0) {
					this.cancel();
					
					messageAll("&aRound started!");
					getPlayers().stream().forEach(p -> p.playSound(p.getLocation(), Sound.NOTE_PLING, 6F, 2.4F));
					roundStarted = true;
					roundDuration = 0;
					
					roundTask = new BukkitRunnable() {

						@Override
						public void run() {
							roundDuration++;
						}
						
					}.runTaskTimerAsynchronously(Practice.getInstance(), 20L, 20L);
				}
				
				roundStartingTime--;
			}
			
		}.runTaskTimerAsynchronously(Practice.getInstance(), 20L, 20L);
	}
	
	public void finishRound(SumoTeam winnerTeam) {
		this.roundTask.cancel();
		this.playingTeams.clear();
		
		winnerTeam.setOpponentsDefeated(winnerTeam.getOpponentsDefeated() + 1);
		
		SumoTeam loserTeam = this.playingTeams.get(0) == winnerTeam ? this.playingTeams.get(1) : this.playingTeams.get(0);
		loserTeam.setEliminated(true);
		
		this.messageAll("&b" + MatchUtil.getTeamPlayerNames(winnerTeam.getPlayers(), "&b") + " &7has won the round!");
		
		for(Player p : winnerTeam.getPlayers()) {
			PlayerUtil.reset(p, GameMode.SURVIVAL, true);
			p.teleport(this.arena.getEventsSpawn());
			Practice.getInstance().getNametagManager().updateNametag(p);
		}
		
		for(Player p : loserTeam.getPlayers()) {
			this.setAsSpectator(p);
		}
		
		// Finish the tournament
		if(this.getAliveTeams().size() == 1) {
			finish(false);
			return;
		}
		
		this.startNewRound();
	}
	
	@Override
	public void finish(boolean cancelled) {
		if(this.state == EventState.ENDING) return;
		
		this.state = EventState.ENDING;
		this.endedAt = System.currentTimeMillis();
		if(this.task != null) this.task.cancel();
		if(this.roundTask != null) this.roundTask.cancel();
		
		if(!cancelled) {
			SumoTeam winnerTeam = this.getAliveTeams().get(0);
			
			Bukkit.broadcastMessage(" ");
			Bukkit.broadcastMessage(ChatUtil.TRANSLATE("&b&lSumo Event Ended"));
			Bukkit.broadcastMessage(" ");
			Bukkit.broadcastMessage(ChatUtil.TRANSLATE(" &7* &f" + (winnerTeam.getPlayers().size() == 1 ? "Winner" : "Winners") + "&7: &a" + MatchUtil.getTeamPlayerNames(winnerTeam.getPlayers(), "&a")));
			Bukkit.broadcastMessage(ChatUtil.TRANSLATE(" &7* &fOpponents Defeated&7: &b" + winnerTeam.getOpponentsDefeated()));
			Bukkit.broadcastMessage(ChatUtil.TRANSLATE(" &7* &fRounds&7: &b" + this.round));
			
			int duration = (int) (this.endedAt - this.startedAt);
			
			Bukkit.broadcastMessage(ChatUtil.TRANSLATE(" &7* &fDuration&7: &b" + TimeUtil.getFormattedDuration(duration, true)));
			Bukkit.broadcastMessage(" ");
		} else {
			Bukkit.broadcastMessage(" ");
			Bukkit.broadcastMessage(ChatUtil.TRANSLATE("&c&lThe event has been cancelled!"));
			Bukkit.broadcastMessage(" ");
		}
		
		for(Player p : this.getPlayers()) {
			PlayerUtil.reset(p, GameMode.SURVIVAL, false);
			PlayerUtil.sendToSpawn(p);
			ItemManager.loadLobbyItems(p);
			PlayerUtil.updateVisibility(p);
			Practice.getInstance().getNametagManager().updateNametag(p);
		}
		
		this.aliveParticipants.clear();
		this.spectators.clear();
		this.playingTeams.clear();
		this.teams.clear();
		this.playersWhoPlayedCache.clear();
		
		this.totalHits.clear();
		
		this.arena.setInUse(false);
	}
}
