package club.veluxpvp.practice.party.command;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.item.ItemManager;
import club.veluxpvp.practice.party.Party;
import club.veluxpvp.practice.utilities.PlayerUtil;
import club.veluxpvp.practice.utilities.Preconditions;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class PartyCreateCommand extends PartyCommand {

	@Command(name = "party.create", aliases = {"p.create", "faction.create", "f.create", "team.create", "t.create", "clan.create"})
	public void execute(CommandArgs cmd) {
		Player player = cmd.getPlayer();
		
		if(!Preconditions.canCreateParty(player)) return;
		
		Party party = new Party(player);
		pm.getParties().add(party);
		
		if(PlayerUtil.isInLobby(player)) {
			PlayerUtil.reset(player, player.getGameMode(), false);
			ItemManager.loadPartyItems(player);
		}
	}
}
