package club.veluxpvp.practice.party.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.menu.Button;
import club.veluxpvp.practice.menu.Menu;
import club.veluxpvp.practice.menu.MenuManager;
import club.veluxpvp.practice.party.Party;
import club.veluxpvp.practice.party.PartyMember;
import club.veluxpvp.practice.party.menu.HCFRosterButton;
import club.veluxpvp.practice.party.menu.HCFRosterMenu;
import club.veluxpvp.practice.party.pvpclass.HCFClassType;
import club.veluxpvp.practice.utilities.ChatUtil;

public class HCFRosterMenuListener implements Listener {

	public HCFRosterMenuListener() {
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Menu openedMenu = MenuManager.getOpenedMenu(player);
		
		if(openedMenu != null && openedMenu instanceof HCFRosterMenu) {
			event.setCancelled(true);
			
			HCFRosterMenu menu = (HCFRosterMenu) openedMenu;
			Button clickedButton = menu.getClickedButton(event.getSlot());
			
			if(clickedButton != null && clickedButton instanceof HCFRosterButton) {
				Party party = Practice.getInstance().getPartyManager().getPlayerParty(player);
				HCFRosterButton button = (HCFRosterButton) clickedButton;
				PartyMember clickedMember = button.getClickedMember();
				
				if(party == null || clickedMember == null || Practice.getInstance().getPartyManager().getPlayerParty(clickedMember.getPlayer()) == null) return;
				if(party.getLeader() != party.getMember(player)) return;
				
				HCFClassType currentClass = clickedMember.getHcfClass();
				
				if(currentClass == null) return;
				
				HCFClassType nextClass = currentClass == HCFClassType.DIAMOND ? HCFClassType.BARD : currentClass == HCFClassType.BARD ? HCFClassType.ROGUE : currentClass == HCFClassType.ROGUE ? HCFClassType.ARCHER : HCFClassType.DIAMOND;
			
				clickedMember.setHcfClass(nextClass);
				party.getMembers().stream().forEach(m -> m.getPlayer().sendMessage(ChatUtil.TRANSLATE("&b" + clickedMember.getPlayer().getName() + "&7's HCF class has been set to " + nextClass.getColor() + nextClass.name + "&7.")));
			
				menu.openMenu(player);
			}
		}
	}
}
