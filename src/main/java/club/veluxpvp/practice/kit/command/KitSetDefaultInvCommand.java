package club.veluxpvp.practice.kit.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.kit.Kit;
import club.veluxpvp.practice.kit.KitType;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class KitSetDefaultInvCommand extends KitCommand {

	@Command(name = "kit.setdefaultinv", permission = "practice.command.kit", playersOnly = true)
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		Player player = cmd.getPlayer();
		
		if(args.length >= 1) {
			KitType kitType = KitType.getByName(args[0]);
			
			if(kitType == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cKit Type \"" + args[0] + "\" not found!"));
				return;
			}
			
			player.updateInventory();
			
			Bukkit.getScheduler().runTaskLater(Practice.getInstance(), () -> {
				boolean isNew = false;
				Kit defaultKit = km.getDefaultKit(kitType);
				
				if(defaultKit == null) {
					defaultKit = new Kit(null, kitType, true, 1);
					isNew = true;
				}
				defaultKit.setContents(player.getInventory().getContents());
				defaultKit.setArmorContents(player.getInventory().getArmorContents());
				
				if(isNew) km.getKits().add(defaultKit);
				player.sendMessage(ChatUtil.TRANSLATE("&b" + defaultKit.getType().name + " &fkit contents &aupdated&f!"));
			}, 2L);
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /kit setdefaultinv <kitType>"));
		}
	}
}
