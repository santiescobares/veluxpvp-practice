package club.veluxpvp.practice.command;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.PlayerUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class SpawnCommand {

	public SpawnCommand() {
		Practice.getInstance().getCommandFramework().registerCommands(this);
	}
	
	@Command(name = "spawn", permission = "practice.command.spawn", playersOnly = true)
	public void execute(CommandArgs cmd) {
		Player player = cmd.getPlayer();
		
		if(PlayerUtil.sendToSpawn(player)) {
			player.sendMessage(ChatUtil.TRANSLATE("&aTeleported to the spawn!"));
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cThe spawn is not definied yet!"));
		}
	}
}
