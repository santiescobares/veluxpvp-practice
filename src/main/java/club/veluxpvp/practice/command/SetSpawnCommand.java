package club.veluxpvp.practice.command;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.Serializer;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class SetSpawnCommand {

	public SetSpawnCommand() {
		Practice.getInstance().getCommandFramework().registerCommands(this);
	}
	
	@Command(name = "setspawn", permission = "practice.command.setspawn", playersOnly = true)
	public void execute(CommandArgs cmd) {
		Player player = cmd.getPlayer();
		
		Practice.getInstance().getConfig().set("SPAWN", Serializer.serializeLocation(player.getLocation()));
		Practice.getInstance().saveConfig();
		
		player.sendMessage(ChatUtil.TRANSLATE("&aSpawn set!"));
	}
}
