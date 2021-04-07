package club.veluxpvp.practice.arena.command;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.ArenaManager;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class ArenaCommand {

	protected ArenaManager am;
	
	public ArenaCommand() {
		this.am = Practice.getInstance().getArenaManager();
		Practice.getInstance().getCommandFramework().registerCommands(this);
	}
	
	@Command(name = "arena", permission = "practice.command.arena", playersOnly = true)
	public void execute(CommandArgs cmd) {
		sendHelpMessage(cmd.getPlayer());
	}
	
	private void sendHelpMessage(Player player) {
		player.sendMessage(ChatUtil.SHORTER_LINE());
		player.sendMessage(ChatUtil.TRANSLATE("&b&lArena Commands"));
		player.sendMessage(ChatUtil.SHORTER_LINE());
		player.sendMessage(ChatUtil.TRANSLATE("&bGeneral Commands:"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/arena create <arenaName>"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/arena delete <arenaName>"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/arena corner1 <arenaName>"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/arena corner2 <arenaName>"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/arena spectatorsspawn <arenaName>"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/arena eventsspawn <arenaName>"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/arena addladder <arenaName> <ladder>"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/arena removeladder <arenaName> <ladder>"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/arena icon <arenaName>"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/arena bounds <arenaName>"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/arena enable <arenaName>"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/arena disable <arenaName>"));
		player.sendMessage(ChatUtil.TRANSLATE("&bArena Options:"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/arena ranked <arenaName> <true|false>"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/arena parties <arenaName> <true|false>"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/arena tournaments <arenaName> <true|false>"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/arena events <arenaName> <true|false>"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/arena enderpearls <arenaName> <true|false>"));
		player.sendMessage(ChatUtil.TRANSLATE("&bParkour Commands:"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/arena checkpoints <arenaName>"));
		player.sendMessage(ChatUtil.TRANSLATE("&bBridges Commands:"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/arena claim <arenaName> <red|blue>"));
		player.sendMessage(ChatUtil.TRANSLATE("&bOther Commands:"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/arena maxbuildheight <arenaName> <maxBuildHeight>"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/arena info <arenaName>"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/arena list"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/arena saveall"));
		player.sendMessage(ChatUtil.SHORTER_LINE());
	}
}
