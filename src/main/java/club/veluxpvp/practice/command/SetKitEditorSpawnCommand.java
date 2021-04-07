package club.veluxpvp.practice.command;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.Serializer;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class SetKitEditorSpawnCommand {

	public SetKitEditorSpawnCommand() {
		Practice.getInstance().getCommandFramework().registerCommands(this);
	}
	
	@Command(name = "setkiteditorspawn", permission = "practice.command.setkiteditorspawn", playersOnly = true)
	public void execute(CommandArgs cmd) {
		Player player = cmd.getPlayer();
		
		Practice.getInstance().getConfig().set("KIT_EDITOR_SPAWN", Serializer.serializeLocation(player.getLocation()));
		Practice.getInstance().saveConfig();
		
		player.sendMessage(ChatUtil.TRANSLATE("&aKit Editor Spawn set!"));
	}
}
