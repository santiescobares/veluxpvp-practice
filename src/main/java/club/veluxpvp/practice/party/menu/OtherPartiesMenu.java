package club.veluxpvp.practice.party.menu;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import com.google.common.collect.Maps;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.menu.Button;
import club.veluxpvp.practice.menu.Menu;
import club.veluxpvp.practice.party.Party;

public class OtherPartiesMenu extends Menu {

	public OtherPartiesMenu(Player viewer) {
		super(viewer);
	}

	@Override
	public String getTitle() {
		return "Other Parties";
	}
	
	@Override
	public Map<Integer, Button> getButtons() {
		Map<Integer, Button> buttons = Maps.newConcurrentMap();
		Set<Party> partiesInLobby = Practice.getInstance().getPartyManager().getParties().stream().filter(p -> p.isInLobby()).collect(Collectors.toSet());
		
		int slot = 0;
		for(Party p : partiesInLobby) {
			buttons.put(slot++, new OtherPartyButton(p));
		}
		
		return buttons;
	}
}
