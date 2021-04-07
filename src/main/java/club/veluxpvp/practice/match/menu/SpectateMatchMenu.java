package club.veluxpvp.practice.match.menu;

import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.menu.Button;
import club.veluxpvp.practice.menu.Menu;

public class SpectateMatchMenu extends Menu {

	public SpectateMatchMenu(Player viewer) {
		super(viewer);
	}

	@Override
	public String getTitle() {
		return "Select a Match";
	}
	
	@Override
	public int getSize() {
		return 54;
	}
	
	@Override
	public Map<Integer, Button> getButtons() {
		Map<Integer, Button> buttons = Maps.newConcurrentMap();
		List<Match> matches = Lists.newArrayList();
		Ladder[] ladders = Ladder.values();
		
		for(int i = 0; i < ladders.length; i++) {
			final int index = i;
			
			Practice.getInstance().getMatchManager().getMatches()
			.stream()
			.filter(m -> m.getLadder() == ladders[index])
			.forEach(m -> matches.add(m));
		}
		
		int slot = 0;
		for(Match m : matches) {
			if(slot == 49) continue;
			buttons.put(slot++, new SpectateMatchButton(m));
		}
		
		buttons.put(49, new SpectateRandomMatchButton());
		
		return buttons;
	}
}
