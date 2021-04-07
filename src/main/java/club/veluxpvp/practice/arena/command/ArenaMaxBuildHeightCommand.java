package club.veluxpvp.practice.arena.command;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.arena.Arena;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class ArenaMaxBuildHeightCommand extends ArenaCommand {

	@Command(name = "arena.maxbuildheight")
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		Player player = cmd.getPlayer();
		
		if(args.length >= 2) {
			Arena arena = am.getByName(args[0]);
			int maxBuildHeight = 0;
			
			if(arena == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cArena \"" + args[0] + "\" not found!"));
				return;
			}
			
			if(arena.isEnabled()) {
				player.sendMessage(ChatUtil.TRANSLATE("&cYou can't modify an arena while is enabled!"));
				return;
			}
			
			try {
				maxBuildHeight = Integer.valueOf(args[1]);
				
				if(maxBuildHeight < 0 || maxBuildHeight > 256) {
					player.sendMessage(ChatUtil.TRANSLATE("&cThe maximum must be a number between 0 and 256!"));
					return;
				}
				
				arena.setMaxBuildHeight(maxBuildHeight);
				
				player.sendMessage(ChatUtil.TRANSLATE("Arena &b" + arena.getName() + "&f's maximum build height &aset &fto &b" + maxBuildHeight + "&f!"));
			} catch(NumberFormatException e) {
				player.sendMessage(ChatUtil.TRANSLATE("&cYou must enter a valid number!"));
			}
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /arena maxbuildheight <arenaName> <maxBuildHeight>"));
		}
	}
}
