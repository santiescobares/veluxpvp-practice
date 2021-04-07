package club.veluxpvp.practice.tournament.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.tournament.TournamentManager;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class TournamentCommand {

	protected TournamentManager tm;
	
	public TournamentCommand() {
		this.tm = Practice.getInstance().getTournamentManager();
		Practice.getInstance().getCommandFramework().registerCommands(this);
	}
	
	@Command(name = "tournament", aliases = {"tour"})
	public void execute(CommandArgs cmd) {
		sendHelpMessage(cmd.getSender());
	}
	
	private void sendHelpMessage(CommandSender sender) {
		sender.sendMessage(ChatUtil.LINE());
		sender.sendMessage(ChatUtil.TRANSLATE("&b&lTournament Commands"));
		sender.sendMessage(ChatUtil.LINE());
		sender.sendMessage(ChatUtil.TRANSLATE(" &7* &f/tournament join &7- &bJoin in the active tournament"));
		sender.sendMessage(ChatUtil.TRANSLATE(" &7* &f/tournament leave &7- &bLeave your current tournament"));
		
		if(sender instanceof Player) {
			Player player = (Player) sender;
			
			if(player.hasPermission("practice.tournament.start")) sender.sendMessage(ChatUtil.TRANSLATE(" &7* &f/tournament start <kit> <teamSize> <teamLimit> &7- &bStart a tournament"));
			if(player.hasPermission("practice.tournament.forcestart")) sender.sendMessage(ChatUtil.TRANSLATE(" &7* &f/tournament forcestart &7- &bForce a tournament to start"));
			if(player.hasPermission("practice.tournament.cancel")) sender.sendMessage(ChatUtil.TRANSLATE(" &7* &f/tournament cancel &7- &bCancel a tournament"));
		} else {
			sender.sendMessage(ChatUtil.TRANSLATE(" &7* &f/tournament start <kit> <teamSize> <teamLimit> &7- &bStart a tournament"));
			sender.sendMessage(ChatUtil.TRANSLATE(" &7* &f/tournament forcestart &7- &bForce a tournament to start"));
			sender.sendMessage(ChatUtil.TRANSLATE(" &7* &f/tournament cancel &7- &bCancel a tournament"));
		}
		
		sender.sendMessage(ChatUtil.LINE());
	}
}
