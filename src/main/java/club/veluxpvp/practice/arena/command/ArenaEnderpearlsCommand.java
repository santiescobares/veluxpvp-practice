package club.veluxpvp.practice.arena.command;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.arena.Arena;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class ArenaEnderpearlsCommand extends ArenaCommand {

	@Command(name = "arena.enderpearls")
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		Player player = cmd.getPlayer();
		
		if(args.length >= 2) {
			Arena arena = am.getByName(args[0]);
			boolean enderpearls = false;
			
			if(arena == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cArena \"" + args[0] + "\" not found!"));
				return;
			}
			
			if(arena.isEnabled()) {
				player.sendMessage(ChatUtil.TRANSLATE("&cYou can't modify an arena while is enabled!"));
				return;
			}
			
			if(args[1].equalsIgnoreCase("true")) {
				enderpearls = true;
			} else if(args[1].equalsIgnoreCase("false")) {
				enderpearls = false;
			} else {
				player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /arena enderpearls <arenaName> <true|false>"));
				return;
			}
			
			arena.setEnderpearls(enderpearls);
			
			player.sendMessage(ChatUtil.TRANSLATE("Enderpearls are " + (enderpearls ? "&anow" : "&cno longer") + " &fenabled on arena &b" + arena.getName() + "&f!"));
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /arena enderpearls <arenaName> <true|false>"));
		}
	}
}
