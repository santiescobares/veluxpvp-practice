package club.veluxpvp.practice.arena.command;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.arena.Arena;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class ArenaInfoCommand extends ArenaCommand {

	@SuppressWarnings("deprecation")
	@Command(name = "arena.info", aliases = {"arena.information"})
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		Player player = cmd.getPlayer();
		
		if(args.length >= 1) {
			Arena arena = am.getByName(args[0]);
			
			if(arena == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cArena \"" + args[0] + "\" not found!"));
				return;
			}
			
			player.sendMessage(ChatUtil.LINE());
			player.sendMessage(ChatUtil.TRANSLATE("&b&l" + arena.getName() + "'s Arena Information"));
			player.sendMessage(ChatUtil.LINE());
			player.sendMessage(ChatUtil.TRANSLATE(" &7* &fIcon&7: &b" + (arena.getIcon() == null ? "None" : arena.getIcon().getType().name() + ":" + arena.getIcon().getData().getData())));
			player.sendMessage(ChatUtil.TRANSLATE(" &7* &fEnabled&7: " + (arena.isEnabled() ? "&aYes" : "&cNo")));
			player.sendMessage(ChatUtil.TRANSLATE(" &7* &fMax Build Height&7: &b" + arena.getMaxBuildHeight()));
			player.sendMessage(ChatUtil.TRANSLATE("&bLocations:"));
			player.sendMessage(ChatUtil.TRANSLATE(" &7* &fCorner 1&7: &b" + (arena.getCorner1() == null ? "None" : arena.getCorner1().getBlockX() + ", " + arena.getCorner1().getBlockY() + ", " + arena.getCorner1().getBlockZ() + " (" + arena.getCorner1().getWorld().getName() + ")")));
			player.sendMessage(ChatUtil.TRANSLATE(" &7* &fCorner 2&7: &b" + (arena.getCorner2() == null ? "None" : arena.getCorner2().getBlockX() + ", " + arena.getCorner2().getBlockY() + ", " + arena.getCorner2().getBlockZ() + " (" + arena.getCorner2().getWorld().getName() + ")")));
			player.sendMessage(ChatUtil.TRANSLATE(" &7* &fSpectators Spawn&7: &b" + (arena.getSpectatorsSpawn() == null ? "None" : arena.getSpectatorsSpawn().getBlockX() + ", " + arena.getSpectatorsSpawn().getBlockY() + ", " + arena.getSpectatorsSpawn().getBlockZ() + " (" + arena.getSpectatorsSpawn().getWorld().getName() + ")")));
			player.sendMessage(ChatUtil.TRANSLATE(" &7* &fEvents Spawn&7: &b" + (arena.getEventsSpawn() == null ? "None" : arena.getEventsSpawn().getBlockX() + ", " + arena.getEventsSpawn().getBlockY() + ", " + arena.getEventsSpawn().getBlockZ() + " (" + arena.getEventsSpawn().getWorld().getName() + ")")));
			player.sendMessage(ChatUtil.TRANSLATE("&bOptions:"));
			player.sendMessage(ChatUtil.TRANSLATE(" &7* &fAllows Ranked&7: " + (arena.isAllowsRanked() ? "&aYes" : "&cNo")));
			player.sendMessage(ChatUtil.TRANSLATE(" &7* &fAllows Parties&7: " + (arena.isAllowsParties() ? "&aYes" : "&cNo")));
			player.sendMessage(ChatUtil.TRANSLATE(" &7* &fAllows Tournaments&7: " + (arena.isAllowsTournaments() ? "&aYes" : "&cNo")));
			player.sendMessage(ChatUtil.TRANSLATE(" &7* &fEnderpearls&7: " + (arena.isEnderpearls() ? "&aEnabled" : "&cDisabled")));
			player.sendMessage(ChatUtil.TRANSLATE("&bSupported Ladders: &7(" + arena.getSupportedLadders().size() + ")"));
			
			for(Ladder l : arena.getSupportedLadders()) {
				player.sendMessage(ChatUtil.TRANSLATE(" &7* &f" + l.name));
			}
			
			player.sendMessage(ChatUtil.LINE());
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /arena info <arenaName>"));
		}
	}
}
