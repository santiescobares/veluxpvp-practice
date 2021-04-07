package club.veluxpvp.practice.elo.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.elo.menu.EloMenu;
import club.veluxpvp.practice.profile.Profile;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class EloCommand {

	public EloCommand() {
		Practice.getInstance().getCommandFramework().registerCommands(this);
	}
	
	@SuppressWarnings("deprecation")
	@Command(name = "elo", aliases = {"statistics", "stats"})
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		CommandSender sender = cmd.getSender();
		
		if(args.length >= 1) {
			OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
			Profile targetProfile = Practice.getInstance().getProfileManager().getProfile(target);
			
			if(targetProfile == null) {
				sender.sendMessage(ChatUtil.TRANSLATE("&c\"" + target.getName() + "\" has never played before!"));
				return;
			}
			
			if(sender instanceof Player) {
				EloMenu menu = new EloMenu(cmd.getPlayer(), targetProfile);
				menu.openMenu(cmd.getPlayer());
			} else {
				sender.sendMessage(ChatUtil.SHORTER_LINE());
				sender.sendMessage(ChatUtil.TRANSLATE("&b&l" + target.getName() + "'s Statistics"));
				sender.sendMessage(ChatUtil.SHORTER_LINE());
				sender.sendMessage(ChatUtil.TRANSLATE("&bElo"));
				
				Ladder[] rankedLadders = Ladder.values();
				
				for(int i = 0; i < rankedLadders.length; i++) {
					Ladder l = rankedLadders[i];
					
					if(l == Ladder.COMBO_FLY || l == Ladder.HCF || l == Ladder.HCT_NO_DEBUFF || l == Ladder.HCT_DEBUFF) continue;
					
					sender.sendMessage(ChatUtil.TRANSLATE(" &7* &f" + l.name + "&7: &b" + targetProfile.getElo(l)));
				}
				
				sender.sendMessage(ChatUtil.TRANSLATE("&bUnranked"));
				sender.sendMessage(ChatUtil.TRANSLATE(" &7* &fWins&7: &b" + targetProfile.getUnrankedWins()));
				sender.sendMessage(ChatUtil.TRANSLATE(" &7* &fLoses&7: &b" + targetProfile.getUnrankedLoses()));
				sender.sendMessage(ChatUtil.TRANSLATE(" &7* &fWLR&7: &b" + targetProfile.getUnrankedWLR()));
				sender.sendMessage(ChatUtil.TRANSLATE("&bRanked"));
				sender.sendMessage(ChatUtil.TRANSLATE(" &7* &fWins&7: &b" + targetProfile.getRankedWins()));
				sender.sendMessage(ChatUtil.TRANSLATE(" &7* &fLoses&7: &b" + targetProfile.getRankedLoses()));
				sender.sendMessage(ChatUtil.TRANSLATE(" &7* &fWLR&7: &b" + targetProfile.getRankedWLR()));
				sender.sendMessage(ChatUtil.SHORTER_LINE());
			}
		} else {
			if(sender instanceof Player) {
				EloMenu menu = new EloMenu(cmd.getPlayer(), Practice.getInstance().getProfileManager().getProfile((Player) sender));
				menu.openMenu(cmd.getPlayer());
			} else {
				sender.sendMessage(ChatUtil.TRANSLATE("&cUsage: /elo <player>"));
			}
		}
	}
}
