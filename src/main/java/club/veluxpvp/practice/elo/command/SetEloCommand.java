package club.veluxpvp.practice.elo.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.profile.Profile;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class SetEloCommand {

	public SetEloCommand() {
		Practice.getInstance().getCommandFramework().registerCommands(this);
	}
	
	@SuppressWarnings("deprecation")
	@Command(name = "setelo", permission = "practice.command.setelo")
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		CommandSender sender = cmd.getSender();
		
		if(args.length >= 3) {
			OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
			Profile targetProfile = Practice.getInstance().getProfileManager().getProfile(target);
			Ladder ladder = Ladder.getByName(args[1]);
			int elo = 0;
			
			if(targetProfile == null) {
				sender.sendMessage(ChatUtil.TRANSLATE("&c\"" + target.getName() + "\" has never played before!"));
				return;
			}
			
			if(ladder == null) {
				sender.sendMessage(ChatUtil.TRANSLATE("&cLadder \"" + args[1] + "\" not found!"));
				return;
			}
			
			if(ladder == Ladder.COMBO_FLY || ladder == Ladder.HCF || ladder == Ladder.HCT_NO_DEBUFF || ladder == Ladder.HCT_DEBUFF) {
				sender.sendMessage(ChatUtil.TRANSLATE("&c" + ladder.name + " is not a ranked ladder!"));
				return;
			}
			
			try {
				elo = Integer.valueOf(args[2]);
				
				if(elo <= 0) {
					sender.sendMessage(ChatUtil.TRANSLATE("&cThe amount must be positive!"));
					return;
				}
				
				targetProfile.setElo(ladder, elo);
				
				sender.sendMessage(ChatUtil.TRANSLATE("&b" + target.getName() + "&f's elo has been &aset &fto &b" + elo + " &fon ladder &b" + ladder.name + "&f!"));
			} catch(NumberFormatException e) {
				sender.sendMessage(ChatUtil.TRANSLATE("&cYou must enter a valid number!"));
			}
		} else {
			sender.sendMessage(ChatUtil.TRANSLATE("&cUsage: /setelo <player> <ladder> <elo>"));
		}
	}
}
