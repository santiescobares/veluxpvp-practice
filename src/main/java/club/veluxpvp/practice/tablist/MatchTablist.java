package club.veluxpvp.practice.tablist;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.match.MatchTeam;
import club.veluxpvp.practice.match.TeamType;
import club.veluxpvp.practice.tablist.provider.Skin;
import club.veluxpvp.practice.tablist.provider.TabEntry;
import club.veluxpvp.practice.utilities.PlayerUtil;
import club.veluxpvp.practice.utilities.TimeUtil;

public final class MatchTablist {

	public static final List<TabEntry> get1vs1PlayingLines(Player player, Match match) {
		List<TabEntry> lines = Lists.newArrayList();
		Player opponent = match.getPlayerTeam(player) != null && match.getPlayerTeam(player).getType() == TeamType.TEAM_1 ? match.getTeam2().getFirstPlayer() : match.getTeam1().getFirstPlayer();
		
		// Left
		lines.add(new TabEntry(0, 1, "&7Online: " + Bukkit.getOnlinePlayers().size()));
		lines.add(new TabEntry(0, 3, "&a&lYou"));
		lines.add(new TabEntry(0, 4, ChatColor.GREEN + player.getName(), PlayerUtil.getPing(player), Skin.getPlayer(player)));
		if(match.isRanked()) lines.add(new TabEntry(0, 5, "&fELO&7: &b" + Practice.getInstance().getProfileManager().getProfile(player).getElo(match.getLadder())));
		
		// Middle
		lines.add(new TabEntry(1, 0, "&b&lVelux &7┃ &fPractice"));
		lines.add(new TabEntry(1, 1, "&7Your Connnection", PlayerUtil.getPing(player), Skin.getPlayer(player)));
		lines.add(new TabEntry(1, 3, "&fLadder&7: &b" + match.getLadder().name));
		lines.add(new TabEntry(1, 4, "&fArena&7: &b" + match.getArena().getName()));
		lines.add(new TabEntry(1, 5, "&fDuration&7: &b" + TimeUtil.getFormattedDuration(match.getDuration(), true)));
		
		// Right
		lines.add(new TabEntry(2, 1, "&7Fighting: " + Practice.getInstance().getMatchManager().getFighting()));
		lines.add(new TabEntry(2, 3, "&c&lOpponent"));
		if(opponent != null) {
			lines.add(new TabEntry(2, 4, ChatColor.RED + opponent.getName(), PlayerUtil.getPing(opponent), Skin.getPlayer(opponent)));
			if(match.isRanked()) lines.add(new TabEntry(2, 5, "&fELO&7: &b" + Practice.getInstance().getProfileManager().getProfile(opponent).getElo(match.getLadder())));
		}
		
		return lines;
	}
	
	public static final List<TabEntry> getPartyPlayingLines(Player player, Match match) {
		List<TabEntry> lines = Lists.newArrayList();
		MatchTeam playerTeam = match.getPlayerTeam(player) != null && match.getPlayerTeam(player) == match.getTeam1() ? match.getTeam1() : match.getTeam2();
		MatchTeam enemyTeam = playerTeam != null && playerTeam == match.getTeam1() ? match.getTeam2() : match.getTeam1();
		
		// Left
		lines.add(new TabEntry(0, 1, "&7Online: " + Bukkit.getOnlinePlayers().size()));
		lines.add(new TabEntry(0, 3, "&a&lYour Team &a(" + match.getAliveTeamMembers(playerTeam).size() + "/" + playerTeam.getFIRST_TOTAL_MEMBERS() + ")"));
		
		playerTeam.renderTablistNames(lines, player, match, false, false);
		
		// Middle
		lines.add(new TabEntry(1, 0, "&b&lVelux &7┃ &fPractice"));
		lines.add(new TabEntry(1, 1, "&7Your Connnection", PlayerUtil.getPing(player), Skin.getPlayer(player)));
		lines.add(new TabEntry(1, 3, "&fLadder&7: &b" + match.getLadder().name));
		lines.add(new TabEntry(1, 4, "&fArena&7: &b" + match.getArena().getName()));
		lines.add(new TabEntry(1, 5, "&fDuration&7: &b" + TimeUtil.getFormattedDuration(match.getDuration(), true)));
		
		// Right
		lines.add(new TabEntry(2, 1, "&7Fighting: " + Practice.getInstance().getMatchManager().getFighting()));
		lines.add(new TabEntry(2, 3, "&c&lEnemy Team &c(" + match.getAliveTeamMembers(enemyTeam).size() + "/" + enemyTeam.getFIRST_TOTAL_MEMBERS() + ")"));
		
		enemyTeam.renderTablistNames(lines, player, match, true, false);
		
		return lines;
	}
	
	public static final List<TabEntry> getFFAPlayingLines(Player player, Match match) {
		List<TabEntry> lines = Lists.newArrayList();
		List<Player> enemies = match.getTeam1().getPlayers().stream().filter(p -> p != player && !match.isSpectating(p)).collect(Collectors.toList());
		match.getTeam1().getPlayers().stream().filter(p -> match.isSpectating(p)).forEach(p -> enemies.add(p));
		int aliveOpponents = (int) enemies.stream().filter(p -> !match.isSpectating(p)).count();
		
		// Left
		lines.add(new TabEntry(0, 1, "&7Online: " + Bukkit.getOnlinePlayers().size()));
		lines.add(new TabEntry(0, 3, "&a&lYou"));
		lines.add(new TabEntry(0, 4, ChatColor.GREEN + player.getName(), PlayerUtil.getPing(player), Skin.getPlayer(player)));
		
		// Middle
		lines.add(new TabEntry(1, 0, "&b&lVelux &7┃ &fPractice"));
		lines.add(new TabEntry(1, 1, "&7Your Connnection", PlayerUtil.getPing(player), Skin.getPlayer(player)));
		lines.add(new TabEntry(1, 3, "&fLadder&7: &b" + match.getLadder().name));
		lines.add(new TabEntry(1, 4, "&fArena&7: &b" + match.getArena().getName()));
		lines.add(new TabEntry(1, 5, "&fDuration&7: &b" + TimeUtil.getFormattedDuration(match.getDuration(), true)));
		
		// Right
		lines.add(new TabEntry(2, 1, "&7Fighting: " + Practice.getInstance().getMatchManager().getFighting()));
		lines.add(new TabEntry(2, 3, "&c&lOpponents &c(" + aliveOpponents + "/" + enemies.size() + ")"));
		
		int enemyRaw = 4;
		for(Player p : enemies) {
			if(enemyRaw >= 19) {
				int membersLeft = enemies.size() - 19;
				if(membersLeft > 0) {
					lines.add(new TabEntry(2, enemyRaw, ChatColor.RED + "+" + membersLeft + " more"));
					break;
				}
			}
			
			if(!match.isSpectating(p)) {
				lines.add(new TabEntry(2, enemyRaw++, ChatColor.RED + p.getName(), PlayerUtil.getPing(p), Skin.getPlayer(p)));
			} else {
				lines.add(new TabEntry(2, enemyRaw++, ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + p.getName()));
			}
		}
		
		return lines;
	}
}
