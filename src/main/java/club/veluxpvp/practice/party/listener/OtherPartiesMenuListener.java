package club.veluxpvp.practice.party.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Arena;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.menu.Button;
import club.veluxpvp.practice.menu.Menu;
import club.veluxpvp.practice.menu.MenuManager;
import club.veluxpvp.practice.party.Party;
import club.veluxpvp.practice.party.PartyDuel;
import club.veluxpvp.practice.party.PartyManager;
import club.veluxpvp.practice.party.menu.OtherPartiesMenu;
import club.veluxpvp.practice.party.menu.OtherPartyButton;
import club.veluxpvp.practice.party.menu.SelectOtherPartiesLadderButton;
import club.veluxpvp.practice.party.menu.SelectOtherPartiesLadderMenu;
import club.veluxpvp.practice.party.pvpclass.HCFClassType;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.Preconditions;

public class OtherPartiesMenuListener implements Listener {

	public OtherPartiesMenuListener() {
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Menu openedMenu = MenuManager.getOpenedMenu(player);
		
		if(openedMenu == null) return;
		// Choosing party for duel
		if(openedMenu instanceof OtherPartiesMenu) {
			event.setCancelled(true);
			
			OtherPartiesMenu menu = (OtherPartiesMenu) openedMenu;
			Button clickedButton = menu.getClickedButton(event.getSlot());
			
			if(clickedButton != null && clickedButton instanceof OtherPartyButton) {
				OtherPartyButton button = (OtherPartyButton) clickedButton;
				Party party = Practice.getInstance().getPartyManager().getPlayerParty(player);
				Party clickedParty = button.getParty();
				
				if(party == null || party.getLeader().getPlayer() != player || party == clickedParty) return;
				if(clickedParty == null || !clickedParty.isInLobby()) {
					player.sendMessage(ChatUtil.TRANSLATE("&cThis party is no longer avaiable to duel!"));
					return;
				}
				
				if(PartyManager.hasSentPartyDuel(party, clickedParty)) {
					player.sendMessage(ChatUtil.TRANSLATE("&cYour party has already sent a duel request to " + clickedParty.getLeader().getPlayer().getName() + "'s party!"));
					return;
				}
				
				PartyDuel pd = new PartyDuel(party, clickedParty);
				PartyManager.makingPartyDuel.put(party, pd);
				
				SelectOtherPartiesLadderMenu newMenu = new SelectOtherPartiesLadderMenu(player);
				newMenu.openMenu(player);
			}
			
			return;
		}
		
		// Choosing ladder
		if(openedMenu instanceof SelectOtherPartiesLadderMenu) {
			event.setCancelled(true);
			
			SelectOtherPartiesLadderMenu menu = (SelectOtherPartiesLadderMenu) openedMenu;
			Button clickedButton = menu.getClickedButton(event.getSlot());
			
			if(clickedButton != null && clickedButton instanceof SelectOtherPartiesLadderButton) {
				SelectOtherPartiesLadderButton button = (SelectOtherPartiesLadderButton) clickedButton;
				Party party = Practice.getInstance().getPartyManager().getPlayerParty(player);
				
				if(party == null || party.getLeader().getPlayer() != player) return;
				
				PartyDuel pd = PartyManager.getMakingDuel(party);
				
				if(pd == null) return;
				
				Ladder ladder = button.getLadder();
				
				if(ladder == Ladder.HCT_NO_DEBUFF || ladder == Ladder.HCT_DEBUFF) {
					if(party.getMembers().size() < 2) {
						player.sendMessage(ChatUtil.TRANSLATE("&cYour party requires at least 3 members to accept a HCF TeamFight match!"));
						return;
					}
					
					HCFClassType[] limitedClasses = {HCFClassType.BARD, HCFClassType.ROGUE, HCFClassType.ARCHER};
					
					for(int i = 0; i < limitedClasses.length; i++) {
						if(Preconditions.isClassLimitExceded(party, limitedClasses[i])) {
							player.sendMessage(ChatUtil.TRANSLATE("&cYour party has reached the limit of " + limitedClasses[i].name + " HCF classes! Please reorganize your roster."));
							return;
						}
					}
				}
				
				Arena arena = Practice.getInstance().getArenaManager().getRandomPartyArena(ladder);
				
				if(arena == null) {
					PartyManager.remove(party, pd.getTarget());
					player.closeInventory();
					player.sendMessage(ChatUtil.TRANSLATE("&cThere are no avaiable arenas for this ladder!"));
					return;
				}
				
				pd.setLadder(ladder);
				pd.setArena(arena);
				PartyManager.sendPartyDuel(party);
				player.closeInventory();
			}
		}
	}
}
