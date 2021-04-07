package club.veluxpvp.practice.kit.menu;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import club.veluxpvp.practice.menu.Button;

public class DeleteKitButton extends Button {

	@Override
	public Material getMaterial() {
		return Material.INK_SACK;
	}
	
	@Override
	public byte getDataValue() {
		return (byte) 1;
	}
	
	@Override
	public String getName() {
		return ChatColor.AQUA + "Delete Kit";
	}
	
	@Override
	public List<String> getLore() {
		return Arrays.asList(
				"&7* &fClick to &cdelete &fthis kit!"
				);
	}
}
