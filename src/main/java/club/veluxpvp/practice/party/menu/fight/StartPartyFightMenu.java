package club.veluxpvp.practice.party.menu.fight;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.menu.Button;
import club.veluxpvp.practice.menu.Menu;

public class StartPartyFightMenu extends Menu {

	public StartPartyFightMenu(Player viewer) {
		super(viewer);
	}
	
	@Override
	public String getTitle() {
		return "Select a party fight type";
	}
	
	@Override
	public int getSize() {
		return 27;
	}
	
	@Override
	public Map<Integer, Button> getButtons() {
		Map<Integer, Button> buttons = new ConcurrentHashMap<>();
		
		buttons.put(11, new FFAPartyFightButton());
		buttons.put(15, new SplitPartyFightButton());
		
		return buttons;
	}
}
