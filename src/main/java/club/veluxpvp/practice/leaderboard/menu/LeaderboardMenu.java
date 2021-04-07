package club.veluxpvp.practice.leaderboard.menu;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;

import club.veluxpvp.practice.leaderboard.LeaderboardLadder;
import club.veluxpvp.practice.menu.Button;
import club.veluxpvp.practice.menu.Menu;

public class LeaderboardMenu extends Menu {

	public LeaderboardMenu(Player viewer) {
		super(viewer);
	}

	@Override
	public String getTitle() {
		return ChatColor.BLUE + "Leaderboards";
	}
	
	@Override
	public int getSize() {
		return 36;
	}
	
	@Override
	public boolean isFillEmptySpaces() {
		return true;
	}
	
	@Override
	public Map<Integer, Button> getButtons() {
		Map<Integer, Button> buttons = Maps.newConcurrentMap();
		LeaderboardLadder[] ladders = LeaderboardLadder.values();
		
		int slot = 19;
		for(int i = 0; i < ladders.length; i++) {
			if(ladders[i] == LeaderboardLadder.GLOBAL) {
				buttons.put(4, new LeaderboardLadderButton(ladders[i]));
				continue;
			}
			
			if(slot == 26) {
				buttons.put(30, new LeaderboardLadderButton(ladders[i]));
				slot++;
			} else if(slot == 27) {
				buttons.put(31, new LeaderboardLadderButton(ladders[i]));
				slot++;
			} else if(slot == 28) {
				buttons.put(32, new LeaderboardLadderButton(ladders[i]));
				slot++;
			} else {
				buttons.put(slot++, new LeaderboardLadderButton(ladders[i]));
			}
		}
		
		return buttons;
	}
}
