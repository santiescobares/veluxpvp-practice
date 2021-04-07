package club.veluxpvp.practice.arena.command;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.arena.Arena;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class ArenaEventsSpawnCommand extends ArenaCommand {

	@Command(name = "arena.eventsspawn")
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
				player.sendMessage(ChatUtil.TRANSLATE("&cYou can't modify an arena while is enabled!"));
				return;
			}
			
			arena.setEventsSpawn(player.getLocation());
			
			player.sendMessage(ChatUtil.TRANSLATE("Events spawn of arena &b" + arena.getName() + " &asetted &fon your position!"));
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /arena eventsspawn <arenaName>"));
		}
	}
}
