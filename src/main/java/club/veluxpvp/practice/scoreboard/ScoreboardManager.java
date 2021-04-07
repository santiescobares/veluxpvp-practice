package club.veluxpvp.practice.scoreboard;

import java.util.List;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.scoreboard.provider.AssembleAdapter;

public class ScoreboardManager implements AssembleAdapter {

	@Override
	public String getTitle(Player player) {
		return "&b&lPractice";
	}

	@Override
	public List<String> getLines(Player player) {
		Match match = Practice.getInstance().getMatchManager().getPlayerMatch(player);
		
		if(match == null) {
			return LobbyScoreboard.getLines(player);
		} else {
			if(match.isSpectating(player)) return MatchSpectateScoreboard.getLines(player, match);
			
			switch(match.getState()) {
			case STARTING:
				return MatchStartingScoreboard.getLines(player, match);
			case PLAYING:
				return MatchPlayingScoreboard.getLines(player, match);
			case ENDING:
				return MatchEndingScoreboard.getLines(player, match);
			default:
				break;
			}
		}
		
		return LobbyScoreboard.getLines(player);
	}
}
