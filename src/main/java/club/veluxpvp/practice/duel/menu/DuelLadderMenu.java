package club.veluxpvp.practice.duel.menu;

import java.util.Map;

import org.bukkit.entity.Player;

import com.google.common.collect.Maps;

import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.menu.Button;
import club.veluxpvp.practice.menu.Menu;

public class DuelLadderMenu extends Menu {

	public DuelLadderMenu(Player viewer) {
		super(viewer);
	}

	@Override
	public String getTitle() {
		return "Select a Ladder";
	}
	
	@Override
	public Map<Integer, Button> getButtons() {
		Map<Integer, Button> buttons = Maps.newConcurrentMap();
		Ladder[] ladders = Ladder.values();
		
		int slot = 0;
		for(int i = 0; i < ladders.length; i++) {
			if(ladders[i] == Ladder.HCT_NO_DEBUFF || ladders[i] == Ladder.HCT_DEBUFF) continue;
			
			buttons.put(slot++, new DuelLadderButton(ladders[i]));
		}
		
		return buttons;
	}
}
