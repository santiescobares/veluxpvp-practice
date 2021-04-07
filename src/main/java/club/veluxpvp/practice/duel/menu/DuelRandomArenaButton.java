package club.veluxpvp.practice.duel.menu;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import club.veluxpvp.practice.menu.Button;

public class DuelRandomArenaButton extends Button {

	@Override
	public Material getMaterial() {
		return Material.NETHER_STAR;
	}
	
	@Override
	public String getName() {
		return ChatColor.AQUA + "Random Arena";
	}
	
	@Override
	public List<String> getLore() {
		return Arrays.asList(
				"&7* &aClick to select a random arena!"
				);
	}
}
