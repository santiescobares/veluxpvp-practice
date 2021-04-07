package club.veluxpvp.practice.arena.command;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.arena.Arena;
import club.veluxpvp.practice.arena.listener.ArenaCheckpointsListener;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class ArenaCheckpointsCommand extends ArenaCommand {

	@Command(name = "arena.checkpoints")
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
			
			if(ArenaCheckpointsListener.makingCheckpoints.containsKey(player.getUniqueId())) {
				Arena a = ArenaCheckpointsListener.makingCheckpoints.get(player.getUniqueId());
				
				if(!arena.getName().equalsIgnoreCase(a.getName())) {
					player.sendMessage(ChatUtil.TRANSLATE("&cYou are already making a different arena checkpoints!"));
					return;
				}
				
				player.sendMessage(ChatUtil.TRANSLATE("Checkpoints task of arena &b" + arena.getName() + " &cfinished&f!"));
				
				ArenaCheckpointsListener.makingCheckpoints.remove(player.getUniqueId());
				return;
			}
			
			ArenaCheckpointsListener.makingCheckpoints.put(player.getUniqueId(), arena);
			
			player.sendMessage(ChatUtil.TRANSLATE("You are now making checkpoints for arena &b" + arena.getName() + "&f. Checkpoints material is &bIron Plate&f. To finish this task, just execute again &b/arena checkpoints " + arena.getName() + "&f."));
			return;
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /arena checkpoints <arenaName>"));
		}
	}
}
