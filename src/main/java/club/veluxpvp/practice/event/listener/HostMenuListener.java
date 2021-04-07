package club.veluxpvp.practice.event.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Arena;
import club.veluxpvp.practice.event.EventType;
import club.veluxpvp.practice.event.PracticeEventManager;
import club.veluxpvp.practice.event.menu.HostButton;
import club.veluxpvp.practice.event.menu.HostMenu;
import club.veluxpvp.practice.menu.Button;
import club.veluxpvp.practice.menu.Menu;
import club.veluxpvp.practice.menu.MenuManager;
import club.veluxpvp.practice.utilities.ChatUtil;

public class HostMenuListener implements Listener {

	public HostMenuListener() {
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Menu openedMenu = MenuManager.getOpenedMenu(player);
		
		if(openedMenu != null && openedMenu instanceof HostMenu) {
			event.setCancelled(true);
			
			HostMenu menu = (HostMenu) openedMenu;
			Button clickedButton = menu.getClickedButton(event.getSlot());
			
			if(clickedButton != null && clickedButton instanceof HostButton) {
				HostButton button = (HostButton) clickedButton;
				PracticeEventManager em = Practice.getInstance().getEventManager();
				
				if(event.getClick() == ClickType.RIGHT || event.getClick() == ClickType.SHIFT_RIGHT) {
					boolean twoVStwo = button.isTwoVStwo() ? false : true;
					button.setTwoVStwo(twoVStwo);
					menu.openMenu(player);
					return;
				}
				
				if(em.getActiveEvent() != null) {
					player.sendMessage(ChatUtil.TRANSLATE("&cThere is already an active event!"));
					return;
				}
				
				if(em.isEventCooldown()) {
					player.sendMessage(ChatUtil.TRANSLATE("&cAn event was already hosted recently! Please wait a few seconds before host another one."));
					return;
				}
				
				EventType type = button.getEventType();
				Arena arena = Practice.getInstance().getArenaManager().getRandomEventsArena(type);
				
				if(arena == null) {
					player.sendMessage(ChatUtil.TRANSLATE("&cThere are no avaiable free arenas for this event!"));
					return;
				}
			}
		}
	}
}
