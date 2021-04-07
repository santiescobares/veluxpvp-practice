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
import club.veluxpvp.practice.party.menu.SelectSplitLadderMenu;
import club.veluxpvp.practice.party.menu.SelectFFALadderMenu;
import club.veluxpvp.practice.party.menu.fight.FFAPartyFightButton;
import club.veluxpvp.practice.party.menu.fight.SplitPartyFightButton;
import club.veluxpvp.practice.party.menu.fight.StartPartyFightMenu;

public class StartFightMenuListener implements Listener {

	public StartFightMenuListener() {
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Menu openedMenu = MenuManager.getOpenedMenu(player);
		
		if(openedMenu != null && openedMenu instanceof StartPartyFightMenu) {
			event.setCancelled(true);
			
			StartPartyFightMenu menu = (StartPartyFightMenu) openedMenu;
			Button clickedButton = menu.getClickedButton(event.getSlot());
			
			if(clickedButton == null) return;
			if(clickedButton instanceof SplitPartyFightButton) {
				SelectSplitLadderMenu newMenu = new SelectSplitLadderMenu(player);
				newMenu.openMenu(player);
				return;
			}
			
			if(clickedButton instanceof FFAPartyFightButton) {
				SelectFFALadderMenu newMenu = new SelectFFALadderMenu(player);
				newMenu.openMenu(player);
				return;
			}
		}
	}
}
