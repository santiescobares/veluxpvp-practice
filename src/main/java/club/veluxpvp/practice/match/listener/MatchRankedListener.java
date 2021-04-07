package club.veluxpvp.practice.match.listener;

import java.text.SimpleDateFormat;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.elo.EloCalculator;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.match.MatchManager;
import club.veluxpvp.practice.match.MatchTeam;
import club.veluxpvp.practice.match.RankedMatchLog;
import club.veluxpvp.practice.match.event.MatchEndEvent;
import club.veluxpvp.practice.profile.Profile;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.TimeUtil;

public class MatchRankedListener implements Listener {

	private MatchManager mm;
	
	public MatchRankedListener() {
		this.mm = Practice.getInstance().getMatchManager();
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onMatchEnd(MatchEndEvent event) {
		Match match = event.getMatch();
		
		if(match.isRanked()) {
			Bukkit.getScheduler().runTaskLater(Practice.getInstance(), () -> {
				MatchTeam winnerTeam = null;
				
				if(match.getLadder() == Ladder.PARKOUR) {
					winnerTeam = MatchParkourListener.winnerTeam.get(match);
				} else if(match.getLadder() == Ladder.BRIDGES) {
					winnerTeam = MatchBridgesListener.teamScore.get(match.getTeam1()) == 3 ? match.getTeam1() : match.getTeam2();
				} else {
					winnerTeam = match.getLastAliveTeam();
				}
				
				MatchTeam loserTeam = winnerTeam == match.getTeam1() ? match.getTeam2() : match.getTeam1();
				
				UUID winnerUUID = winnerTeam.getFirstPlayer().getUniqueId();
				UUID loserUUID = loserTeam.getFirstPlayer().getUniqueId();
				Profile winnerProfile = Practice.getInstance().getProfileManager().getProfile(Bukkit.getOfflinePlayer(winnerUUID));
				Profile loserProfile = Practice.getInstance().getProfileManager().getProfile(Bukkit.getOfflinePlayer(loserUUID));
				
				String playedOn = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(TimeUtil.getDateInGMT3());
				String duration = TimeUtil.getFormattedDuration(match.getDuration(), true);
				
				int winnerTotalHits = match.getTotalHits().get(winnerUUID);
				int loserTotalHits = match.getTotalHits().get(loserUUID);
				int winnerLongestCombo = match.getLongestCombo().get(winnerUUID);
				int loserLongestCombo = match.getLongestCombo().get(loserUUID);
				int winnerHealingLeft = match.getPostMatchPlayers().get(winnerUUID).getHealingLeft();
				int loserHealingLeft = match.getPostMatchPlayers().get(loserUUID).getHealingLeft();
				int winnerMissedPots = match.getMissedPots().get(winnerUUID);
				int loserMissedPots = match.getMissedPots().get(loserUUID);
				
				// Updating elo
				final int winnerElo = winnerProfile.getElo(match.getLadder());
				final int loserElo = loserProfile.getElo(match.getLadder());
				final int eloUpdate = EloCalculator.calculate(winnerElo, loserElo);
				
				winnerProfile.addElo(match.getLadder(), eloUpdate);
				winnerProfile.setRankedWins(winnerProfile.getRankedWins() + 1);
				loserProfile.removeElo(match.getLadder(), eloUpdate);
				loserProfile.setRankedLoses(loserProfile.getRankedLoses() + 1);
				
				RankedMatchLog rml = new RankedMatchLog(match.getId(), winnerUUID, loserUUID, playedOn, match.getArena().getName(), duration, match.getLadder(), match.getHealingType(), winnerTotalHits, loserTotalHits, winnerLongestCombo, loserLongestCombo, winnerHealingLeft, loserHealingLeft, winnerMissedPots, loserMissedPots, winnerElo, loserElo, eloUpdate);
				mm.getRankedMatchesLogs().add(rml);
				
				String matchLink = mm.uploadAndGetPostMatchURL(rml);
				
				Bukkit.getScheduler().runTaskLater(Practice.getInstance(), () -> {
					for(Player p : match.getPlayers()) {
						p.sendMessage(ChatUtil.TRANSLATE("&bMatch Link&7: &f" + matchLink));
					}
				}, 60L);
			}, 20L);
		}
	}
}
