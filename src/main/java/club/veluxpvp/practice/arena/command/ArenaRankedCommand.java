package club.veluxpvp.practice.arena.command;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.arena.Arena;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class ArenaRankedCommand extends ArenaCommand {

	@Command(name = "arena.ranked")
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		Player player = cmd.getPlayer();
		
		if(args.length >= 2) {
			Arena arena = am.getByName(args[0]);
			boolean ranked = false;
			
			if(arena == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cArena \"" + args[0] + "\" not found!"));
				return;
			}
			
			if(arena.isEnabled()) {
				player.sendMessage(ChatUtil.TRANSLATE("&cYou can't modify an arena while is enabled!"));
				return;
			}
			
			if(args[1].equalsIgnoreCase("true")) {
				ranked = true;
			} else if(args[1].equalsIgnoreCase("false")) {
				ranked = false;
			} else {
				player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /arena ranked <arenaName> <true|false>"));
				return;
			}
			
			arena.setAllowsRanked(ranked);
			
			player.sendMessage(ChatUtil.TRANSLATE("Arena &b" + arena.getName() + " &fis " + (ranked ? "&anow" : "&cno longer") + " &fsupporting ranked matches!"));
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /arena ranked <arenaName> <true|false>"));
		}
	}
}
