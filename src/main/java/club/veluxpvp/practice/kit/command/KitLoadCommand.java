package club.veluxpvp.practice.kit.command;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.kit.Kit;
import club.veluxpvp.practice.kit.KitType;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class KitLoadCommand extends KitCommand {

	@Command(name = "kit.load", permission = "practice.command.kit", playersOnly = true)
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		Player player = cmd.getPlayer();
		
		if(args.length >= 1) {
			KitType kitType = KitType.getByName(args[0]);
			
			if(kitType == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cKit Type \"" + args[0] + "\" not found!"));
				return;
			}
			
			Kit defaultKit = km.getDefaultKit(kitType);
			
			if(defaultKit != null) defaultKit.apply(player, true);
			
			player.sendMessage(ChatUtil.TRANSLATE("&b" + defaultKit.getType().name + " &fkit contents &aloaded&f!"));
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /kit load <kitType>"));
		}
	}
}
