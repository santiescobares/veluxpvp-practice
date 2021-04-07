package club.veluxpvp.practice.queue.menu;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.menu.Button;
import club.veluxpvp.practice.menu.Menu;

public class UnrankedQueueMenu extends Menu {

	public UnrankedQueueMenu(Player viewer) {
		super(viewer);
		
		setTitle(ChatColor.BLUE + "Select an Unranked Ladder");
		setAutoUpdate(true);
	}
	
	@Override
	public Map<Integer, Button> getButtons() {
		Map<Integer, Button> buttons = new HashMap<>();
		Ladder[] ladders = Ladder.values();
		
		int slot = 0;
		for(int i = 0; i < ladders.length; i++) {
			if(ladders[i] == Ladder.HCT_NO_DEBUFF || ladders[i] == Ladder.HCT_DEBUFF) continue;
			
			buttons.put(slot++, new UnrankedQueueButton(ladders[i]));
		}
		
		return buttons;
	}
}
