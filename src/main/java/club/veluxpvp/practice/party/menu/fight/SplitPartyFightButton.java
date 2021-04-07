package club.veluxpvp.practice.party.menu.fight;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import club.veluxpvp.practice.menu.Button;

public class SplitPartyFightButton extends Button {

	@Override
	public Material getMaterial() {
		return Material.STONE_SWORD;
	}
	
	@Override
	public String getName() {
		return ChatColor.AQUA + "Party Split";
	}
	
	@Override
	public List<String> getLore() {
		return Arrays.asList(
				"&7Split the party and start a",
				"&7match between two teams formed",
				"&7by the party members.",
				" ",
				"&aLast team alive wins!"
				);
	}
}
