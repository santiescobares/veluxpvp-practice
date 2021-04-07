package club.veluxpvp.practice.scoreboard;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import club.veluxpvp.core.utilities.ChatUtil;
import club.veluxpvp.core.utilities.TimeUtil;
import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.party.Party;
import club.veluxpvp.practice.queue.QueuedPlayer;
import club.veluxpvp.practice.tournament.Tournament;

public final class LobbyScoreboard {

	public static List<String> getLines(Player player) {
		List<String> lines = Lists.newArrayList();
		QueuedPlayer qp = Practice.getInstance().getQueueManager().getPlayer(player);
		Party party = Practice.getInstance().getPartyManager().getPlayerParty(player);
		Tournament tour = Practice.getInstance().getTournamentManager().getActiveTournament();
		
		lines.add(ChatUtil.SB_LINE());
		lines.add("Online&7: &b" + Bukkit.getOnlinePlayers().size());
		lines.add("Fighting&7: &b" + Practice.getInstance().getMatchManager().getFighting());
		
		if(qp != null) {
			lines.add("");
			renderQueueLines(lines, qp);
		} else {
			if(party != null) {
				lines.add("");
				renderPartyLines(lines, player, party);
			}
		}
		
		if(tour != null) {
			lines.add("");
			renderTournamentLines(lines, player, tour);
		}
		
		lines.add("");
		lines.add("&bveluxpvp.club");
		lines.add(ChatUtil.SB_LINE());
		
		return lines;
	}
	
	private static void renderQueueLines(List<String> lines, QueuedPlayer qp) {
		lines.add("&b&lQueue &7(" + (qp.isRanked() ? "Ranked" : "Unranked") + ")");
		lines.add("&7* &a" + qp.getLadder().name);
		if(qp.isRanked()) lines.add("&7* &fELO Range&7: &b" + qp.getMinEloRange() + " - " + qp.getMaxEloRange());
		lines.add("&7* &fTime&7: &b" + TimeUtil.getFormattedDuration(qp.getTime(), true));
	}
	
	private static void renderPartyLines(List<String> lines, Player player, Party party) {
		lines.add("&b&lParty &7(" + party.getMembers().size() + "/" + party.getSlots() + ")");
		lines.add("&7* &fLeader&7: &b" + party.getLeader().getPlayer().getName());
		lines.add("&7* &fHCF Class&7: &b" + party.getMember(player).getHcfClass().name);
	}
	
	private static void renderTournamentLines(List<String> lines, Player player, Tournament tour) {
		lines.add("&b&lTournament &7(" + tour.getTeamSize() + "vs" + tour.getTeamSize() + ")");
		lines.add("&7* &fKit&7: &b" + tour.getLadder().name);
		lines.add("&7* &fParticipants&7: &b" + tour.getParticipants().size() + "/" + tour.getTeamsLimit());
		
		switch(tour.getState()) {
		case WAITING:
			lines.add("&7* &aWaiting for players...");
			break;
		case STARTING:
			lines.add("&7* &fBegins in &b" + (tour.getStartingTime() + 1));
			break;
		case STARTING_ROUND:
			lines.add("&7* &fRound&7: &b" + tour.getRound());
			lines.add("&7* &fStarts in &b" + (tour.getStartingTime() + 1));
			break;
		case PLAYING:
			lines.add("&7* &fRound&7: &b" + tour.getRound());
			lines.add("&7* &fDuration&7: &b" + TimeUtil.getFormattedDuration(tour.getDuration(), true));
			break;
		default:
			break;
		}
	}
}
