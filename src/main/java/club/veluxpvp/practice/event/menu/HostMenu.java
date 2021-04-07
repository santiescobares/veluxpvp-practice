package club.veluxpvp.practice.event.menu;

import java.util.Map;

import org.bukkit.entity.Player;

import com.google.common.collect.Maps;

import club.veluxpvp.practice.event.EventType;
import club.veluxpvp.practice.menu.Button;
import club.veluxpvp.practice.menu.Menu;

public class HostMenu extends Menu {

	public HostMenu(Player viewer) {
		super(viewer);
	}

	@Override
	public String getTitle() {
		return "Select an Event";
	}
	
	@Override
	public boolean isFillEmptySpaces() {
		return true;
	}
	
	@Override
	public Map<Integer, Button> getButtons() {
		Map<Integer, Button> buttons = Maps.newConcurrentMap();
		EventType[] types = EventType.values();
		
		for(int i = 0; i < types.length; i++) {
			EventType e = types[i];
			
			switch(e) {
			case SUMO:
				buttons.put(13, new HostButton(e));
				break;
			}
		}
		
		return buttons;
	}
}
