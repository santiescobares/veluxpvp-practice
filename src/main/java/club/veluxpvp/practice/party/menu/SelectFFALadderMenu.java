package club.veluxpvp.practice.party.menu;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.menu.Button;
import club.veluxpvp.practice.menu.Menu;

public class SelectFFALadderMenu extends Menu {

	public SelectFFALadderMenu(Player viewer) {
		super(viewer);
	}
	
	@Override
	public String getTitle() {
		return "Select a Ladder";
	}
	
	@Override
	public Map<Integer, Button> getButtons() {
		Map<Integer, Button> buttons = new HashMap<>();
		Ladder[] ladders = Ladder.values();
		
		int slot = 0;
		for(int i = 0; i < ladders.length; i++) {
			if(ladders[i] == Ladder.HCT_NO_DEBUFF || ladders[i] == Ladder.HCT_DEBUFF || ladders[i] == Ladder.BRIDGES) continue;
			
			buttons.put(slot++, new SelectFFALadderButton(ladders[i]));
		}
		
		return buttons;
	}
}
