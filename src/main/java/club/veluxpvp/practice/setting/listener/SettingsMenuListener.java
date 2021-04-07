package club.veluxpvp.practice.setting.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.menu.Button;
import club.veluxpvp.practice.menu.Menu;
import club.veluxpvp.practice.menu.MenuManager;
import club.veluxpvp.practice.setting.SettingType;
import club.veluxpvp.practice.setting.menu.SettingButton;
import club.veluxpvp.practice.setting.menu.SettingsMenu;

public class SettingsMenuListener implements Listener {

	public SettingsMenuListener() {
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Menu openedMenu = MenuManager.getOpenedMenu(player);
		
		if(openedMenu != null && openedMenu instanceof SettingsMenu) {
			event.setCancelled(true);
			
			SettingsMenu menu = (SettingsMenu) openedMenu;
			Button clickedButton = menu.getClickedButton(event.getSlot());
			
			if(clickedButton != null && clickedButton instanceof SettingButton) {
				SettingButton button = (SettingButton) clickedButton;
				SettingType type = button.getType();
				String command = "";
				
				switch(type) {
				case SCOREBOARD:
					command = "togglescoreboard";
					break;
				case ALLOW_SPECTATORS:
					command = "togglespectators";
					break;
				case ALLOW_DUELS:
					command = "toggleduels";
					break;
				case TOURNAMENT_MESSAGES:
					command = "toggletournamentmessages";
					break;
				case PING_ON_SCOREBOARD:
					command = "togglepingonscoreboard";
					break;
				case RANKED_SIMILAR_PING:
					command = "togglesimilarping";
					break;
				}
				
				Bukkit.dispatchCommand(player, command);
				menu.openMenu(player);
			}
		}
	}
}
