package club.veluxpvp.practice.tournament.command;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.item.ItemManager;
import club.veluxpvp.practice.party.Party;
import club.veluxpvp.practice.tournament.Tournament;
import club.veluxpvp.practice.tournament.TournamentState;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.PlayerUtil;
import club.veluxpvp.practice.utilities.Preconditions;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class TournamentJoinCommand extends TournamentCommand {
	
	@Command(name = "tournament.join", aliases = {"tour.join"}, playersOnly = true)
	public void execute(CommandArgs cmd) {
		Player player = cmd.getPlayer();
		
		Tournament tour = tm.getActiveTournament();
		
		if(tour == null) {
			player.sendMessage(ChatUtil.TRANSLATE("&cThere aren't any active tournament!"));
			return;
		}
		
		if(tour.isFull() && !player.hasPermission("practice.tournament.joinfull")) {
			player.sendMessage(ChatUtil.TRANSLATE("&cThe tournament is full!"));
			return;
		}
		
		if(tour.getState() == TournamentState.STARTING && !player.hasPermission("practice.tournament.joinfull")) {
			player.sendMessage(ChatUtil.TRANSLATE("&cThe tournament has already started!"));
			return;
		}
		
		if(tour.isStarted()) {
			player.sendMessage(ChatUtil.TRANSLATE("&cThe tournament has already started!"));
			return;
		}
		
		if(!Preconditions.canJoinTournament(player)) return;
		
		Party party = Practice.getInstance().getPartyManager().getPlayerParty(player);
		
		if(party == null) {
			party = new Party(player);
			Practice.getInstance().getPartyManager().getParties().add(party);
			
			if(PlayerUtil.isInLobby(player)) {
				PlayerUtil.reset(player, player.getGameMode(), false);
				ItemManager.loadPartyItems(player);
			}
		} else {
			if(party.getLeader().getPlayer() != player) {
				player.sendMessage(ChatUtil.TRANSLATE("&cYou must be the party leader!"));
				return;
			}
			
			if(party.isOpen()) {
				party.setOpen(false);
				party.stopAutoAnnounce();
			}
		}
		
		tour.addParticipant(party);
	}
}
