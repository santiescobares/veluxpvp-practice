package club.veluxpvp.practice.command.toggle;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.profile.Profile;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class ToggleSpectatorsCommand {

	public ToggleSpectatorsCommand() {
		Practice.getInstance().getCommandFramework().registerCommands(this);
	}
	
	@Command(name = "togglespectators", aliases = {"ts"}, playersOnly = true)
	public void execute(CommandArgs cmd) {
		Player player = cmd.getPlayer();
		Profile p = Practice.getInstance().getProfileManager().getProfile(player);
		
		if(p.isAllowSpectators()) {
			p.setAllowSpectators(false);
			player.sendMessage(ChatUtil.TRANSLATE("&cYou are no longer allowing spectators!"));
		} else {
			p.setAllowSpectators(true);
			player.sendMessage(ChatUtil.TRANSLATE("&aYou are now allowing spectators!"));
		}
	}
}
