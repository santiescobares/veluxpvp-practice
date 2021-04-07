package club.veluxpvp.practice.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.profile.Profile;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.TimeUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class PlayTimeCommand {

	public PlayTimeCommand() {
		Practice.getInstance().getCommandFramework().registerCommands(this);
	}
	
	@SuppressWarnings("deprecation")
	@Command(name = "playtime")
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		CommandSender sender = cmd.getSender();
		
		if(args.length >= 1) {
			OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
			Profile targetProfile = Practice.getInstance().getProfileManager().getProfile(target);
			
			if(targetProfile == null) {
				sender.sendMessage(ChatUtil.TRANSLATE("&c" + target.getName() + "'s profile not found!"));
				return;
			}
			
			long playtimeMillis = targetProfile.getPlayTime();
			if(target.isOnline()) playtimeMillis += System.currentTimeMillis();
			String playTime = TimeUtil.formatDuration((int) playtimeMillis);
			
			sender.sendMessage(ChatUtil.TRANSLATE("&b" + target.getName() + "&f's play time&7: &b" + playTime));
		} else {
			if(sender instanceof Player) {
				Profile profile = Practice.getInstance().getProfileManager().getProfile(cmd.getPlayer());
				long playtimeMillis = profile.getPlayTime() + System.currentTimeMillis();
				String playTime = TimeUtil.formatDuration((int) playtimeMillis);
				
				sender.sendMessage(ChatUtil.TRANSLATE("&fYour play time&7: &b" + playTime));
			} else {
				sender.sendMessage(ChatUtil.TRANSLATE("&cUsage: /playtime <player>"));
			}
		}
	}
}
