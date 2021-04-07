package club.veluxpvp.practice.command.toggle;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.profile.Profile;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class ToggleTournamentMessagesCommand {

	public ToggleTournamentMessagesCommand() {
		Practice.getInstance().getCommandFramework().registerCommands(this);
	}
	
	@Command(name = "toggletournamentmessages", aliases = {"ttm"}, playersOnly = true)
	public void execute(CommandArgs cmd) {
		Player player = cmd.getPlayer();
		Profile p = Practice.getInstance().getProfileManager().getProfile(player);
		
		if(p.isTournamentMessages()) {
			p.setTournamentMessages(false);
			player.sendMessage(ChatUtil.TRANSLATE("&cYou are no longer showing tournament join/elimination messages!"));
		} else {
			p.setTournamentMessages(true);
			player.sendMessage(ChatUtil.TRANSLATE("&aYou are now showing tournament join/elimination messages!"));
		}
	}
}
