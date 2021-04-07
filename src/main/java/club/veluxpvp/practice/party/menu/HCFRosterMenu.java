package club.veluxpvp.practice.party.menu;

import java.util.Map;

import org.bukkit.entity.Player;

import com.google.common.collect.Maps;

import club.veluxpvp.practice.menu.Button;
import club.veluxpvp.practice.menu.Menu;
import club.veluxpvp.practice.party.Party;
import club.veluxpvp.practice.party.PartyMember;

public class HCFRosterMenu extends Menu {

	private Party party;
	
	public HCFRosterMenu(Player viewer, Party party) {
		super(viewer);
		this.party = party;
	}

	@Override
	public String getTitle() {
		return "Party HCF Roster";
	}
	
	@Override
	public Map<Integer, Button> getButtons() {
		Map<Integer, Button> buttons = Maps.newConcurrentMap();
		
		int slot = 0;
		for(PartyMember pm : party.getMembers()) {
			buttons.put(slot++, new HCFRosterButton(pm));
		}
		
		return buttons;
	}
}
