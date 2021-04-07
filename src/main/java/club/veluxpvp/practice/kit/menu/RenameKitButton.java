package club.veluxpvp.practice.kit.menu;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import club.veluxpvp.practice.menu.Button;

public class RenameKitButton extends Button {

	@Override
	public Material getMaterial() {
		return Material.NAME_TAG;
	}
	
	@Override
	public String getName() {
		return ChatColor.AQUA + "Rename Kit";
	}
	
	@Override
	public List<String> getLore() {
		return Arrays.asList(
				"&7* &fClick to &brename &fthis kit!"
				);
	}
}
