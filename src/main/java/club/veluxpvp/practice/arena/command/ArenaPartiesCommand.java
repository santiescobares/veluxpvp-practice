package club.veluxpvp.practice.arena.command;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.arena.Arena;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class ArenaPartiesCommand extends ArenaCommand {

	@Command(name = "arena.parties")
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		Player player = cmd.getPlayer();
		
		if(args.length >= 2) {
			Arena arena = am.getByName(args[0]);
			boolean parties = false;
			
			if(arena == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cArena \"" + args[0] + "\" not found!"));
				return;
			}
			
			if(arena.isEnabled()) {
				player.sendMessage(ChatUtil.TRANSLATE("&cYou can't modify an arena while is enabled!"));
				return;
			}
			
			if(args[1].equalsIgnoreCase("true")) {
				parties = true;
			} else if(args[1].equalsIgnoreCase("false")) {
				parties = false;
			} else {
				player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /arena parties <arenaName> <true|false>"));
				return;
			}
			
			arena.setAllowsParties(parties);
			
			player.sendMessage(ChatUtil.TRANSLATE("Arena &b" + arena.getName() + " &fis " + (parties ? "&anow" : "&cno longer") + " &fsupporting parties!"));
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /arena parties <arenaName> <true|false>"));
		}
	}
}
