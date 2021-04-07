package club.veluxpvp.practice.party.menu.fight;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import club.veluxpvp.practice.menu.Button;

public class FFAPartyFightButton extends Button {

	@Override
	public Material getMaterial() {
		return Material.GOLD_SWORD;
	}
	
	@Override
	public String getName() {
		return ChatColor.AQUA + "Party FFA";
	}
	
	@Override
	public List<String> getLore() {
		return Arrays.asList(
				"&7Start a \"Free For All\"",
				"&7between all party members.",
				" ",
				"&aLast player alive wins!"
				);
	}
}
