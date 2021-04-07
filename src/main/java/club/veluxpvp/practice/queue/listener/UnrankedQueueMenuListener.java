package club.veluxpvp.practice.queue.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.menu.Button;
import club.veluxpvp.practice.menu.Menu;
import club.veluxpvp.practice.menu.MenuManager;
import club.veluxpvp.practice.queue.menu.UnrankedQueueButton;
import club.veluxpvp.practice.queue.menu.UnrankedQueueMenu;

public class UnrankedQueueMenuListener implements Listener {

	public UnrankedQueueMenuListener() {
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Menu openedMenu = MenuManager.getOpenedMenu(player);
		
		if(openedMenu != null && openedMenu instanceof UnrankedQueueMenu) {
			event.setCancelled(true);
			
			UnrankedQueueMenu menu = (UnrankedQueueMenu) openedMenu;
			Button button = menu.getClickedButton(event.getSlot());
			
			if(button != null && button instanceof UnrankedQueueButton) {
				UnrankedQueueButton b = (UnrankedQueueButton) button;
				Ladder ladder = b.getLadder();
				
				player.closeInventory();
				
				Practice.getInstance().getQueueManager().addPlayer(player, ladder, false);
			}
		}
	}
}
