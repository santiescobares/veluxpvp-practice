package club.veluxpvp.practice.kit.menu;

import org.bukkit.inventory.ItemStack;

import club.veluxpvp.practice.menu.Button;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class KitItemButton extends Button {

	@Getter private ItemStack itemStack;
	
	@Override
	public ItemStack getItem() {
		return this.itemStack;
	}
}
