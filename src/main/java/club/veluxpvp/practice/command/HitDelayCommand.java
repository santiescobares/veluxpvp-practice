package club.veluxpvp.practice.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class HitDelayCommand {

	public HitDelayCommand() {
		Practice.getInstance().getCommandFramework().registerCommands(this);
	}
	
	@Command(name = "hitdelay", permission = "practice.command.hitdelay", playersOnly = true)
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		Player player = cmd.getPlayer();
		
		if(args.length >= 2) {
			Player target = Bukkit.getPlayer(args[0]);
			int hitDelay = 0;
			
			if(target == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cPlayer \"" + args[0] + "\" not found!"));
				return;
			}
			
			try {
				hitDelay = Integer.valueOf(args[1]);
				target.setMaximumNoDamageTicks(hitDelay);
				player.sendMessage(ChatUtil.TRANSLATE("&a" + target.getName() + "'s hit delay set to " + hitDelay + "!"));
			} catch(NumberFormatException e) {
				player.sendMessage(ChatUtil.TRANSLATE("&cYou must enter a valid number!"));
				return;
			}
		} else if(args.length == 1) {
			Player target = Bukkit.getPlayer(args[0]);
			
			if(target == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cPlayer \"" + args[0] + "\" not found!"));
				return;
			}
			
			player.sendMessage(ChatUtil.TRANSLATE("&a" + target.getName() + "'s hit delay is " + target.getMaximumNoDamageTicks() + "!"));
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /hitdelay <player> [hitDelay]"));
		}
	}
}
