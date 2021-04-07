package club.veluxpvp.practice.kit.command;

import org.bukkit.command.CommandSender;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.kit.KitManager;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class KitCommand {

	protected KitManager km;
	
	public KitCommand() {
		this.km = Practice.getInstance().getKitManager();
		Practice.getInstance().getCommandFramework().registerCommands(this);
	}
	
	@Command(name = "kit")
	public void execute(CommandArgs cmd) {
		sendHelpMessage(cmd.getSender());
	}
	
	private void sendHelpMessage(CommandSender sender) {
		sender.sendMessage(ChatUtil.SHORTER_LINE());
		sender.sendMessage(ChatUtil.TRANSLATE("&b&lKit Commands"));
		sender.sendMessage(ChatUtil.SHORTER_LINE());
		sender.sendMessage(ChatUtil.TRANSLATE(" &7* &f/kit clearkits"));
		
		if(sender.hasPermission("practice.command.kit")) {
			sender.sendMessage(ChatUtil.TRANSLATE(" &7* &f/kit load <kitType>"));
			sender.sendMessage(ChatUtil.TRANSLATE(" &7* &f/kit setdefaultinv <kitType>"));
			sender.sendMessage(ChatUtil.TRANSLATE(" &7* &f/kit clearplayerkits <player>"));
		}
		
		sender.sendMessage(ChatUtil.SHORTER_LINE());
	}
}
