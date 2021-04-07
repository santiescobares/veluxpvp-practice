package club.veluxpvp.practice.event.command;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.event.menu.HostMenu;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class HostCommand {

	public HostCommand() {
		Practice.getInstance().getCommandFramework().registerCommands(this);
	}
	
	@Command(name = "host", permission = "practice.command.host", playersOnly = true)
	public void execute(CommandArgs cmd) {
		Player player = cmd.getPlayer();
		
		if(Practice.getInstance().getEventManager().getActiveEvent() != null) {
			player.sendMessage(ChatUtil.TRANSLATE("&cThere is already an active event!"));
			return;
		}
		
		HostMenu menu = new HostMenu(player);
		menu.openMenu(player);
	}
}
