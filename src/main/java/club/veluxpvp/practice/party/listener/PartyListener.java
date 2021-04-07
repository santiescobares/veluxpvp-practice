package club.veluxpvp.practice.party.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.item.ItemManager;
import club.veluxpvp.practice.kit.menu.KitEditorMenu;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.party.Party;
import club.veluxpvp.practice.party.Party.QuitReason;
import club.veluxpvp.practice.party.PartyManager;
import club.veluxpvp.practice.party.PartyRole;
import club.veluxpvp.practice.party.command.PartyInfoCommand;
import club.veluxpvp.practice.party.menu.HCFRosterMenu;
import club.veluxpvp.practice.party.menu.OtherPartiesMenu;
import club.veluxpvp.practice.party.menu.fight.StartPartyFightMenu;
import club.veluxpvp.practice.utilities.ChatUtil;

public class PartyListener implements Listener {

	private PartyManager pm;
	
	public PartyListener() {
		this.pm = Practice.getInstance().getPartyManager();
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Party party = pm.getPlayerParty(player);
		
		if(party == null) return;
		
		party.removeMember(player, Party.QuitReason.LEFT);
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		Player player = event.getPlayer();
		Party party = pm.getPlayerParty(player);
		
		if(party == null) return;
		
		party.removeMember(player, Party.QuitReason.LEFT);
	}
	
	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		String message = event.getMessage();
		Party party = pm.getPlayerParty(player);
		
		if(party == null) return;
		
		if(message.equals("@")) {
			event.setCancelled(true);
			player.sendMessage(ChatUtil.TRANSLATE("&cYou must supply a message!"));
			return;
		}
		
		if(message.startsWith("@")) {
			String format = "&b(Party) " + (party.getLeader().getPlayer() == player ? "&f" : "&a") + player.getName() + "&f: " + message.replaceFirst("@", "");
			
			event.setCancelled(true);
			
			party.getMembers().stream().forEach(m -> m.getPlayer().sendMessage(ChatUtil.TRANSLATE(format)));
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Party party = pm.getPlayerParty(player);
		Match match = Practice.getInstance().getMatchManager().getPlayerMatch(player);
		
		if(party == null) return;
		if(match != null) return;
		
		if(event.getAction().name().startsWith("RIGHT_CLICK")) {
			ItemStack item = event.getItem();
			
			if(item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return;
			
			// Party Info
			if(item.equals(ItemManager.getPartyInfo())) {
				event.setCancelled(true);
				event.setUseInteractedBlock(Result.DENY);
				event.setUseItemInHand(Result.DENY);
				
				PartyInfoCommand.sendPartyInfo(player, party);
				return;
			}
			
			// Start Party Fight
			if(item.equals(ItemManager.getPartyStartFight())) {
				event.setCancelled(true);
				event.setUseInteractedBlock(Result.DENY);
				event.setUseItemInHand(Result.DENY);
				
				if(Practice.getInstance().getTournamentManager().getActiveTournament() != null && Practice.getInstance().getTournamentManager().getActiveTournament().isInTournament(party)) {
					player.sendMessage(ChatUtil.TRANSLATE("&cYou can't start a party fight while your party is in a tournament!"));
					return;
				}
				
				StartPartyFightMenu menu = new StartPartyFightMenu(player);
				menu.openMenu(player);
				return;
			}
			
			// HCF Roster
			if(item.equals(ItemManager.getPartyHCFRoster())) {
				event.setCancelled(true);
				event.setUseInteractedBlock(Result.DENY);
				event.setUseItemInHand(Result.DENY);
				
				HCFRosterMenu menu = new HCFRosterMenu(player, party);
				menu.openMenu(player);
				return;
			}
			
			// Other Parties
			if(item.equals(ItemManager.getPartyOtherParties())) {
				if(Practice.getInstance().getTournamentManager().getActiveTournament() != null && Practice.getInstance().getTournamentManager().getActiveTournament().isInTournament(party)) {
					player.sendMessage(ChatUtil.TRANSLATE("&cYou can't start a party fight while your party is in a tournament!"));
					return;
				}
				
				event.setCancelled(true);
				event.setUseInteractedBlock(Result.DENY);
				event.setUseItemInHand(Result.DENY);
				
				OtherPartiesMenu menu = new OtherPartiesMenu(player);
				menu.openMenu(player);
				return;
			}
			
			// Kit Editor
			if(item.equals(ItemManager.getPartyKitEditor())) {
				event.setCancelled(true);
				event.setUseInteractedBlock(Result.DENY);
				event.setUseItemInHand(Result.DENY);
				
				KitEditorMenu menu = new KitEditorMenu(player);
				menu.openMenu(player);
				return;
			}
			
			// Leave Party
			if(item.equals(ItemManager.getPartyLeaveParty())) {
				event.setCancelled(true);
				event.setUseInteractedBlock(Result.DENY);
				event.setUseItemInHand(Result.DENY);
				
				if(party.getMember(player).getRole() == PartyRole.MEMBER) {
					party.removeMember(player, QuitReason.LEFT);
				} else {
					party.disband();
				}
			}
		}
	}
}
