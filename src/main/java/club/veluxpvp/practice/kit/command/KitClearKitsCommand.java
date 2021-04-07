package club.veluxpvp.practice.kit.command;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class KitClearKitsCommand extends KitCommand {

	@Command(name = "kit.clearkits", playersOnly = true)
	public void execute(CommandArgs cmd) {
		Player player = cmd.getPlayer();
		
		km.getAllPlayerKits(player).stream().forEach(k -> km.getKits().remove(k));
		
		player.sendMessage(ChatUtil.TRANSLATE("&aAll your custom kits have been cleared!"));
	}
}
