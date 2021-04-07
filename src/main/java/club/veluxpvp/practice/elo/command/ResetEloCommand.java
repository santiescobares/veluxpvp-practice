package club.veluxpvp.practice.elo.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.profile.Profile;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class ResetEloCommand {

	public ResetEloCommand() {
		Practice.getInstance().getCommandFramework().registerCommands(this);
	}
	
	@SuppressWarnings("deprecation")
	@Command(name = "resetelo", permission = "practice.command.resetelo")
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		CommandSender sender = cmd.getSender();
		
		if(args.length >= 1) {
			OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
			Profile targetProfile = Practice.getInstance().getProfileManager().getProfile(target);
			
			if(targetProfile == null) {
				sender.sendMessage(ChatUtil.TRANSLATE("&c\"" + target.getName() + "\" has never played before!"));
				return;
			}
			
			targetProfile.resetElo();
			
			sender.sendMessage(ChatUtil.TRANSLATE("&b" + target.getName() + "&f's elo has been &creset&f!"));
		} else {
			sender.sendMessage(ChatUtil.TRANSLATE("&cUsage: /resetelo <player>"));
		}
	}
}
