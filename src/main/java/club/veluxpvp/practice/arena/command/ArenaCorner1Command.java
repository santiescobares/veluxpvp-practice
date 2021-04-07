package club.veluxpvp.practice.arena.command;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.arena.Arena;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class ArenaCorner1Command extends ArenaCommand {

	@Command(name = "arena.corner1")
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
			
			arena.setCorner1(player.getLocation());
			
			player.sendMessage(ChatUtil.TRANSLATE("First corner of arena &b" + arena.getName() + " &aset &fon your position!"));
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /arena corner1 <arenaName>"));
		}
	}
}
