package club.veluxpvp.practice.arena.command;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class ArenaSaveAllCommand extends ArenaCommand {

	@Command(name = "arena.saveall")
	public void execute(CommandArgs cmd) {
		Player player = cmd.getPlayer();
		
		am.saveArenas();
		
		player.sendMessage(ChatUtil.TRANSLATE("All arenas have been &asaved &fin &barenas.yml&f!"));
	}
}
