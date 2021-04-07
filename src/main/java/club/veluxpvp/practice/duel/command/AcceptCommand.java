package club.veluxpvp.practice.duel.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.duel.Duel;
import club.veluxpvp.practice.duel.DuelManager;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.Preconditions;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class AcceptCommand {

	public AcceptCommand() {
		Practice.getInstance().getCommandFramework().registerCommands(this);
	}
	
	@Command(name = "accept", playersOnly = true)
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		Player player = cmd.getPlayer();
		
		if(args.length >= 1) {
			Player target = Bukkit.getPlayer(args[0]);
			
			if(target == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cPlayer \"" + args[0] + "\" not found!"));
				return;
			}

			if(!Preconditions.canAcceptDuel(player, target)) return;
			
			if(!DuelManager.hasSentDuel(target, player)) {
				player.sendMessage(ChatUtil.TRANSLATE("&cYou don't have any duel request of " + target.getName() + "!"));
				return;
			}
			
			Duel duel = DuelManager.duels.stream().filter(d -> d.getSender() == target && d.getTarget() == player && !d.isExpired()).findFirst().orElse(null);
			
			if(duel == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cYou don't have any duel request of " + target.getName() + "!"));
				return;
			}
			
			if(Practice.getInstance().getQueueManager().getPlayer(player) != null) {
				Practice.getInstance().getQueueManager().removePlayer(player, false, true);
			}
			
			if(Practice.getInstance().getQueueManager().getPlayer(target) != null) {
				Practice.getInstance().getQueueManager().removePlayer(target, false, true);
			}
			
			Match match = new Match(duel.getArena(), duel.getLadder(), false);
			match.getAlivePlayers().add(player);
			match.getAlivePlayers().add(target);
			
			Practice.getInstance().getMatchManager().getMatches().add(match);
			match.startCountdown();
			
			DuelManager.duels.remove(duel);
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /accept <player>"));
		}
	}
}
