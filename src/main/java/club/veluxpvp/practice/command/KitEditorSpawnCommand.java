package club.veluxpvp.practice.command;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.PlayerUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class KitEditorSpawnCommand {

	public KitEditorSpawnCommand() {
		Practice.getInstance().getCommandFramework().registerCommands(this);
	}
	
	@Command(name = "kiteditorspawn", permission = "practice.command.kiteditorspawn", playersOnly = true)
	public void execute(CommandArgs cmd) {
		Player player = cmd.getPlayer();
		
		if(PlayerUtil.sendToKitEditorSpawn(player)) {
			player.sendMessage(ChatUtil.TRANSLATE("&aTeleported to the kit editor spawn!"));
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cThe kit editor spawn is not definied yet!"));
		}
	}
}
