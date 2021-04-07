package club.veluxpvp.practice.match;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum HealingType {
	NONE,
	HEALTH_POTION,
	GOLDEN_APPLE,
	GAPPLE,
	SOUP;
	
	public ItemStack getItem() {
		switch(this) {
		case HEALTH_POTION:
			return new ItemStack(Material.POTION, 1, (short) 16421);
		case GOLDEN_APPLE:
			return new ItemStack(Material.GOLDEN_APPLE);
		case GAPPLE:
			return new ItemStack(Material.GOLDEN_APPLE, 1, (byte) 1);
		case SOUP:
			return new ItemStack(Material.MUSHROOM_SOUP);
		default:
			return null;
		}
	}
}
