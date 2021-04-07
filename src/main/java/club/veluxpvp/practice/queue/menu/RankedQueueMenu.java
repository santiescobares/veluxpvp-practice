package club.veluxpvp.practice.queue.menu;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.menu.Button;
import club.veluxpvp.practice.menu.Menu;

public class RankedQueueMenu extends Menu {

	public RankedQueueMenu(Player viewer) {
		super(viewer);
		
		setTitle(ChatColor.BLUE + "Select a Ranked Ladder");
		setAutoUpdate(true);
	}
	
	@Override
	public int getSize() {
		return 18;
	}
	
	@Override
	public Map<Integer, Button> getButtons() {
		Map<Integer, Button> buttons = new HashMap<>();
		Ladder[] ladders = Ladder.values();
		
		int slot = 0;
		for(int i = 0; i < ladders.length; i++) {
			if(ladders[i] == Ladder.FINAL_UHC || ladders[i] == Ladder.COMBO_FLY || ladders[i] == Ladder.HCF || ladders[i] == Ladder.HCT_NO_DEBUFF || ladders[i] == Ladder.HCT_DEBUFF) continue;
			
			buttons.put(slot++, new RankedQueueButton(ladders[i]));
		}
		
		return buttons;
	}
}
