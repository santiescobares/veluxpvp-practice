package club.veluxpvp.practice.tournament.command;

import org.bukkit.command.CommandSender;

import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class TournamentStartCommand extends TournamentCommand {
	
	@Command(name = "tournament.start", aliases = {"tour.start"}, permission = "practice.tournament.start")
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		CommandSender sender = cmd.getSender();
		
		if(args.length >= 3) {
			Ladder ladder = Ladder.getByName(args[0]);
			int teamSize = 0;
			int teamLimit = 0;
			
			if(ladder == null) {
				sender.sendMessage(ChatUtil.TRANSLATE("&cLadder \"" + args[0] + "\" not found! Examples: No_Debuff - HCT_NoDebuff - HCT_Debuff"));
				return;
			}
			
			try {
				teamSize = Integer.valueOf(args[1]);
				teamLimit = Integer.valueOf(args[2]);
				
				if(teamSize <= 0 || teamLimit <= 0) {
					sender.sendMessage(ChatUtil.TRANSLATE("&cThe number must be positive!"));
					return;
				}
				
				if(teamSize > 10) {
					sender.sendMessage(ChatUtil.TRANSLATE("&cThe maximum team size is 10!"));
					return;
				}
				
				if(teamLimit < 4) {
					sender.sendMessage(ChatUtil.TRANSLATE("&cThe minimum team limit is 4!"));
					return;
				}
				
				if(tm.getActiveTournament() != null) {
					sender.sendMessage(ChatUtil.TRANSLATE("&cThere is already an active tournament!"));
					return;
				}
				
				tm.startTournament(ladder, teamSize, teamLimit);
			} catch(NumberFormatException e) {
				sender.sendMessage(ChatUtil.TRANSLATE("&cYou must enter a valid number!"));
			}
		} else {
			sender.sendMessage(ChatUtil.TRANSLATE("&cUsage: /tournament start <kit> <teamSize> <teamLimit>"));
		}
	}
}
