package club.veluxpvp.practice.arena.command;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.arena.Arena;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class ArenaListCommand extends ArenaCommand {

	@Command(name = "arena.list")
	public void execute(CommandArgs cmd) {
		Player player = cmd.getPlayer();
		
		player.sendMessage(ChatUtil.SHORTER_LINE());
		player.sendMessage(ChatUtil.TRANSLATE("&b&lArenas List &7(" + am.getArenas().size() + ")"));
		player.sendMessage(ChatUtil.SHORTER_LINE());
		
		if(am.getArenas().size() > 0) {
			for(Arena a : am.getArenas()) {
				player.sendMessage(ChatUtil.TRANSLATE(" &7* &f" + a.getName() + " &7- " + (a.isEnabled() ? "&aEnabled" : "&cDisabled")));
			}
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cThere are no arenas created yet!"));
		}
		
		player.sendMessage(ChatUtil.SHORTER_LINE());
	}
}
