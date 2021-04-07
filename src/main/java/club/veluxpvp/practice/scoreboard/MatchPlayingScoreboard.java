package club.veluxpvp.practice.scoreboard;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import club.veluxpvp.core.utilities.ChatUtil;
import club.veluxpvp.core.utilities.TimeUtil;
import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.match.MatchTeam;
import club.veluxpvp.practice.match.listener.MatchBridgesListener;
import club.veluxpvp.practice.match.listener.MatchEnderPearlListener;
import club.veluxpvp.practice.match.listener.MatchHCTListener;
import club.veluxpvp.practice.match.listener.MatchParkourListener;
import club.veluxpvp.practice.party.pvpclass.BardClass;
import club.veluxpvp.practice.party.pvpclass.HCFClassType;
import club.veluxpvp.practice.utilities.PlayerUtil;

public final class MatchPlayingScoreboard {

	public static List<String> getLines(Player player, Match match) {
		List<String> lines = Lists.newArrayList();
		
		lines.add(ChatUtil.SB_LINE());
		
		renderYourTeamLines(lines, player, match);
		renderOpponentsLines(lines, player, match);
		renderGameInfoLines(lines, player, match);
		
		lines.add("");
		lines.add("&bveluxpvp.club");
		lines.add(ChatUtil.SB_LINE());
		
		return lines;
	}
	
	private static void renderYourTeamLines(List<String> lines, Player player, Match match) {
		MatchTeam playerTeam = match.getPlayerTeam(player);
		
		if(match.isFfa()) return;
		
		if(playerTeam != null) {
			if(playerTeam.getPlayers().size() == 1) return;
			
			if(match.getLadder() == Ladder.BRIDGES || playerTeam.getPlayers().size() > 7) {
				lines.add("&aYour Team&7: &f" + match.getAliveTeamMembers(playerTeam).size() + "/" + playerTeam.getFIRST_TOTAL_MEMBERS());
			} else {
				lines.add("&aYour Team (" + match.getAliveTeamMembers(playerTeam).size() + "/" + playerTeam.getFIRST_TOTAL_MEMBERS() + ")");
				playerTeam.renderScoreboardNames(lines, player, match);
			}
		}
	}
	
	private static void renderOpponentsLines(List<String> lines, Player player, Match match) {
		MatchTeam playerTeam = match.getPlayerTeam(player);
		
		if(match.isFfa()) {
			lines.add("Alive Players&7: &b" + match.getAlivePlayers().size() + "/" + match.getTeam1().getFIRST_TOTAL_MEMBERS());
			return;
		}
		
		if(!match.isParty() && !match.isFfa()) {
			Player opponent = playerTeam != null && playerTeam == match.getTeam1() ? match.getTeam2().getFirstPlayer() : match.getTeam1().getFirstPlayer();
			
			if(opponent != null) lines.add("Opponent&7: &b" + opponent.getName());
			return;
		}
		
		MatchTeam enemyTeam = playerTeam != null && playerTeam == match.getTeam1() ? match.getTeam2() : match.getTeam1();
		if(enemyTeam != null) lines.add("&cOpponents&7: &f" + match.getAliveTeamMembers(enemyTeam).size() + "/" + enemyTeam.getFIRST_TOTAL_MEMBERS());
	}
	
	private static void renderGameInfoLines(List<String> lines, Player player, Match match) {
		MatchTeam playerTeam = match.getPlayerTeam(player);
		
		lines.add("Duration&7: &b" + TimeUtil.getFormattedDuration(match.getDuration(), true));
		
		if(!match.isFfa() && !match.isParty()) {
			boolean isPingLine = Practice.getInstance().getProfileManager().getProfile(player).isPingOnScoreboard();
			Player opponent = playerTeam != null && playerTeam == match.getTeam1() ? match.getTeam2().getFirstPlayer() : match.getTeam1().getFirstPlayer();
			
			if(opponent != null && isPingLine) lines.add("Ping&7: &a" + PlayerUtil.getPing(player) + "ms &7┃ &c" + PlayerUtil.getPing(opponent) + "ms"); 
		
			if(match.getLadder() == Ladder.HCT_NO_DEBUFF || match.getLadder() == Ladder.HCT_DEBUFF) {
				HCFClassType activeClass = Practice.getInstance().getHcfClassManager().getActiveClass(player);
				
				if(activeClass != null) {
					if(activeClass == HCFClassType.BARD) {
						lines.add("&b&lEnergy&7: &f" + BardClass.getEnergy(player));
					}
				}
				
				if(MatchHCTListener.isArcherMarked(player)) {
					long archerMarkTime = MatchHCTListener.archerMark.get(player.getUniqueId());
					int timeleft = (int) (System.currentTimeMillis() - archerMarkTime);
					
					lines.add("&6&lArcher Mark&7: &f" + TimeUtil.getFormattedDuration(timeleft, true));
				}
			}
		}
		
		if(match.getLadder() == Ladder.PARKOUR) {
			int reachedCheckpoints = MatchParkourListener.reachedCheckpoints.getOrDefault(player.getUniqueId(), Sets.newHashSet()).size();
			
			lines.add("Checkpoints&7: &b" + reachedCheckpoints + "/" + match.getArena().getParkourCheckpoints().size());
		} else if(match.getLadder() == Ladder.BRIDGES) {
			int redTeamScore = MatchBridgesListener.getTeamScore(match, match.getTeam1());
			String redTeamScoreText = redTeamScore == 0 ? ChatColor.GRAY + "•••" : redTeamScore == 1 ? ChatColor.RED + "•" + ChatColor.GRAY + "••" : redTeamScore == 2 ? ChatColor.RED + "••" + ChatColor.GRAY + "•" : ChatColor.RED + "•••";
			
			int blueTeamScore = MatchBridgesListener.getTeamScore(match, match.getTeam2());
			String blueTeamScoreText = blueTeamScore == 0 ? ChatColor.GRAY + "•••" : blueTeamScore == 1 ? ChatColor.BLUE + "•" + ChatColor.GRAY + "••" : blueTeamScore == 2 ? ChatColor.BLUE + "••" + ChatColor.GRAY + "•" : ChatColor.BLUE + "•••";
			
			lines.add("");
			lines.add("&c[R] " + redTeamScoreText);
			lines.add("&9[B] " + blueTeamScoreText);
			lines.add("");
			lines.add("Kills&7: &b" + match.getKills().getOrDefault(player.getUniqueId(), 0));
			lines.add("Goals&7: &b" + MatchBridgesListener.getGoals(player, match));
		} else {
			if(MatchEnderPearlListener.pearlCooldown.containsKey(player.getUniqueId())) {
				long timeleft = MatchEnderPearlListener.pearlCooldown.get(player.getUniqueId());
				
				if(timeleft > System.currentTimeMillis()) {
					int millisLeft = (int) (timeleft - System.currentTimeMillis());
			        double secondsLeft = millisLeft / 1000D;
			        secondsLeft = Math.round(10D * secondsLeft) / 10D;
			        
			        lines.add("Enderpearl&7: &b" + secondsLeft);
				}
			}
			
			if(match.getLadder() == Ladder.HCT_NO_DEBUFF || match.getLadder() == Ladder.HCT_DEBUFF) {
				HCFClassType activeClass = Practice.getInstance().getHcfClassManager().getActiveClass(player);
				
				if(activeClass != null) {
					if(activeClass == HCFClassType.BARD) {
						lines.add("&b&lEnergy&7: &f" + BardClass.getEnergy(player));
					}
				}
				
				if(MatchHCTListener.isArcherMarked(player)) {
					long archerMarkTime = MatchHCTListener.archerMark.get(player.getUniqueId());
					int timeleft = (int) (System.currentTimeMillis() - archerMarkTime);
					
					lines.add("&6&lArcher Mark&7: &f" + TimeUtil.getFormattedDuration(timeleft, true));
				}
			}
		}
	}
}
