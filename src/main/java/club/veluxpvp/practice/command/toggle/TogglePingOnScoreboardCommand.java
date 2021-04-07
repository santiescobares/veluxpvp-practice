package club.veluxpvp.practice.command.toggle;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.profile.Profile;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class TogglePingOnScoreboardCommand {

	public TogglePingOnScoreboardCommand() {
		Practice.getInstance().getCommandFramework().registerCommands(this);
	}
	
	@Command(name = "togglepingonscoreboard", aliases = {"tposb", "tpsb"}, playersOnly = true)
	public void execute(CommandArgs cmd) {
		Player player = cmd.getPlayer();
		Profile p = Practice.getInstance().getProfileManager().getProfile(player);
		
		if(p.isPingOnScoreboard()) {
			p.setPingOnScoreboard(false);
			player.sendMessage(ChatUtil.TRANSLATE("&cYou are no longer showing your and opponent's ping in the scoreboard!"));
		} else {
			p.setPingOnScoreboard(true);
			player.sendMessage(ChatUtil.TRANSLATE("&aYou are now showing your and opponent's ping in the scoreboard!"));
		}
	}
}
