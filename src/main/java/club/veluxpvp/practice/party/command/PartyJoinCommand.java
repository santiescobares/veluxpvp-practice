package club.veluxpvp.practice.party.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import club.veluxpvp.practice.party.Party;
import club.veluxpvp.practice.party.PartyManager;
import club.veluxpvp.practice.party.PartyRole;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.Preconditions;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class PartyJoinCommand extends PartyCommand {

	@Command(name = "party.join", aliases = {"p.join", "faction.join", "f.join", "team.join", "t.join", "clan.join"})
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		Player player = cmd.getPlayer();

		if(args.length >= 1) {
			Player target = Bukkit.getPlayer(args[0]);

			if(!Preconditions.canJoinParty(player)) return;
			
			if(target == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cPlayer \"" + args[0] + "\" not found!"));
				return;
			}
			
			Party targetParty = pm.getPlayerParty(target);
			
			if(targetParty == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&c" + target.getName() + " is not in a party!"));
				return;
			}
			
			if(target.getName().equalsIgnoreCase(player.getName())) {
				player.sendMessage(ChatUtil.TRANSLATE("&cYou can't join into your own party!"));
				return;
			}
			
			if(targetParty.isOpen()) {
				if(targetParty.isFull()) {
					player.sendMessage(ChatUtil.TRANSLATE("&cThe party is full!"));
					return;
				}
				
				if(pm.isPartyInTournament(targetParty)) {
					player.sendMessage(ChatUtil.TRANSLATE("&cThe party is in a tournament!"));
					return;
				}

				targetParty.addMember(player, PartyRole.MEMBER);
			} else {
				if(PartyManager.hasSentInvite(target, player)) {
					if(targetParty.getLeader().getPlayer() != target) {
						player.sendMessage(ChatUtil.TRANSLATE("&cYou don't have any invite to the " + target.getName() + "'s party!"));
						return;
					}
					
					if(targetParty.isFull()) {
						player.sendMessage(ChatUtil.TRANSLATE("&cThe party is full!"));
						return;
					}

					if(pm.isPartyInTournament(targetParty)) {
						player.sendMessage(ChatUtil.TRANSLATE("&cThe party is in a tournament!"));
						return;
					}
					
					targetParty.addMember(player, PartyRole.MEMBER);
					PartyManager.acceptInvite(player, target);
				} else {
					player.sendMessage(ChatUtil.TRANSLATE("&cYou don't have any invite to the " + target.getName() + "'s party!"));
				}
			}
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /party join <player>"));
		}
	}
}
