package club.veluxpvp.practice.kit.menu;

import java.util.Map;

import org.bukkit.entity.Player;

import com.google.common.collect.Maps;

import club.veluxpvp.practice.kit.KitType;
import club.veluxpvp.practice.menu.Button;
import club.veluxpvp.practice.menu.Menu;

public class KitEditorMenu extends Menu {

	public KitEditorMenu(Player viewer) {
		super(viewer);
	}

	@Override
	public String getTitle() {
		return "Select a Ladder";
	}
	
	@Override
	public Map<Integer, Button> getButtons() {
		Map<Integer, Button> buttons = Maps.newConcurrentMap();
		KitType[] types = KitType.values();
		
		int slot = 0;
		for(int i = 0; i < types.length; i++) {
			if(!types[i].getLadder().canEditKit || types[i] == KitType.BRIDGES_BLUE) continue;
			
			buttons.put(slot++, new KitEditorLadderButton(types[i]));
		}
		
		return buttons;
	}
}
