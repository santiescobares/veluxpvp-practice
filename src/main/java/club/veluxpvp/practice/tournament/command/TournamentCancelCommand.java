package club.veluxpvp.practice.tournament.command;

import org.bukkit.command.CommandSender;

import club.veluxpvp.practice.tournament.Tournament;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class TournamentCancelCommand extends TournamentCommand {
	
	@Command(name = "tournament.cancel", aliases = {"tour.cancel"}, permission = "practice.tournament.cancel")
	public void execute(CommandArgs cmd) {
		CommandSender sender = cmd.getSender();
		
		Tournament tour = tm.getActiveTournament();
		
		if(tour == null) {
			sender.sendMessage(ChatUtil.TRANSLATE("&cThere aren't any active tournament!"));
			return;
		}
		
		tour.finish(true);
	}
}
