package club.veluxpvp.practice.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.Preconditions;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class SpectateCommand {

	public SpectateCommand() {
		Practice.getInstance().getCommandFramework().registerCommands(this);
	}
	
	@Command(name = "spectate", aliases = {"spec"}, playersOnly = true)
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		Player player = cmd.getPlayer();
		
		if(args.length >= 1) {
			Player target = Bukkit.getPlayer(args[0]);
			
			if(target == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cPlayer \"" + args[0] + "\" not found!"));
				return;
			}
			
			if(target.getName().equalsIgnoreCase(player.getName())) {
				player.sendMessage(ChatUtil.TRANSLATE("&cYou can't spectate yourself!"));
				return;
			}
			
			if(!Preconditions.canSpectate(player, target)) return;
			
			Match playerMatch = Practice.getInstance().getMatchManager().getPlayerMatch(player);
			Match targetMatch = Practice.getInstance().getMatchManager().getPlayerMatch(target);
			
			if(playerMatch != null) {
				playerMatch.removeSpectator(player, true);
			}
			
			targetMatch.addSpectator(player);
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /spectate <player>"));
		}
	}
}
