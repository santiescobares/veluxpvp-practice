package club.veluxpvp.practice.menu;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.ItemBuilder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Menu {

	private Player viewer;
	private Inventory inventory;
	private String title;
	private int size;
	private boolean autoUpdate, updateOnClick, fillEmptySpaces, removeOnClose;
	private Map<Integer, Button> buttons;
	private BukkitTask updateTask;
	
	public Menu(Player viewer) {
		this.viewer = viewer;
		this.inventory = null;
		this.title = "";
		this.size = 0;
		this.autoUpdate = false;
		this.updateOnClick = false;
		this.fillEmptySpaces = false;
		this.removeOnClose = true;
		this.buttons = new HashMap<>();
		this.updateTask = null;
	}
	
	public Inventory getInventory() {
		int size = getSize();
		
		if(size == 0) {
			int maxSlot = 0;
			
			for(int slotsEntry : getButtons().keySet()) {
				if(slotsEntry >= maxSlot) maxSlot = slotsEntry;
			}
			
			if(maxSlot <= 9) size = 9;
			if(maxSlot > 9 && maxSlot <= 18) size = 18;
			if(maxSlot > 18 && maxSlot <= 27) size = 27;
			if(maxSlot > 27 && maxSlot <= 36) size = 36;
			if(maxSlot > 36 && maxSlot <= 45) size = 45;
			if(maxSlot > 45 && maxSlot <= 54) size = 54;
		}
		
		String coloredName = ChatUtil.TRANSLATE(getTitle());
		this.inventory = Bukkit.createInventory(this.viewer, size, coloredName.length() > 32 ? coloredName.substring(0, 32) : coloredName);
		
		if(isFillEmptySpaces()) {
			ItemStack refill = new ItemBuilder().of(Material.STAINED_GLASS_PANE).dataValue((byte) 15).name(" ").build();
			
			for(int i = 0; i < this.inventory.getSize(); i++) {
				ItemStack item = this.inventory.getItem(i);
				
				if(item == null || item.getType() == Material.AIR) {
					this.inventory.setItem(i, refill);
				}
			}
		}
		
		for(Map.Entry<Integer, Button> buttonEntry : getButtons().entrySet()) {
			this.inventory.setItem(buttonEntry.getKey(), buttonEntry.getValue().build());
		}
		
		return this.inventory;
	}
	
	public void startAutoUpdate() {
		final Inventory originalInventory = this.inventory;
		
		this.updateTask = new BukkitRunnable() {

			@Override
			public void run() {
				Inventory inventory = viewer.getOpenInventory().getTopInventory();
				
				if(inventory == null || inventory != originalInventory) {
					this.cancel();
					updateTask = null;
					return;
				}
				
				for(Map.Entry<Integer, Button> buttonEntry : getButtons().entrySet()) {
					inventory.setItem(buttonEntry.getKey(), new ItemStack(Material.AIR));
					inventory.setItem(buttonEntry.getKey(), buttonEntry.getValue().build());
				}
			}
			
		}.runTaskTimerAsynchronously(Practice.getInstance(), 3L, 3L);
	}
	
	public Button getClickedButton(int slot) {
		return getButtons().get(slot);
	}
	
	public void openMenu(Player player) {
		MenuManager.openedMenu.remove(player.getUniqueId());
		player.openInventory(getInventory());
		player.updateInventory();
		MenuManager.openedMenu.put(player.getUniqueId(), this);
		
		if(this.autoUpdate && this.updateTask == null) this.startAutoUpdate();
	}
}
