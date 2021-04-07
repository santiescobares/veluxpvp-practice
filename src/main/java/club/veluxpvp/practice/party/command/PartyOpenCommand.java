package club.veluxpvp.practice.party.command;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.party.Party;
import club.veluxpvp.practice.party.PartyRole;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class PartyOpenCommand extends PartyCommand {

	@Command(name = "party.open", aliases = {"p.open", "faction.open", "f.open", "team.open", "t.open", "clan.open"}, playersOnly = true, partyOnly = true)
	public void execute(CommandArgs cmd) {
		Player player = cmd.getPlayer();
		Party party = pm.getPlayerParty(player);
		
		if(party.getMember(player).getRole() != PartyRole.LEADER) {
			player.sendMessage(ChatUtil.TRANSLATE("&cYou must be the party leader!"));
			return;
		}
		
		if(party.isOpen()) {
			player.sendMessage(ChatUtil.TRANSLATE("&cThe party is already open!"));
			return;
		}
		
		if(pm.isPartyInTournament(party)) {
			player.sendMessage(ChatUtil.TRANSLATE("&cYou cant' open your party while in a tournament!"));
			return;
		}
		
		party.setOpen(true);
		player.sendMessage(ChatUtil.TRANSLATE("&aYour party is now open to the public!"));
		if(player.hasPermission("practice.party.announceopen")) party.startAutoAnnounce();
	}
}
