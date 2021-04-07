package club.veluxpvp.practice.party.command;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.party.Party;
import club.veluxpvp.practice.party.PartyRole;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class PartyCloseCommand extends PartyCommand {

	@Command(name = "party.close", aliases = {"p.close", "faction.close", "f.close", "team.close", "t.close", "clan.close",
			"party.lock", "p.lock", "faction.lock", "f.lock", "team.lock", "t.lock", "clan.lock"}, playersOnly = true, partyOnly = true)
	public void execute(CommandArgs cmd) {
		Player player = cmd.getPlayer();
		Party party = pm.getPlayerParty(player);
		
		if(party.getMember(player).getRole() != PartyRole.LEADER) {
			player.sendMessage(ChatUtil.TRANSLATE("&cYou must be the party leader!"));
			return;
		}
		
		if(!party.isOpen()) {
			player.sendMessage(ChatUtil.TRANSLATE("&cThe party is not open!"));
			return;
		}
		
		party.setOpen(false);
		party.stopAutoAnnounce();
		player.sendMessage(ChatUtil.TRANSLATE("&cYour party is no longer open to the public!"));
	}
}
