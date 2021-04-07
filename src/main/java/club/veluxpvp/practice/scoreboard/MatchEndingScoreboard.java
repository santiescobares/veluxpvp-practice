package club.veluxpvp.practice.scoreboard;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import club.veluxpvp.core.utilities.ChatUtil;
import club.veluxpvp.core.utilities.TimeUtil;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.match.MatchTeam;
import club.veluxpvp.practice.match.listener.MatchBridgesListener;
import club.veluxpvp.practice.match.listener.MatchParkourListener;

public final class MatchEndingScoreboard {

	public static List<String> getLines(Player player, Match match) {
		List<String> lines = Lists.newArrayList();
		
		lines.add(ChatUtil.SB_LINE());
		
		renderFinishLines(lines, player, match);
		
		lines.add("");
		lines.add("&bveluxpvp.club");
		lines.add(ChatUtil.SB_LINE());
		
		return lines;
	}
	
	private static void renderFinishLines(List<String> lines, Player player, Match match) {
		MatchTeam playerTeam = match.getPlayerTeam(player);
		String winner = "";
		
		if(match.isFfa()) {
			if(match.getLadder() == Ladder.PARKOUR) {
				winner = Bukkit.getOfflinePlayer(MatchParkourListener.winnerPlayer.get(match)).getName();
			} else {
				winner = match.getLastAlivePlayer().getName();
			}
		} else if(match.getLadder() == Ladder.PARKOUR) {
			MatchTeam winnerTeam = MatchParkourListener.winnerTeam.get(match);
			
			winner = !match.isParty() ? winnerTeam.getFirstPlayer().getName() : (winnerTeam == playerTeam ? "Your Team" : "Enemy Team");
		} else if(match.getLadder() == Ladder.BRIDGES) {
			MatchTeam winnerTeam = MatchBridgesListener.teamScore.get(match.getTeam1()) == 3 ? match.getTeam1() : match.getTeam2();
			
			winner = !match.isParty() ? winnerTeam.getFirstPlayer().getName() : (winnerTeam == playerTeam ? "Your Team" : "Enemy Team");
		} else {
			MatchTeam winnerTeam = match.getLastAliveTeam();
			winner = !match.isParty() ? winnerTeam.getFirstPlayer().getName() : (winnerTeam == playerTeam ? "Your Team" : "Enemy Team");
		}
		
		lines.add("&aMatch ended");
		lines.add("");
		lines.add("Winner&7: &b" + winner);
		lines.add("Duration&7: &b" + TimeUtil.getFormattedDuration(match.getDuration(), true));
	}
}
