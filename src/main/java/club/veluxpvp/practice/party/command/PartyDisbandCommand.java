package club.veluxpvp.practice.party.command;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.party.Party;
import club.veluxpvp.practice.party.PartyRole;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class PartyDisbandCommand extends PartyCommand {

	@Command(name = "party.disband", aliases = {"p.disband", "faction.disband", "f.disband", "team.disband", "t.disband", "clan.disband"}, playersOnly = true, partyOnly = true)
	public void execute(CommandArgs cmd) {
		Player player = cmd.getPlayer();
		Party party = pm.getPlayerParty(player);
		
		if(party.getMember(player).getRole() != PartyRole.LEADER) {
			player.sendMessage(ChatUtil.TRANSLATE("&cYou must be the party leader!"));
			return;
		}
		
		party.disband();
	}
}
