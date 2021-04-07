package club.veluxpvp.practice.kit.menu.listener;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.google.common.collect.Maps;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.kit.Kit;
import club.veluxpvp.practice.kit.KitManager;
import club.veluxpvp.practice.kit.menu.CreateKitButton;
import club.veluxpvp.practice.kit.menu.DeleteKitButton;
import club.veluxpvp.practice.kit.menu.EditKitsMenu;
import club.veluxpvp.practice.kit.menu.RenameKitButton;
import club.veluxpvp.practice.kit.menu.SaveKitButton;
import club.veluxpvp.practice.menu.Button;
import club.veluxpvp.practice.menu.Menu;
import club.veluxpvp.practice.menu.MenuManager;
import club.veluxpvp.practice.utilities.ChatUtil;

public class EditKitsMenuListener implements Listener {

	public static Map<UUID, Kit> renamingKit = Maps.newHashMap();
	
	public EditKitsMenuListener() {
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Menu openedMenu = MenuManager.getOpenedMenu(player);
		
		if(openedMenu != null && openedMenu instanceof EditKitsMenu) {
			event.setCancelled(true);
			
			EditKitsMenu menu = (EditKitsMenu) openedMenu;
			Button clickedButton = menu.getClickedButton(event.getSlot());
			KitManager km = Practice.getInstance().getKitManager();
			
			if(clickedButton == null) return;
			if(clickedButton instanceof CreateKitButton) {
				Kit kit = new Kit(player.getUniqueId(), menu.getKitType(), false, km.getKitNumberBySlot(event.getSlot()));
				Kit defaultKit = km.getDefaultKit(menu.getKitType());

				player.updateInventory();
				kit.setContents(player.getInventory().getContents());
				if(defaultKit != null && defaultKit.getArmorContents() != null) kit.setArmorContents(defaultKit.getArmorContents());
			
				km.getKits().add(kit);
				
				menu.openMenu(player);
				return;
			}
			
			if(clickedButton instanceof RenameKitButton) {
				Kit kit = km.getClickedKit(player, menu.getKitType(), event.getSlot());
				
				if(kit == null) return;
				
				renamingKit.put(player.getUniqueId(), kit);
				player.closeInventory();
				player.sendMessage(ChatUtil.TRANSLATE("&aPlease enter the new kit name in the chat. Type &cCancel &ato cancel the process. &7(You can include color codes)"));
				
				return;
			}
			
			if(clickedButton instanceof SaveKitButton) {
				Kit kit = km.getClickedKit(player, menu.getKitType(), event.getSlot() - 9);
				
				if(kit == null) return;
				
				player.updateInventory();
				kit.setContents(player.getInventory().getContents());
				return;
			}
			
			if(clickedButton instanceof DeleteKitButton) {
				Kit kit = km.getClickedKit(player, menu.getKitType(), event.getSlot() - 18);
				
				if(kit == null) return;
				
				km.getKits().remove(kit);
				menu.openMenu(player);
				return;
			}
		}
	}
}
