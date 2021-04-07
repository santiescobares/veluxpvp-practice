package club.veluxpvp.practice.staffmode.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.staffmode.StaffModeManager;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class FreezeCommand {

	public FreezeCommand() {
		Practice.getInstance().getCommandFramework().registerCommands(this);
	}
	
	@Command(name = "freeze", aliases = {"ss"}, permission = "practice.command.freeze", playersOnly = true)
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		Player player = cmd.getPlayer();
		StaffModeManager sm = Practice.getInstance().getStaffModeManager();
		
		if(args.length >= 1) {
			Player target = Bukkit.getPlayer(args[0]);
			
			if(target == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cPlayer \"" + args[0] + "\" not found!"));
				return;
			}
			
			if(target.getName().equalsIgnoreCase(player.getName())) {
				player.sendMessage(ChatUtil.TRANSLATE("&cYou can't freeze yourself!"));
				return;
			}
			
			if(!sm.isFreezed(target)) {
				sm.setFreezed(target, true);
				player.sendMessage(ChatUtil.TRANSLATE("You have frozen &b" + target.getName() + "&f!"));
				
				for(Player staff : Bukkit.getOnlinePlayers()) {
					if(!staff.hasPermission("core.freeze.notify")) return;
					staff.sendMessage(ChatUtil.TRANSLATE("&9[S] &b" + target.getName() + " &7has been frozen by &b" + player.getName() + "&7."));
				}
			} else {
				sm.setFreezed(target, false);
				player.sendMessage(ChatUtil.TRANSLATE("You have unfrozen &b" + target.getName() + "&f!"));
				
				for(Player staff : Bukkit.getOnlinePlayers()) {
					if(!staff.hasPermission("core.freeze.notify")) return;
					staff.sendMessage(ChatUtil.TRANSLATE("&9[S] &b" + target.getName() + " &7has been unfrozen by &b" + player.getName() + "&7."));
				}
			}
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /freeze <player>"));
		}
	}
}
