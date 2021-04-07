package club.veluxpvp.practice.scoreboard;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import club.veluxpvp.core.utilities.ChatUtil;
import club.veluxpvp.core.utilities.TimeUtil;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.match.MatchState;
import club.veluxpvp.practice.match.MatchTeam;
import club.veluxpvp.practice.match.listener.MatchBridgesListener;
import club.veluxpvp.practice.match.listener.MatchParkourListener;
import club.veluxpvp.practice.utilities.PlayerUtil;

public final class MatchSpectateScoreboard {

	public static List<String> getLines(Player player, Match match) {
		List<String> lines = Lists.newArrayList();
		
		lines.add(ChatUtil.SB_LINE());
		
		if(match.getState() != MatchState.ENDING) {
			lines.add("Ladder&7: &b" + match.getLadder().name);
			lines.add("Duration&7: &b" + TimeUtil.getFormattedDuration(match.getDuration(), true));
			lines.add("");
			
			renderVersusLines(lines, match);
		} else {
			renderFinishLines(lines, match);
		}
		
		lines.add("");
		lines.add("&bveluxpvp.club");
		lines.add(ChatUtil.SB_LINE());
		
		return lines;
	}
	
	private static void renderVersusLines(List<String> lines, Match match) {
		if(match.isFfa()) {
			lines.add("Alive Players&7: &b" + match.getAlivePlayers().size() + "/" + match.getTeam1().getFIRST_TOTAL_MEMBERS());
		} else {
			if(match.isParty()) {
				MatchTeam team1 = match.getTeam1();
				MatchTeam team2 = match.getTeam2();
				
				if(team1 != null) lines.add("&cTeam 1&7: &f" + match.getAliveTeamMembers(team1).size() + "/" + team1.getFIRST_TOTAL_MEMBERS());
				lines.add("&7vs");
				if(team2 != null) lines.add("&9Team 2&7: &f" + match.getAliveTeamMembers(team2).size() + "/" + team2.getFIRST_TOTAL_MEMBERS());
			} else {
				Player player1 = match.getTeam1().getFirstPlayer();
				Player player2 = match.getTeam2().getFirstPlayer();
				
				if(player1 != null) lines.add("&c" + player1.getName() + " &7(" + PlayerUtil.getPing(player1) + "ms)");
				lines.add("&7vs");
				if(player2 != null) lines.add("&9" + player2.getName() + " &7(" + PlayerUtil.getPing(player2) + "ms)");
			}
		}
	}
	
	private static void renderFinishLines(List<String> lines, Match match) {
		String winner = "";
		
		if(match.isFfa()) {
			if(match.getLadder() == Ladder.PARKOUR) {
				winner = Bukkit.getOfflinePlayer(MatchParkourListener.winnerPlayer.get(match)).getName();
			} else {
				winner = match.getLastAlivePlayer().getName();
			}
		} else if(match.getLadder() == Ladder.PARKOUR) {
			MatchTeam winnerTeam = MatchParkourListener.winnerTeam.get(match);
			
			winner = !match.isParty() ? winnerTeam.getFirstPlayer().getName() : (winnerTeam == match.getTeam1() ? "Team 1" : "Team 2");
		} else if(match.getLadder() == Ladder.BRIDGES) {
			MatchTeam winnerTeam = MatchBridgesListener.teamScore.get(match.getTeam1()) == 3 ? match.getTeam1() : match.getTeam2();
			
			winner = !match.isParty() ? winnerTeam.getFirstPlayer().getName() : (winnerTeam == match.getTeam1() ? "Team 1" : "Team 2");
		} else {
			MatchTeam winnerTeam = match.getLastAliveTeam();
			winner = !match.isParty() ? winnerTeam.getFirstPlayer().getName() : (winnerTeam == match.getTeam1() ? "Team 1" : "Team 2");
		}
		
		lines.add("&aMatch ended");
		lines.add("");
		lines.add("Winner&7: &b" + winner);
		lines.add("Duration&7: &b" + TimeUtil.getFormattedDuration(match.getDuration(), true));
	}
}
