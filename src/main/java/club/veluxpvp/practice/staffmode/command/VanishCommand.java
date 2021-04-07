package club.veluxpvp.practice.staffmode.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.staffmode.StaffModeManager;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.Preconditions;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class VanishCommand {

	public VanishCommand() {
		Practice.getInstance().getCommandFramework().registerCommands(this);
	}
	
	@Command(name = "vanish", aliases = {"v"}, permission = "practice.command.vanish", playersOnly = true)
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
			
			if(!Preconditions.canEnableStaffMode(target)) {
				player.sendMessage(ChatUtil.TRANSLATE("&c" + target.getName() + "'s vanish mode can't be enabled in their current state!"));
				return;
			}
			
			if(!sm.isVanished(target)) {
				sm.setVanished(target, true);
				target.sendMessage(ChatUtil.TRANSLATE("Your vanish mode has been &aenabled&f!"));
				player.sendMessage(ChatUtil.TRANSLATE("&b" + target.getName() + "&f's vanish mode has been &aenabled&f!"));
			} else {
				sm.setVanished(target, false);
				target.sendMessage(ChatUtil.TRANSLATE("Your vanish mode has been &cdisabled&f!"));
				player.sendMessage(ChatUtil.TRANSLATE("&b" + target.getName() + "&f's vanish mode has been &cdisabled&f!"));
			}
		} else {
			if(!Preconditions.canEnableStaffMode(player)) {
				player.sendMessage(ChatUtil.TRANSLATE("&cYou can't enable your vanish mode in your current state!"));
				return;
			}
			
			if(!sm.isVanished(player)) {
				sm.setVanished(player, true);
				player.sendMessage(ChatUtil.TRANSLATE("Your vanish mode has been &aenabled&f!"));
			} else {
				sm.setVanished(player, false);
				player.sendMessage(ChatUtil.TRANSLATE("Your vanish mode has been &cdisabled&f!"));
			}
		}
	}
}
