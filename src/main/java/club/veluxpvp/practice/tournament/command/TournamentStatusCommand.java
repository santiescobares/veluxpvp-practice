package club.veluxpvp.practice.tournament.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.tournament.Tournament;
import club.veluxpvp.practice.tournament.TournamentState;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.TimeUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class TournamentStatusCommand extends TournamentCommand {
	
	@Command(name = "tournament.status", aliases = {"tour.status"})
	public void execute(CommandArgs cmd) {
		CommandSender sender = cmd.getSender();
		
		Tournament tour = tm.getActiveTournament();
		
		if(tour == null) {
			sender.sendMessage(ChatUtil.TRANSLATE("&cThere aren't any active tournament!"));
			return;
		}
		
		sender.sendMessage(ChatUtil.SHORTER_LINE());
		sender.sendMessage(ChatUtil.TRANSLATE("&b&lTournament Status"));
		sender.sendMessage(ChatUtil.SHORTER_LINE());
		sender.sendMessage(ChatUtil.TRANSLATE(" &7* &fKit&7: &b" + tour.getLadder().name));
		sender.sendMessage(ChatUtil.TRANSLATE(" &7* &fTeam Size&7: &b" + tour.getTeamSize() + "vs" + tour.getTeamSize()));
		
		if(tour.getState() == TournamentState.WAITING) {
			sender.sendMessage(ChatUtil.TRANSLATE(" &7* &fTeams&7: &b" + tour.getParticipants().size() + "/" + tour.getTeamsLimit()));
		} else if(tour.getState() == TournamentState.STARTING) {
			sender.sendMessage(ChatUtil.TRANSLATE(" &7* &fTeams&7: &b" + tour.getParticipants().size() + "/" + tour.getTeamsLimit()));
			sender.sendMessage(ChatUtil.TRANSLATE(" &7* &fStarts in&7: &b" + tour.getStartingTime()));
		} else if(tour.getState() == TournamentState.STARTING_ROUND) {
			sender.sendMessage(ChatUtil.TRANSLATE(" &7* &fTeams&7: &b" + tour.getParticipants().size() + "/" + tour.getTOTAL_PARTICIPANTS()));
			sender.sendMessage(ChatUtil.TRANSLATE(" &7* &fRound&7: &b" + tour.getRound()));
			sender.sendMessage(ChatUtil.TRANSLATE(" &7* &fStarts in&7: &b" + tour.getStartingTime()));
		} else {
			sender.sendMessage(ChatUtil.TRANSLATE(" &7* &fTeams&7: &b" + tour.getParticipants().size() + "/" + tour.getTOTAL_PARTICIPANTS()));
			sender.sendMessage(ChatUtil.TRANSLATE(" &7* &fRound&7: &b" + tour.getRound()));
			sender.sendMessage(ChatUtil.TRANSLATE(" &7* &fRound Duration&7: &b" + TimeUtil.getFormattedDuration(tour.getDuration(), true)));
		}
		
		if(tour.getLiveMatches().size() > 0) {
			sender.sendMessage(" ");
			sender.sendMessage(ChatUtil.TRANSLATE("&bLive Matches: &7(" + tour.getLiveMatches().size() + ")"));
			
			for(Match m : tour.getLiveMatches()) {
				Player player1 = m.getTeam1().getFirstPlayer();
				Player player2 = m.getTeam2().getFirstPlayer();
				
				String firstPlayer = tour.getTeamSize() > 1 ? player1.getName() + "'s Team" : player1.getName();
				String secondPlayer = tour.getTeamSize() > 1 ? player2.getName() + "'s Team" : player2.getName();
				
				sender.sendMessage(ChatUtil.TRANSLATE(" &7* &f" + firstPlayer + " &7- &f" + secondPlayer));
			}
		}
		
		sender.sendMessage(ChatUtil.SHORTER_LINE());
	}
}
