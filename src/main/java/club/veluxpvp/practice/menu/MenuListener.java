package club.veluxpvp.practice.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import club.veluxpvp.practice.Practice;

public class MenuListener implements Listener {

	public MenuListener() {
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		Menu openedMenu = MenuManager.getOpenedMenu(player);
		
		if(openedMenu != null) {
			if(openedMenu.isRemoveOnClose()) {
				if(openedMenu.isAutoUpdate()) {
					if(openedMenu.getUpdateTask() != null) Bukkit.getScheduler().cancelTask(openedMenu.getUpdateTask().getTaskId());
				}
				
				MenuManager.openedMenu.remove(player.getUniqueId(), openedMenu);
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Menu openedMenu = MenuManager.getOpenedMenu(player);
		
		if(openedMenu != null) MenuManager.openedMenu.remove(player.getUniqueId());
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		Player player = event.getPlayer();
		Menu openedMenu = MenuManager.getOpenedMenu(player);
		
		if(openedMenu != null) MenuManager.openedMenu.remove(player.getUniqueId());
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Menu openedMenu = MenuManager.getOpenedMenu(player);
		
		if(openedMenu != null) {
			if(openedMenu.isUpdateOnClick()) {
				Button button = openedMenu.getClickedButton(event.getSlot());
				
				if(button == null) return;
				
				Inventory inventory = event.getInventory();
				
				if(inventory == null) return;
				
				inventory.setItem(event.getSlot(), new ItemStack(Material.AIR));
				inventory.setItem(event.getSlot(), button.build());
			}
		}
	}
}
