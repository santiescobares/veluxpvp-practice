package club.veluxpvp.practice.kit.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class KitClearPlayerKitsCommand extends KitCommand {

	@SuppressWarnings("deprecation")
	@Command(name = "kit.clearplayerkits", permission = "practice.command.kit")
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		CommandSender sender = cmd.getSender();
		
		if(args.length >= 1) {
			OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
			
			km.getAllPlayerKits(target).stream().forEach(k -> km.getKits().remove(k));
			
			sender.sendMessage(ChatUtil.TRANSLATE("&aAll " + target.getName() + "'s custom kits have been cleared!"));
		} else {
			sender.sendMessage(ChatUtil.TRANSLATE("&cUsage: /kit clearplayerkits <player>"));
		}
	}
}
