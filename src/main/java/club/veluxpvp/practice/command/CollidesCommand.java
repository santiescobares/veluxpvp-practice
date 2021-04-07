package club.veluxpvp.practice.command;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class CollidesCommand {

	public CollidesCommand() {
		Practice.getInstance().getCommandFramework().registerCommands(this);
	}
	
	@Command(name = "collides", permission = "practice.command.collides")
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		Player player = cmd.getPlayer();
		
		if(args.length >= 1) {
			Player target = Bukkit.getPlayer(args[0]);
			
			if(target == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cPlayer \"" + args[0] + "\" not found!"));
				return;
			}
			
			CraftPlayer targetCP = (CraftPlayer) target;
			
			if(!targetCP.getHandle().collidesWithEntities) {
				targetCP.getHandle().collidesWithEntities = true;
				player.sendMessage(ChatUtil.TRANSLATE("&a" + target.getName() + " is now colliding with entities!"));
			} else {
				targetCP.getHandle().collidesWithEntities = false;
				player.sendMessage(ChatUtil.TRANSLATE("&a" + target.getName() + " is no longer colliding with entities!"));
			}
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /collides <player>"));
		}
	}
}
