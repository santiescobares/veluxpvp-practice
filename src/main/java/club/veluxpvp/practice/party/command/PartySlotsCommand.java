package club.veluxpvp.practice.party.command;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.party.Party;
import club.veluxpvp.practice.party.PartyRole;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class PartySlotsCommand extends PartyCommand {

	@Command(name = "party.slots", aliases = {"p.slots", "faction.slots", "f.slots", "team.slots", "t.slots", "clan.slots"}, partyOnly = true)
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		Player player = cmd.getPlayer();
		Party party = pm.getPlayerParty(player);
		
		if(party.getMember(player).getRole() != PartyRole.LEADER) {
			player.sendMessage(ChatUtil.TRANSLATE("&cYou must be the party leader!"));
			return;
		}
		
		if(args.length >= 1) {
			try {
				int slots = Integer.valueOf(args[0]);
				
				if(slots < 1) {
					player.sendMessage(ChatUtil.TRANSLATE("&cThe number must be positive!"));
					return;
				}
				
				if(slots < party.getMembers().size()) {
					player.sendMessage(ChatUtil.TRANSLATE("&cThe party slots can't be lower than the party members size!"));
					return;
				}
				
				if(slots > 100) {
					player.sendMessage(ChatUtil.TRANSLATE("&cThe max slots of a party is 100!"));
					return;
				}
				
				if(slots > 75) {
					if(!player.hasPermission("practice.party.slots.max")) {
						player.sendMessage(ChatUtil.TRANSLATE("&cYou rank only can up to 75 slots per party!"));
						return;
					}
				}
				
				if(slots > 50) {
					if(!player.hasPermission("practice.party.slots.diamond")) {
						player.sendMessage(ChatUtil.TRANSLATE("&cYou rank only can up to 50 slots per party!"));
						return;
					}
				}
				
				if(slots > 35) {
					if(!player.hasPermission("practice.party.slots.gold")) {
						player.sendMessage(ChatUtil.TRANSLATE("&cYou rank only can up to 35 slots per party!"));
						return;
					}
				}
				
				if(slots > 20) {
					if(!player.hasPermission("practice.party.slots.ruby")) {
						player.sendMessage(ChatUtil.TRANSLATE("&cYou rank only can up to 20 slots per party!"));
						return;
					}
				}
				
				party.setSlots(slots);
				party.getMembers().stream().forEach(m -> m.getPlayer().sendMessage(ChatUtil.TRANSLATE("&7The party slots have been set to &b" + slots + "&7.")));
			} catch(NumberFormatException e) {
				player.sendMessage(ChatUtil.TRANSLATE("&cYou must enter a valid number!"));
			}
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /party slots <slots>"));
		}
	}
}
