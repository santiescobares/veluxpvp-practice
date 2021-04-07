package club.veluxpvp.practice.tournament.command;

import org.bukkit.command.CommandSender;

import club.veluxpvp.practice.tournament.Tournament;
import club.veluxpvp.practice.tournament.TournamentState;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class TournamentForceStartCommand extends TournamentCommand {
	
	@Command(name = "tournament.forcestart", aliases = {"tour.forcestart"}, permission = "practice.tournament.forcestart")
	public void execute(CommandArgs cmd) {
		CommandSender sender = cmd.getSender();
		
		Tournament tour = tm.getActiveTournament();
		
		if(tour == null) {
			sender.sendMessage(ChatUtil.TRANSLATE("&cThere aren't any active tournament!"));
			return;
		}
		
		if(tour.getState() != TournamentState.WAITING) {
			sender.sendMessage(ChatUtil.TRANSLATE("&cThe tournament has already started!"));
			return;
		}
		
		if(tour.getParticipants().size() < 2) {
			sender.sendMessage(ChatUtil.TRANSLATE("&cThe amount of participants must be at least 2 to start the tournament!"));
			return;
		}
		
		tour.start();
	}
}
