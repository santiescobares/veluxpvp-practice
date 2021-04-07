package club.veluxpvp.practice.arena.command;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.arena.Arena;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class ArenaCreateCommand extends ArenaCommand {

	@Command(name = "arena.create")
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		Player player = cmd.getPlayer();
		
		if(args.length >= 1) {
			Arena arena = am.getByName(args[0]);
			
			if(arena != null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cArena \"" + arena.getName() + "\" is already exists!"));
				return;
			}
			
			arena = new Arena(args[0]);
			am.getArenas().add(arena);
			
			player.sendMessage(ChatUtil.TRANSLATE("Arena &b" + arena.getName() + " &acreated&f!"));
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /arena create <arenaName>"));
		}
	}
}
