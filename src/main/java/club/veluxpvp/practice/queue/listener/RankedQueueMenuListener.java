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
import club.veluxpvp.practice.queue.menu.RankedQueueButton;
import club.veluxpvp.practice.queue.menu.RankedQueueMenu;

public class RankedQueueMenuListener implements Listener {

	public RankedQueueMenuListener() {
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Menu openedMenu = MenuManager.getOpenedMenu(player);
		
		if(openedMenu != null && openedMenu instanceof RankedQueueMenu) {
			event.setCancelled(true);
			
			RankedQueueMenu menu = (RankedQueueMenu) openedMenu;
			Button button = menu.getClickedButton(event.getSlot());
			
			if(button != null && button instanceof RankedQueueButton) {
				RankedQueueButton b = (RankedQueueButton) button;
				Ladder ladder = b.getLadder();

				player.closeInventory();
				
				Practice.getInstance().getQueueManager().addPlayer(player, ladder, true);
			}
		}
	}
}
