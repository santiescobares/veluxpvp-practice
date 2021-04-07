package club.veluxpvp.practice.scoreboard;

import java.util.List;

import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import club.veluxpvp.core.utilities.ChatUtil;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.match.MatchTeam;

public final class MatchStartingScoreboard {

	public static List<String> getLines(Player player, Match match) {
		List<String> lines = Lists.newArrayList();
		
		lines.add(ChatUtil.SB_LINE());
		
		renderOpponentsLine(lines, player, match);
		
		lines.add("Arena&7: &b" + match.getArena().getName());
		lines.add("");
		lines.add("Starting in&7: &b" + (match.getStartingTime() >= 5 ? 5 : (match.getStartingTime() + 1)));
		lines.add("");
		lines.add("&bveluxpvp.club");
		lines.add(ChatUtil.SB_LINE());
		
		return lines;
	}
	
	private static void renderOpponentsLine(List<String> lines, Player player, Match match) {
		if(match.isParty()) {
			MatchTeam playerTeam = match.getPlayerTeam(player);
			MatchTeam enemyTeam = playerTeam != null && playerTeam == match.getTeam1() ? match.getTeam2() : match.getTeam1();
			
			if(enemyTeam != null) lines.add("Opponents&7: &b" + enemyTeam.getFirstPlayer().getName() + "'s Team");
		} else {
			MatchTeam playerTeam = match.getPlayerTeam(player);
			Player enemyPlayer = playerTeam != null && playerTeam == match.getTeam1() ? match.getTeam2().getFirstPlayer() : match.getTeam1().getFirstPlayer();
		
			if(enemyPlayer != null) lines.add("Opponent&7: &b" + enemyPlayer.getName());
		}
	}
}
