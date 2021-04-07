package club.veluxpvp.practice.tournament.command;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.tournament.Tournament;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class TournamentLeaveCommand extends TournamentCommand {
	
	@Command(name = "tournament.leave", aliases = {"tour.leave"}, playersOnly = true)
	public void execute(CommandArgs cmd) {
		Player player = cmd.getPlayer();
		
		Tournament tour = tm.getActiveTournament();
		
		if(tour == null) {
			player.sendMessage(ChatUtil.TRANSLATE("&cThere aren't any active tournament!"));
			return;
		}
		
		if(!tour.isInTournament(player)) {
			player.sendMessage(ChatUtil.TRANSLATE("&cYou are not in a tournament!"));
			return;
		}
		
		if(Practice.getInstance().getMatchManager().getPlayerMatch(player) != null) {
			player.sendMessage(ChatUtil.TRANSLATE("&cYou can't leave the tournament while in match!"));
			return;
		}
		
		tour.removeParticipant(Practice.getInstance().getPartyManager().getPlayerParty(player), false);
		player.sendMessage(ChatUtil.TRANSLATE("&cYour team has left the tournament!"));
	}
}
