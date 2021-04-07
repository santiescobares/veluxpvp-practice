package club.veluxpvp.practice.arena.command;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.arena.Arena;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class ArenaEnableCommand extends ArenaCommand {

	@Command(name = "arena.enable")
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
				player.sendMessage(ChatUtil.TRANSLATE("&cThis arena is already enabled!"));
				return;
			}
			
			if(!arena.canEnable()) {
				player.sendMessage(ChatUtil.TRANSLATE("&cMake sure to set all arena stuff before enabling it!"));
				return;
			}
			
			arena.setEnabled(true);
			
			player.sendMessage(ChatUtil.TRANSLATE("Arena &b" + arena.getName() + " &aenabled&f!"));
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /arena enable <arenaName>"));
		}
	}
}
