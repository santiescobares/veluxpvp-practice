package club.veluxpvp.practice.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.match.PostMatchPlayer;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class _Command {

	public _Command() {
		Practice.getInstance().getCommandFramework().registerCommands(this);
	}
	
	@SuppressWarnings("deprecation")
	@Command(name = "_", playersOnly = true)
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		Player player = cmd.getPlayer();
		
		if(args.length >= 1) {
			OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
			Match lastPlayerMatch = Practice.getInstance().getMatchManager().getLastMatch(player);
			
			if(lastPlayerMatch == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cNo data found for \"" + target.getName() + "\"!"));
				return;
			}
			
			PostMatchPlayer postMatchTarget = lastPlayerMatch.getPostMatchPlayers().get(target.getUniqueId());
			
			if(postMatchTarget == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cNo data found for \"" + target.getName() + "\"!"));
				return;
			}
			
			boolean canSwapInventories = lastPlayerMatch.isFfa() ? false : lastPlayerMatch.isParty() ? false : true;
			
			player.openInventory(postMatchTarget.getInventory(canSwapInventories));
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /_ <player>"));
		}
	}
}
