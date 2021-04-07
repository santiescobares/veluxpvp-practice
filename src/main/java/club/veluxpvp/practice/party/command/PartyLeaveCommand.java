package club.veluxpvp.practice.party.command;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.party.Party;
import club.veluxpvp.practice.party.PartyRole;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class PartyLeaveCommand extends PartyCommand {

	@Command(name = "party.leave", aliases = {"p.leave", "faction.leave", "f.leave", "team.leave", "t.leave", "clan.leave"}, partyOnly = true)
	public void execute(CommandArgs cmd) {
		Player player = cmd.getPlayer();
		Party party = pm.getPlayerParty(player);
		
		if(party.getMember(player).getRole() == PartyRole.LEADER) {
			player.sendMessage(ChatUtil.TRANSLATE("&cPlease set a new party leader before leave it!"));
			return;
		}
		
		party.removeMember(player, Party.QuitReason.LEFT);
	}
}
