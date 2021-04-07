package club.veluxpvp.practice.party.menu;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.menu.Button;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class SelectSplitLadderButton extends Button {

	@Getter private Ladder ladder;
	
	@Override
	public Material getMaterial() {
		return this.ladder.getMaterial();
	}
	
	@Override
	public byte getDataValue() {
		return this.ladder.getDataValue();
	}
	
	@Override
	public String getName() {
		return ChatColor.AQUA + this.ladder.name;
	}
	
	@Override
	public List<String> getLore() {
		return Arrays.asList(
				" ",
				"&aClick to select &e" + this.ladder.name + "&a!"
				);
	}
}
