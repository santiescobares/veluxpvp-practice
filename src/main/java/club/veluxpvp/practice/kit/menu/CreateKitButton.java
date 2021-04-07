package club.veluxpvp.practice.kit.menu;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import club.veluxpvp.practice.menu.Button;

public class CreateKitButton extends Button {

	@Override
	public Material getMaterial() {
		return Material.CHEST;
	}
	
	@Override
	public String getName() {
		return ChatColor.AQUA + "Create Kit";
	}
	
	@Override
	public List<String> getLore() {
		return Arrays.asList(
				"&7* &fClick to &acreate &fa new kit!"
				);
	}
}
