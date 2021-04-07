package club.veluxpvp.practice.tablist;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.match.MatchTeam;
import club.veluxpvp.practice.tablist.provider.Skin;
import club.veluxpvp.practice.tablist.provider.TabEntry;
import club.veluxpvp.practice.utilities.PlayerUtil;
import club.veluxpvp.practice.utilities.TimeUtil;

public final class MatchSpectateTablist {

	public static final List<TabEntry> get1vs1PlayingLines(Player player, Match match) {
		List<TabEntry> lines = Lists.newArrayList();
		Player player1 = match.getTeam1().getFirstPlayer();
		Player player2 = match.getTeam2().getFirstPlayer();
				
		// Left
		lines.add(new TabEntry(0, 1, "&7Online: " + Bukkit.getOnlinePlayers().size()));
		lines.add(new TabEntry(0, 3, "&c&lTeam 1"));
		if(player1 != null) {
			lines.add(new TabEntry(0, 4, ChatColor.RED + player1.getName(), PlayerUtil.getPing(player1), Skin.getPlayer(player1)));
			if(match.isRanked()) lines.add(new TabEntry(0, 5, "&fELO&7: &b" + Practice.getInstance().getProfileManager().getProfile(player1).getElo(match.getLadder())));
		}
		
		// Middle
		lines.add(new TabEntry(1, 0, "&b&lVelux &7┃ &fPractice"));
		lines.add(new TabEntry(1, 1, "&7Your Connnection", PlayerUtil.getPing(player), Skin.getPlayer(player)));
		lines.add(new TabEntry(1, 3, "&fLadder&7: &b" + match.getLadder().name));
		lines.add(new TabEntry(1, 4, "&fArena&7: &b" + match.getArena().getName()));
		lines.add(new TabEntry(1, 5, "&fDuration&7: &b" + TimeUtil.getFormattedDuration(match.getDuration(), true)));
		
		// Right
		lines.add(new TabEntry(2, 1, "&7Fighting: " + Practice.getInstance().getMatchManager().getFighting()));
		lines.add(new TabEntry(2, 3, "&9&lTeam 2"));
		if(player2 != null) {
			lines.add(new TabEntry(2, 4, ChatColor.BLUE + player2.getName(), PlayerUtil.getPing(player2), Skin.getPlayer(player2)));
			if(match.isRanked()) lines.add(new TabEntry(2, 5, "&fELO&7: &b" + Practice.getInstance().getProfileManager().getProfile(player2).getElo(match.getLadder())));
		}
		
		return lines;
	}
	
	public static final List<TabEntry> getPartyPlayingLines(Player player, Match match) {
		List<TabEntry> lines = Lists.newArrayList();
		MatchTeam team1 = match.getTeam1();
		MatchTeam team2 = match.getTeam2();
		
		// Left
		lines.add(new TabEntry(0, 1, "&7Online: " + Bukkit.getOnlinePlayers().size()));
		lines.add(new TabEntry(0, 3, "&c&lTeam 1 &c(" + match.getAliveTeamMembers(team1).size() + "/" + team1.getFIRST_TOTAL_MEMBERS() + ")"));
		
		team1.renderTablistNames(lines, player, match, false, true);
		
		// Middle
		lines.add(new TabEntry(1, 0, "&b&lVelux &7┃ &fPractice"));
		lines.add(new TabEntry(1, 1, "&7Your Connnection", PlayerUtil.getPing(player), Skin.getPlayer(player)));
		lines.add(new TabEntry(1, 3, "&fLadder&7: &b" + match.getLadder().name));
		lines.add(new TabEntry(1, 4, "&fArena&7: &b" + match.getArena().getName()));
		lines.add(new TabEntry(1, 5, "&fDuration&7: &b" + TimeUtil.getFormattedDuration(match.getDuration(), true)));
		
		// Right
		lines.add(new TabEntry(2, 1, "&7Fighting: " + Practice.getInstance().getMatchManager().getFighting()));
		lines.add(new TabEntry(2, 3, "&9&lTeam 2 &9(" + match.getAliveTeamMembers(team2).size() + "/" + team2.getFIRST_TOTAL_MEMBERS() + ")"));
		
		team2.renderTablistNames(lines, player, match, true, true);
		
		return lines;
	}
	
	public static final List<TabEntry> getFFAPlayingLines(Player player, Match match) {
		List<TabEntry> lines = Lists.newArrayList();
		MatchTeam team1 = match.getTeam1();
		
		// Left
		lines.add(new TabEntry(0, 1, "&7Online: " + Bukkit.getOnlinePlayers().size()));
		lines.add(new TabEntry(0, 3, "&c&lAlive Players &c(" + match.getAlivePlayers().size() + "/" + match.getTeam1().getFIRST_TOTAL_MEMBERS() + ")"));

		int enemyRaw = 4;
		for(Player p : team1.getPlayers()) {
			if(enemyRaw >= 19) {
				int membersLeft = team1.getPlayers().size() - 19;
				if(membersLeft > 0) {
					lines.add(new TabEntry(0, enemyRaw, ChatColor.RED + "+" + membersLeft + " more"));
					break;
				}
			}
			
			if(!match.isSpectating(p)) {
				lines.add(new TabEntry(0, enemyRaw++, ChatColor.RED + p.getName(), PlayerUtil.getPing(p), Skin.getPlayer(p)));
			} else {
				lines.add(new TabEntry(0, enemyRaw++, ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + p.getName()));
			}
		}
		
		// Middle
		lines.add(new TabEntry(1, 0, "&b&lVelux &7┃ &fPractice"));
		lines.add(new TabEntry(1, 1, "&7Your Connnection", PlayerUtil.getPing(player), Skin.getPlayer(player)));
		lines.add(new TabEntry(1, 3, "&fLadder&7: &b" + match.getLadder().name));
		lines.add(new TabEntry(1, 4, "&fArena&7: &b" + match.getArena().getName()));
		lines.add(new TabEntry(1, 5, "&fDuration&7: &b" + TimeUtil.getFormattedDuration(match.getDuration(), true)));
		
		// Right
		lines.add(new TabEntry(2, 1, "&7Fighting: " + Practice.getInstance().getMatchManager().getFighting()));
		
		return lines;
	}
}
