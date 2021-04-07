package club.veluxpvp.practice.party.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import club.veluxpvp.practice.party.Party;
import club.veluxpvp.practice.party.PartyManager;
import club.veluxpvp.practice.party.PartyRole;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class PartyInviteCommand extends PartyCommand {

	@Command(name = "party.invite", aliases = {"p.invite", "faction.invite", "f.invite", "team.invite", "t.invite", "clan.invite"}, partyOnly = true)
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		Player player = cmd.getPlayer();
		Party party = pm.getPlayerParty(player);
		
		if(args.length >= 1) {
			Player target = Bukkit.getPlayer(args[0]);
			
			if(party.getMember(player).getRole() != PartyRole.LEADER) {
				player.sendMessage(ChatUtil.TRANSLATE("&cYou must be the party leader!"));
				return;
			}
			
			if(target == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cPlayer \"" + args[0] + "\" not found!"));
				return;
			}
			
			if(target == player) {
				player.sendMessage(ChatUtil.TRANSLATE("&cYou can't invite yourself!"));
				return;
			}
			
			if(party.getMember(target) != null) {
				player.sendMessage(ChatUtil.TRANSLATE("&c" + target.getName() + " is already in your party!"));
				return;
			}
			
			if(pm.getPlayerParty(target) != null) {
				player.sendMessage(ChatUtil.TRANSLATE("&c" + player.getName() + " is already in a party!"));
				return;
			}
			
			if(PartyManager.hasSentInvite(player, target)) {
				player.sendMessage(ChatUtil.TRANSLATE("&cYou have already invited " + target.getName() + " to the party!"));
				return;
			}
			
			PartyManager.sendInvite(player, target);
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /party invite <player>"));
		}
	}
}
