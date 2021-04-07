package club.veluxpvp.practice.tablist;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.party.Party;
import club.veluxpvp.practice.tablist.provider.TabAdapter;
import club.veluxpvp.practice.tablist.provider.TabEntry;

public class TablistManager implements TabAdapter {

	@Override
	public String getHeader(Player player) {
		return ChatColor.AQUA + "" + ChatColor.BOLD + "VeluxPvP Network";
	}

	@Override
	public String getFooter(Player player) {
		return ChatColor.WHITE + "You are currently playing on veluxpvp.club";
	}

	@Override
	public List<TabEntry> getLines(Player player) {
		Match match = Practice.getInstance().getMatchManager().getPlayerMatch(player);
		Party party = Practice.getInstance().getPartyManager().getPlayerParty(player);
		
		if(match == null) {
			if(party != null) return LobbyTablist.getPartyLobbyLines(player, party);
			
			return LobbyTablist.getDefaultLobbyLines(player);
		}
		
		if(match.isSpectating(player) && match.getPlayerTeam(player) == null) {
			if(match.isFfa()) return MatchSpectateTablist.getFFAPlayingLines(player, match);
			if(match.isParty()) return MatchSpectateTablist.getPartyPlayingLines(player, match);
			
			return MatchSpectateTablist.get1vs1PlayingLines(player, match);
		}
		
		if(match.isFfa()) {
			return MatchTablist.getFFAPlayingLines(player, match);
		}
		
		if(match.isParty()) {
			return MatchTablist.getPartyPlayingLines(player, match);
		}
		
		return MatchTablist.get1vs1PlayingLines(player, match);
	}
}
