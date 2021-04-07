package club.veluxpvp.practice.command;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.leaderboard.menu.LeaderboardMenu;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class LeaderboardCommand {

	public LeaderboardCommand() {
		Practice.getInstance().getCommandFramework().registerCommands(this);
	}
	
	@Command(name = "leaderboard", aliases = {"leaderboards", "lb"}, playersOnly = true)
	public void execute(CommandArgs cmd) {
		Player player = cmd.getPlayer();
		
		LeaderboardMenu menu = new LeaderboardMenu(player);
		menu.openMenu(player);
	}
}
