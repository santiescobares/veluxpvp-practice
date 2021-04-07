package club.veluxpvp.practice.duel.menu;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import club.veluxpvp.practice.arena.Arena;
import club.veluxpvp.practice.menu.Button;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class DuelArenaButton extends Button {

	@Getter private Arena arena;
	
	@Override
	public Material getMaterial() {
		return this.arena.getIcon() != null ? this.arena.getIcon().getType() : Material.DIAMOND_SWORD;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public byte getDataValue() {
		return this.arena.getIcon() != null ? this.arena.getIcon().getData().getData() : (byte) 0;
	}
	
	@Override
	public String getName() {
		return ChatColor.AQUA + this.arena.getName();
	}
	
	@Override
	public List<String> getLore() {
		return Arrays.asList(
				"&7* &aClick to select this arena!"
				);
	}
}
