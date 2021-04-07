package club.veluxpvp.practice.arena.command;

import java.util.ConcurrentModificationException;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Arena;
import club.veluxpvp.practice.match.MatchEndReason;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class ArenaDisableCommand extends ArenaCommand {

	@Command(name = "arena.disable")
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		Player player = cmd.getPlayer();
		
		if(args.length >= 1) {
			Arena arena = am.getByName(args[0]);
			
			if(arena == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cArena \"" + args[0] + "\" not found!"));
				return;
			}
			
			if(!arena.isEnabled()) {
				player.sendMessage(ChatUtil.TRANSLATE("&cThis arena is not enabled!"));
				return;
			}
			
			arena.setEnabled(false);
			
			try {
				Practice.getInstance().getMatchManager().getMatches()
				.stream()
				.filter(m -> m.getArena() == arena)
				.forEach(m -> {
					m.getPlayers().stream().forEach(p -> p.sendMessage(ChatUtil.TRANSLATE("&cYour current match has been cancelled due to the match's arena was disabled!")));
						m.finish(MatchEndReason.CANCELLED);
				});	
			} catch(ConcurrentModificationException ignored) {}

			player.sendMessage(ChatUtil.TRANSLATE("Arena &b" + arena.getName() + " &cdisabled&f!"));
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /arena disable <arenaName>"));
		}
	}
}
