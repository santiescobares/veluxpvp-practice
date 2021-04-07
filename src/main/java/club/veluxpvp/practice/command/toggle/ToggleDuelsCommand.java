package club.veluxpvp.practice.command.toggle;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.profile.Profile;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class ToggleDuelsCommand {

	public ToggleDuelsCommand() {
		Practice.getInstance().getCommandFramework().registerCommands(this);
	}
	
	@Command(name = "toggleduels", aliases = {"td"}, playersOnly = true)
	public void execute(CommandArgs cmd) {
		Player player = cmd.getPlayer();
		Profile p = Practice.getInstance().getProfileManager().getProfile(player);
		
		if(p.isAllowDuels()) {
			p.setAllowDuels(false);
			player.sendMessage(ChatUtil.TRANSLATE("&cYou are no longer allowing duels!"));
		} else {
			p.setAllowDuels(true);
			player.sendMessage(ChatUtil.TRANSLATE("&aYou are now allowing duels!"));
		}
	}
}
