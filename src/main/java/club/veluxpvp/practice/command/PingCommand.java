package club.veluxpvp.practice.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.match.MatchTeam;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.PlayerUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class PingCommand {

	public PingCommand() {
		Practice.getInstance().getCommandFramework().registerCommands(this);
	}
	
	@Command(name = "ping", playersOnly = true)
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		Player player = cmd.getPlayer();
		
		if(args.length >= 1) {
			Player target = Bukkit.getPlayer(args[0]);
			
			if(target == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cPlayer \"" + args[0] + "\" not found!"));
				return;
			}
			
			player.sendMessage(ChatUtil.TRANSLATE("&b" + target.getName() + "&f's Ping&7: &b" + PlayerUtil.getPing(target) + "ms"));
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&fYour Ping&7: &b" + PlayerUtil.getPing(player) + "ms"));
			
			Match match = Practice.getInstance().getMatchManager().getPlayerMatch(player);
			
			if(match != null && !match.isFfa() && match.getTeam1().getPlayers().size() == 1 && match.getTeam2().getPlayers().size() == 1) {
				MatchTeam playerTeam = match.getPlayerTeam(player);
				Player target = playerTeam == match.getTeam1() ? match.getTeam2().getFirstPlayer() : match.getTeam1().getFirstPlayer();
				
				player.sendMessage(ChatUtil.TRANSLATE("&b" + target.getName() + "&f's Ping&7: &b" + PlayerUtil.getPing(target) + "ms"));
			}
		}
	}
}
