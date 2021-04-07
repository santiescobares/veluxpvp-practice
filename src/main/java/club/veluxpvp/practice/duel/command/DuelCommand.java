package club.veluxpvp.practice.duel.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.duel.Duel;
import club.veluxpvp.practice.duel.DuelManager;
import club.veluxpvp.practice.duel.menu.DuelLadderMenu;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.Preconditions;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class DuelCommand {

	public DuelCommand() {
		Practice.getInstance().getCommandFramework().registerCommands(this);
	}
	
	@Command(name = "duel", aliases = {"1vs1", "1v1"}, playersOnly = true)
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
				player.sendMessage(ChatUtil.TRANSLATE("&cYou can't duel yourself!"));
				return;
			}
			
			if(!Preconditions.canSendDuel(player, target)) return;
			
			if(DuelManager.hasSentDuel(player, target)) {
				player.sendMessage(ChatUtil.TRANSLATE("&cYou have already sent a duel request to " + target.getName() + "!"));
				return;
			}
			
			Duel duel = new Duel(player, target);
			DuelManager.makingDuel.put(player.getUniqueId(), duel);
			
			DuelLadderMenu menu = new DuelLadderMenu(player);
			menu.openMenu(player);
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /duel <player>"));
		}
	}
}
