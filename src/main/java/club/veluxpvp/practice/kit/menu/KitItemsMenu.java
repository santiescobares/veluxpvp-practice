package club.veluxpvp.practice.kit.menu;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Maps;

import club.veluxpvp.practice.kit.KitType;
import club.veluxpvp.practice.menu.Button;
import club.veluxpvp.practice.menu.Menu;

public class KitItemsMenu extends Menu {

	private KitType kitType;
	
	public KitItemsMenu(Player viewer, KitType kitType) {
		super(viewer);
		this.kitType = kitType;
	}

	@Override
	public int getSize() {
		return 27;
	}
	
	@Override
	public Map<Integer, Button> getButtons() {
		Map<Integer, Button> buttons = Maps.newConcurrentMap();
		
		if(kitType == KitType.NO_DEBUFF || kitType == KitType.DEBUFF || kitType == KitType.HCT_DIAMOND_NO_DEBUFF || kitType == KitType.HCT_DIAMOND_DEBUFF) {
			buttons.put(0, new KitItemButton(new ItemStack(Material.COOKED_BEEF, 64)));
			buttons.put(1, new KitItemButton(new ItemStack(Material.GRILLED_PORK, 64)));
			buttons.put(2, new KitItemButton(new ItemStack(Material.BAKED_POTATO, 64)));
			buttons.put(3, new KitItemButton(new ItemStack(Material.GOLDEN_CARROT, 64)));
			buttons.put(4, new KitItemButton(new ItemStack(Material.POTION, 1, (byte) 8259)));
			buttons.put(5, new KitItemButton(new ItemStack(Material.POTION, 1, (byte) 8259)));
			buttons.put(6, new KitItemButton(new ItemStack(Material.POTION, 1, (byte) 8226)));
			buttons.put(7, new KitItemButton(new ItemStack(Material.POTION, 1, (byte) 8226)));
			buttons.put(8, new KitItemButton(new ItemStack(Material.POTION, 1, (byte) 8226)));
			
			for(int i = 0; i < 27; i++) {
				buttons.putIfAbsent(i, new KitItemButton(new ItemStack(Material.POTION, 1, (short) 16421)));
			}
		} else if(kitType == KitType.SOUP) {
			buttons.put(0, new KitItemButton(new ItemStack(Material.POTION, 1, (byte) 8226)));
			buttons.put(1, new KitItemButton(new ItemStack(Material.POTION, 1, (byte) 8226)));
			buttons.put(2, new KitItemButton(new ItemStack(Material.POTION, 1, (byte) 8226)));
			
			for(int i = 0; i < 27; i++) {
				buttons.putIfAbsent(i, new KitItemButton(new ItemStack(Material.MUSHROOM_SOUP, 1)));
			}
		}
		
		return buttons;
	}
}
