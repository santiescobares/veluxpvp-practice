package club.veluxpvp.practice.arena.command;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.arena.Arena;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class ArenaRemoveLadderCommand extends ArenaCommand {

	@Command(name = "arena.removeladder")
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		Player player = cmd.getPlayer();
		
		if(args.length >= 2) {
			Arena arena = am.getByName(args[0]);
			Ladder ladder = Ladder.getByName(args[1]);
			
			if(arena == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cArena \"" + args[0] + "\" not found!"));
				return;
			}
			
			if(arena.isEnabled()) {
				player.sendMessage(ChatUtil.TRANSLATE("&cYou can't modify an arena while is enabled!"));
				return;
			}
			
			if(ladder == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cLadder \"" + args[1] + "\" not found!"));
				return;
			}
			
			if(!arena.removeSupportedLadder(ladder)) {
				player.sendMessage(ChatUtil.TRANSLATE("&cThis arena doesn't contains that ladder!"));
				return;
			}
			
			player.sendMessage(ChatUtil.TRANSLATE("Ladder &b" + ladder.name + " &cremoved &ffrom arena &b" + arena.getName() + "&f!"));
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /arena removeladder <arenaName> <ladder>"));
		}
	}
}
