package club.veluxpvp.practice.arena.command;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import club.veluxpvp.practice.arena.Arena;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class ArenaIconCommand extends ArenaCommand {

	@Command(name = "arena.icon")
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		Player player = cmd.getPlayer();
		
		if(args.length >= 1) {
			Arena arena = am.getByName(args[0]);
			
			if(arena == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cArena \"" + args[0] + "\" not found!"));
				return;
			}
			
			ItemStack icon = player.getItemInHand();
			
			if(icon == null || icon.getType() == Material.AIR) {
				player.sendMessage(ChatUtil.TRANSLATE("&cYou must hold an item!"));
				return;
			}
			
			arena.setIcon(icon);
			
			player.sendMessage(ChatUtil.TRANSLATE("Icon of arena &b" + arena.getName() + " &aset &fto &b" + icon.getType().name() + "&f!"));
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /arena icon <arenaName>"));
		}
	}
}
