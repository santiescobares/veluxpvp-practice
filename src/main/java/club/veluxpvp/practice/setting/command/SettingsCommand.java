package club.veluxpvp.practice.setting.command;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.setting.menu.SettingsMenu;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class SettingsCommand {

	public SettingsCommand() {
		Practice.getInstance().getCommandFramework().registerCommands(this);
	}
	
	@Command(name = "settings", aliases = {"setts", "options"}, playersOnly = true)
	public void execute(CommandArgs cmd) {
		Player player = cmd.getPlayer();
		
		SettingsMenu menu = new SettingsMenu(player);
		menu.openMenu(player);
	}
}
