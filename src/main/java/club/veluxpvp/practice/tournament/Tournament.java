package club.veluxpvp.practice.tournament;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Arena;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.match.MatchEndReason;
import club.veluxpvp.practice.party.Party;
import club.veluxpvp.practice.party.PartyMember;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.TimeUtil;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

@Getter @Setter
public class Tournament {

	private Practice plugin;
	
	private List<Party> participants;
	private Ladder ladder;
	private TournamentState state;
	private int teamSize, teamsLimit, round, duration, startingTime;
	private Set<Match> liveMatches;
	private long startedAt, endedAt;
	private Map<Party, Integer> opponentsDefeated;
	
	private BukkitTask task;
	
	private int TOTAL_PARTICIPANTS;
	
	public Tournament(Ladder ladder, int teamSize, int teamsLimit) {
		this.plugin = Practice.getInstance();
		this.participants = Lists.newArrayList();
		this.ladder = ladder;
		this.state = TournamentState.WAITING;
		this.teamSize = teamSize;
		this.teamsLimit = teamsLimit;
		this.round = 1;
		this.duration = 0;
		this.startingTime = 30;
		this.startedAt = System.currentTimeMillis();
		this.endedAt = System.currentTimeMillis();
		this.liveMatches = Sets.newHashSet();
		this.opponentsDefeated = Maps.newConcurrentMap();
		
		this.task = null;
		
		this.TOTAL_PARTICIPANTS = 0;
		
		broadcastJoinMessage();
		
		new BukkitRunnable() {

			@Override
			public void run() {
				if(state != TournamentState.WAITING) {
					this.cancel();
					return;
				}
				
				broadcastJoinMessage();
			}
			
		}.runTaskTimerAsynchronously(plugin, 45 * 20L, 45 * 20L);
	}
	
	private void broadcastJoinMessage() {
		TextComponent message = new TextComponent(ChatUtil.TRANSLATE("&7A &b" + teamSize + "vs" + teamSize + " " + ladder.name + " &7tournament has started! Type &b/tournament join &7or "));
		TextComponent clickHere = new TextComponent(ChatUtil.TRANSLATE("&aClick here"));
		clickHere.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatUtil.TRANSLATE("&7* &fClick to &ajoin &fthe tournament!")).create()));
		clickHere.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tournament join"));
		message.addExtra(clickHere);
		message.addExtra(new TextComponent(ChatUtil.TRANSLATE(" &7to join!")));
		
		Bukkit.getOnlinePlayers().stream().forEach(p -> p.sendMessage(" "));
		Bukkit.getOnlinePlayers().stream().forEach(p -> p.spigot().sendMessage(message));
		Bukkit.getOnlinePlayers().stream().forEach(p -> p.sendMessage(" "));
	}
	
	public void addParticipant(Party party) {
		this.participants.add(party);
		
		Bukkit.getOnlinePlayers().stream()
		.filter(p -> this.plugin.getProfileManager().getProfile(p).isTournamentMessages())
		.forEach(p -> {
			Player leader = party.getLeader().getPlayer();
			
			TextComponent message = new TextComponent(ChatUtil.TRANSLATE("&b" + (this.teamSize == 1 ? leader.getName() : leader.getName() + "&7's team") + " &7has joined the &btournament&7! &f(" + this.participants.size() + "/" + this.teamsLimit + ")"));
			message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatUtil.TRANSLATE("&7* &fClick to &ajoin &fthe tournament!")).create()));
			message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tournament join"));
			
			p.spigot().sendMessage(message);
		});
		
		tryStart();
	}
	
	public void removeParticipant(Party party, boolean eliminated) {
		this.participants.remove(party);
		this.opponentsDefeated.remove(party);
		
		if(eliminated) {
			Bukkit.getOnlinePlayers().stream()
			.filter(p -> this.plugin.getProfileManager().getProfile(p).isTournamentMessages())
			.forEach(p -> {
				if(party != null) {
					Player leader = party.getLeader() != null ? party.getLeader().getPlayer() : null;
					
					if(leader != null) p.sendMessage(ChatUtil.TRANSLATE("&b" + (this.teamSize == 1 ? leader.getName() : leader.getName() + "&7's team") + " &7has been eliminated from the &btournament&7! &f(" + this.participants.size() + "/" + this.TOTAL_PARTICIPANTS + ")"));
				}
			});
		}
	}
	
	public boolean isInTournament(Party party) {
		return this.participants.contains(party);
	}
	
	public boolean isInTournament(Player player) {
		Party playerParty = this.plugin.getPartyManager().getPlayerParty(player);
		return playerParty != null && this.isInTournament(playerParty);
	}
	
	public boolean isStarted() {
		return this.state != TournamentState.WAITING && this.state != TournamentState.STARTING;
	}
	
	public boolean isFull() {
		return this.participants.size() == this.teamsLimit;
	}
	
	public void tryCancel() {
		if(this.participants.size() <= 1) {
			this.finish(true);
		}
	}
	
	// Tournament logic
	
	public void tryStart() {
		if(this.state == TournamentState.STARTING) return;
		
		if(this.participants.size() >= this.teamsLimit) {
			start();
		}
	}
	
	public void start() {
		if(this.state == TournamentState.STARTING) return;
		this.state = TournamentState.STARTING;
		this.startedAt = System.currentTimeMillis();
		
		this.task = new BukkitRunnable() {

			@Override
			public void run() {
				if(startingTime == 30 || startingTime == 15 || startingTime <= 5 && startingTime > 1) {
					Bukkit.broadcastMessage(ChatUtil.TRANSLATE("&7The &btournament &7will start in &b" + startingTime + " &7seconds!"));
				} else if(startingTime == 1) {
					Bukkit.broadcastMessage(ChatUtil.TRANSLATE("&7The &btournament &7will start in &b" + startingTime + " &7second!"));
				} else if(startingTime == 0) {
					this.cancel();
					finalStart();
					return;
				}
				
				startingTime--;
			}
			
		}.runTaskTimer(this.plugin, 0L, 20L);
	}
	
	public void finalStart() {
		if(this.state == TournamentState.PLAYING) return;
		this.state = TournamentState.PLAYING;
		this.TOTAL_PARTICIPANTS = this.participants.size();

		startRound();
		
		this.task = new BukkitRunnable() {

			@Override
			public void run() {
				duration++;
			}
			
		}.runTaskTimerAsynchronously(this.plugin, 20L, 20L);
	}
	
	public void startRoundCountdown() {
		if(this.state == TournamentState.STARTING_ROUND) return;
		this.state = TournamentState.STARTING_ROUND;
		this.startingTime = 30;
		this.round++;
		
		this.task = new BukkitRunnable() {

			@Override
			public void run() {
				if(startingTime == 30 || startingTime == 15 || startingTime <= 5 && startingTime > 1) {
					Bukkit.broadcastMessage(ChatUtil.TRANSLATE("&7Tournament's round &b&l" + round + " &7will start in &b" + startingTime + " &7seconds!"));
				} else if(startingTime == 1) {
					Bukkit.broadcastMessage(ChatUtil.TRANSLATE("&7Tournament's round &b&l" + round + " &7will start in &b" + startingTime + " &7second!"));
				} else if(startingTime == 0) {
					this.cancel();
					startRound();
					return;
				}
				
				startingTime--;
			}
			
		}.runTaskTimer(this.plugin, 0L, 20L);
	}
	
	public void startRound() {
		this.checkForPeopleSpectating();
		List<Party> fakeParticipants = Lists.newArrayList(this.participants);
		
		while(fakeParticipants.size() >= 2) {
			Party p1 = fakeParticipants.get(0);
			Party p2 = fakeParticipants.get(1);
			
			Arena arena = this.plugin.getArenaManager().getRandomTournamentArena(this.ladder);
			
			if(arena == null) {
				this.finish(true);
				Bukkit.broadcastMessage(ChatUtil.TRANSLATE("&cThe tournament was cancelled due insufficient arenas!"));
				return;
			}
			
			Match match = new Match(arena, this.ladder, false);
			
			p1.getMembers().stream().map(PartyMember::getPlayer).forEach(p -> {
				match.getAlivePlayers().add(p);
				match.getTeam1().getPlayers().add(p);
			});
			
			p2.getMembers().stream().map(PartyMember::getPlayer).forEach(p -> {
				match.getAlivePlayers().add(p);
				match.getTeam2().getPlayers().add(p);
			});
			
			match.startCountdown();
			this.liveMatches.add(match);
			
			fakeParticipants.remove(0);
			fakeParticipants.remove(0);
		}
		
		if(fakeParticipants.size() == 1) {
			Party luckyTeam = fakeParticipants.get(0);
			
			luckyTeam.getMembers().stream().forEach(p -> p.getPlayer().sendMessage(ChatUtil.TRANSLATE("&cYour team has automatically advanced to the following round due to the irregularly teams size!")));
			
			fakeParticipants.remove(0);
		}
		
		Bukkit.broadcastMessage(ChatUtil.TRANSLATE("&7Tournament's round &b&l" + this.round + " &7has started! &aGood luck!"));
		Bukkit.broadcastMessage(ChatUtil.TRANSLATE("&7Type &b/tournament status &7to see who is fighting."));
		this.state = TournamentState.PLAYING;
	}
	
	public void tryFinishRound() {
		if(this.state == TournamentState.STARTING_ROUND) return;
		
		if(this.liveMatches.size() == 0) {
			if(this.task != null) this.task.cancel();
			tryFinishTournament();
		}
	}
	
	public void tryFinishTournament() {
		if(this.participants.size() == 1) {
			finish(false);
			return;
		}
		
		startRoundCountdown();
	}
	
	public void finish(boolean cancelled) {
		if(this.state == TournamentState.ENDED) return;
		this.state = TournamentState.ENDED;
		this.duration = 0;
		if(this.task != null) this.task.cancel();
		this.endedAt = System.currentTimeMillis();
		
		if(!cancelled) {
			Party winnerTeam = this.participants.get(0);
			
			Bukkit.broadcastMessage(" ");
			Bukkit.broadcastMessage(ChatUtil.TRANSLATE("&b&lTournament Ended"));
			Bukkit.broadcastMessage(" ");
			Bukkit.broadcastMessage(ChatUtil.TRANSLATE(" &7* &fWinner&7: &a" + (this.teamSize == 1 ? winnerTeam.getLeader().getPlayer().getName() : winnerTeam.getLeader().getPlayer().getName() + "'s Team")));
			Bukkit.broadcastMessage(ChatUtil.TRANSLATE(" &7* &fOpponents Defeated&7: &b" + this.opponentsDefeated.getOrDefault(winnerTeam, 0)));
			Bukkit.broadcastMessage(ChatUtil.TRANSLATE(" &7* &fRounds&7: &b" + this.round));
			
			int duration = (int) ((this.endedAt - this.startedAt) / 1000);
			
			Bukkit.broadcastMessage(ChatUtil.TRANSLATE(" &7* &fDuration&7: &b" + TimeUtil.getFormattedDuration(duration, true)));
			Bukkit.broadcastMessage(" ");
		} else {
			try {
				this.liveMatches.stream().forEach(m -> m.finish(MatchEndReason.CANCELLED));
			} catch(ConcurrentModificationException ignored) {}
			
			Bukkit.broadcastMessage(" ");
			Bukkit.broadcastMessage(ChatUtil.TRANSLATE("&c&lThe tournament has been cancelled!"));
			Bukkit.broadcastMessage(" ");
		}
		
		this.participants.clear();
		this.liveMatches.clear();
		this.opponentsDefeated.clear();
		this.plugin.getTournamentManager().setActiveTournament(null);
	}
	
	private void checkForPeopleSpectating() {
		for(Party p : this.participants) {
			for(PartyMember pm : p.getMembers()) {
				Player player = pm.getPlayer();
				Match match = Practice.getInstance().getMatchManager().getPlayerMatch(player);
				
				if(match != null && match.isSpectating(player)) {
					match.removeSpectator(player, false);
				}
			}
		}
	}
}
