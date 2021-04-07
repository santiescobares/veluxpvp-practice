package club.veluxpvp.practice.command.toggle;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.profile.Profile;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class ToggleSimilarPingCommand {

	public ToggleSimilarPingCommand() {
		Practice.getInstance().getCommandFramework().registerCommands(this);
	}
	
	@Command(name = "togglesimilarping", aliases = {"tsp"}, permission = "practice.command.togglesimilarping", playersOnly = true)
	public void execute(CommandArgs cmd) {
		Player player = cmd.getPlayer();
		Profile p = Practice.getInstance().getProfileManager().getProfile(player);
		
		if(p.isRankedSimilarPing()) {
			p.setRankedSimilarPing(false);
			player.sendMessage(ChatUtil.TRANSLATE("&cYou will no longer search opponents with a similar ping to you!"));
		} else {
			p.setRankedSimilarPing(true);
			player.sendMessage(ChatUtil.TRANSLATE("&aYou will now search opponents with a similar ping to you!"));
		}
	}
}
