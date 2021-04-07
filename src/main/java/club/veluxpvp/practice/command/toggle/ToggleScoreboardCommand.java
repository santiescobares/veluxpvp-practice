package club.veluxpvp.practice.command.toggle;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.profile.Profile;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.PlayerUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class ToggleScoreboardCommand {

	public ToggleScoreboardCommand() {
		Practice.getInstance().getCommandFramework().registerCommands(this);
	}
	
	@Command(name = "togglescoreboard", aliases = {"tsb"}, playersOnly = true)
	public void execute(CommandArgs cmd) {
		Player player = cmd.getPlayer();
		Profile p = Practice.getInstance().getProfileManager().getProfile(player);
		
		if(p.isScoreboard()) {
			p.setScoreboard(false);
			player.sendMessage(ChatUtil.TRANSLATE("&cYou are no longer showing the scoreboard!"));
		} else {
			p.setScoreboard(true);
			player.sendMessage(ChatUtil.TRANSLATE("&aYou are now showing the scoreboard!"));
		}
		
		PlayerUtil.updateScoreboard(player);
	}
}
