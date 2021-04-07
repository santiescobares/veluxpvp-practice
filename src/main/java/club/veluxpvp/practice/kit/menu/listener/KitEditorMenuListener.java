package club.veluxpvp.practice.kit.menu.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.kit.menu.KitEditorLadderButton;
import club.veluxpvp.practice.kit.menu.KitEditorMenu;
import club.veluxpvp.practice.menu.Button;
import club.veluxpvp.practice.menu.Menu;
import club.veluxpvp.practice.menu.MenuManager;

public class KitEditorMenuListener implements Listener {

	public KitEditorMenuListener() {
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Menu openedMenu = MenuManager.getOpenedMenu(player);
		
		if(openedMenu != null && openedMenu instanceof KitEditorMenu) {
			event.setCancelled(true);
			
			KitEditorMenu menu = (KitEditorMenu) openedMenu;
			Button clickedButton = menu.getClickedButton(event.getSlot());
			
			if(clickedButton != null && clickedButton instanceof KitEditorLadderButton) {
				KitEditorLadderButton button = (KitEditorLadderButton) clickedButton;
				
				player.closeInventory();
				Practice.getInstance().getKitManager().sendToKitEditorRoom(player, button.getKitType());
			}
		}
	}
}
