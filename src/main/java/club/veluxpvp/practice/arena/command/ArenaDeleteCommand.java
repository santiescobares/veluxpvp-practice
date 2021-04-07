package club.veluxpvp.practice.arena.command;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.arena.Arena;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class ArenaDeleteCommand extends ArenaCommand {

	@Command(name = "arena.delete", aliases = {"arena.remove"})
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		Player player = cmd.getPlayer();
		
		if(args.length >= 1) {
			Arena arena = am.getByName(args[0]);
			
			if(arena == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cArena \"" + args[0] + "\" not found!"));
				return;
			}
			
			if(arena.isEnabled()) {
				player.sendMessage(ChatUtil.TRANSLATE("&cYou can't delete an arena while is enabled!"));
				return;
			}
			
			am.getArenas().remove(arena);
			
			player.sendMessage(ChatUtil.TRANSLATE("Arena &b" + arena.getName() + " &cdeleted&f!"));
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /arena delete <arenaName>"));
		}
	}
}
