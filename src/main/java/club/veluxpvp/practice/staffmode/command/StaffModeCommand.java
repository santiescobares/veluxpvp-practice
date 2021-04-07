package club.veluxpvp.practice.staffmode.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.staffmode.StaffModeManager;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.Preconditions;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class StaffModeCommand {

	public StaffModeCommand() {
		Practice.getInstance().getCommandFramework().registerCommands(this);
	}
	
	@Command(name = "staffmode", aliases = {"staff", "mod", "modmode"}, permission = "practice.command.staffmode", playersOnly = true)
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		Player player = cmd.getPlayer();
		StaffModeManager sm = Practice.getInstance().getStaffModeManager();
		
		if(args.length >= 1) {
			if(!player.hasPermission("practice.command.staffmode.others")) {
				player.sendMessage(ChatUtil.NO_PERMISSION());
				return;
			}
			
			Player target = Bukkit.getPlayer(args[0]);
			
			if(target == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cPlayer \"" + args[0] + "\" not found!"));
				return;
			}
			
			if(!Preconditions.canEnableStaffMode(target)) {
				player.sendMessage(ChatUtil.TRANSLATE("&c" + target.getName() + "'s Staff Mode can't be enabled in their current state!"));
				return;
			}
			
			if(!sm.isInStaffMode(target)) {
				sm.enableStaffMode(target);
				target.sendMessage(ChatUtil.TRANSLATE("Your staff mode has been &aenabled&f!"));
				player.sendMessage(ChatUtil.TRANSLATE("&b" + target.getName() + "&f's staff mode has been &aenabled&f!"));
			} else {
				sm.disableStaffMode(target);
				target.sendMessage(ChatUtil.TRANSLATE("Your staff mode has been &cdisabled&f!"));
				player.sendMessage(ChatUtil.TRANSLATE("&b" + target.getName() + "&f's staff mode has been &cdisabled&f!"));
			}
		} else {
			if(!Preconditions.canEnableStaffMode(player)) {
				player.sendMessage(ChatUtil.TRANSLATE("&cYou can't enable your Staff Mode in your current state!"));
				return;
			}
			
			if(!sm.isInStaffMode(player)) {
				sm.enableStaffMode(player);
				player.sendMessage(ChatUtil.TRANSLATE("Your staff mode has been &aenabled&f!"));
			} else {
				sm.disableStaffMode(player);
				player.sendMessage(ChatUtil.TRANSLATE("Your staff mode has been &cdisabled&f!"));
			}
		}
	}
}
