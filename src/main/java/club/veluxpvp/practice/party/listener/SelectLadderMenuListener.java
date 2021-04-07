package club.veluxpvp.practice.party.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Arena;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.menu.Button;
import club.veluxpvp.practice.menu.Menu;
import club.veluxpvp.practice.menu.MenuManager;
import club.veluxpvp.practice.party.Party;
import club.veluxpvp.practice.party.PartyMember;
import club.veluxpvp.practice.party.menu.SelectSplitLadderMenu;
import club.veluxpvp.practice.party.pvpclass.HCFClassType;
import club.veluxpvp.practice.party.menu.SelectFFALadderButton;
import club.veluxpvp.practice.party.menu.SelectFFALadderMenu;
import club.veluxpvp.practice.party.menu.SelectSplitLadderButton;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.Preconditions;

public class SelectLadderMenuListener implements Listener {

	public SelectLadderMenuListener() {
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Menu openedMenu = MenuManager.getOpenedMenu(player);
		
		if(openedMenu == null) return;
		// Is party split
		if(openedMenu instanceof SelectSplitLadderMenu) {
			event.setCancelled(true);
			
			SelectSplitLadderMenu menu = (SelectSplitLadderMenu) openedMenu;
			Button clickedButton = menu.getClickedButton(event.getSlot());
			
			if(clickedButton != null && clickedButton instanceof SelectSplitLadderButton) {
				SelectSplitLadderButton button = (SelectSplitLadderButton) clickedButton;
				Party party = Practice.getInstance().getPartyManager().getPlayerParty(player);
				
				if(party == null || party.getLeader().getPlayer() != player) return;
				
				if(party.getMembers().size() < 2) {
					player.sendMessage(ChatUtil.TRANSLATE("&cYour party requires at least 2 members to start a splitted fight!"));
					return;
				}
				
				Ladder ladder = button.getLadder();
				
				if(ladder == Ladder.HCT_NO_DEBUFF || ladder == Ladder.HCT_DEBUFF) {
					player.sendMessage(ChatUtil.TRANSLATE("&cHCF Team Fights are not avaiable yet!"));
					return;
				}
				
				if(ladder == Ladder.HCT_NO_DEBUFF || ladder == Ladder.HCT_DEBUFF) {
					if(party.getMembers().size() < 2) {
						player.sendMessage(ChatUtil.TRANSLATE("&cYour party requires at least 6 members to start a HCF TeamFight match!"));
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
					player.closeInventory();
					player.sendMessage(ChatUtil.TRANSLATE("&cThere are no avaiable arenas for this ladder!"));
					return;
				}
				
				Match match = new Match(arena, ladder, false);
				party.getMembers().stream().map(PartyMember::getPlayer).forEach(p -> match.getAlivePlayers().add(p));
				match.startCountdown();
				player.closeInventory();
			}
			
			return;
		}
		
		// Is party ffa
		if(openedMenu instanceof SelectFFALadderMenu) {
			event.setCancelled(true);
			
			SelectFFALadderMenu menu = (SelectFFALadderMenu) openedMenu;
			Button clickedButton = menu.getClickedButton(event.getSlot());
			
			if(clickedButton != null && clickedButton instanceof SelectFFALadderButton) {
				SelectFFALadderButton button = (SelectFFALadderButton) clickedButton;
				Party party = Practice.getInstance().getPartyManager().getPlayerParty(player);
				
				if(party == null || party.getLeader().getPlayer() != player) return;
				
				if(party.getMembers().size() < 2) {
					player.sendMessage(ChatUtil.TRANSLATE("&cYour party requires at least 2 members to start a ffa fight!"));
					return;
				}
				
				Ladder ladder = button.getLadder();
				Arena arena = Practice.getInstance().getArenaManager().getRandomPartyArena(ladder);
				
				if(arena == null) {
					player.closeInventory();
					player.sendMessage(ChatUtil.TRANSLATE("&cThere are no avaiable arenas for this ladder!"));
					return;
				}
				
				Match match = new Match(arena, ladder, false);
				match.setFfa(true);
				party.getMembers().stream().map(PartyMember::getPlayer).forEach(p -> match.getAlivePlayers().add(p));
				match.startCountdown();
				player.closeInventory();
			}
			
			return;
		}
	}
}
