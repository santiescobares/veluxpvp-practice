package club.veluxpvp.practice.leaderboard.menu;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import com.google.common.collect.Lists;

import club.veluxpvp.practice.leaderboard.LeaderboardLadder;
import club.veluxpvp.practice.leaderboard.LeaderboardManager;
import club.veluxpvp.practice.menu.Button;
import club.veluxpvp.practice.utilities.ChatUtil;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LeaderboardLadderButton extends Button {

	private LeaderboardLadder ladder;
	
	@Override
	public Material getMaterial() {
		return this.ladder == LeaderboardLadder.GLOBAL ? Material.NETHER_STAR : this.ladder.getLadder().getMaterial();
	}
	
	@Override
	public byte getDataValue() {
		return this.ladder.getLadder() == null ? (byte) 0 : this.ladder.getLadder().getDataValue();
	}
	
	@Override
	public String getName() {
		return ChatColor.AQUA + "Leaderboard " + ChatColor.GRAY + " - " + ChatColor.WHITE + this.ladder.name;
	}
	
	@Override
	public List<String> getLore() {
		List<String> lines = Lists.newArrayList();
		
		lines.add(ChatUtil.INV_LINE());
		
		int position = 1;
		List<String> top = LeaderboardManager.getTopLadder(this.ladder, 10);
		
		for(String line : top) {
			String positionColor = position == 1 ? "&a" : position == 2 ? "&e" : position == 3 ? "&c" : "&7";
			
			lines.add(positionColor + (position++) + ". &f" + line);
		}
		
		lines.add(ChatUtil.INV_LINE());
		
		return lines;
	}
}
