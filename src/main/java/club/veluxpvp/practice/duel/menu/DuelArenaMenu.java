package club.veluxpvp.practice.duel.menu;

import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import com.google.common.collect.Maps;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Arena;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.menu.Button;
import club.veluxpvp.practice.menu.Menu;

public class DuelArenaMenu extends Menu {

	private Ladder ladder;
	
	public DuelArenaMenu(Player viewer, Ladder ladder) {
		super(viewer);
		this.ladder = ladder;
	}

	@Override
	public String getTitle() {
		return "Select an Arena";
	}
	
	@Override
	public int getSize() {
		List<Arena> arenas = Practice.getInstance().getArenaManager().getArenasForLadder(this.ladder);
		int size = 9;
		
		if(arenas.size() > 8) size = 18;
		if(arenas.size() > 17) size = 27;
		if(arenas.size() > 26) size = 36;
		if(arenas.size() > 35) size = 45;
		if(arenas.size() > 44) size = 54;
		
		return size;
	}
	
	@Override
	public Map<Integer, Button> getButtons() {
		Map<Integer, Button> buttons = Maps.newConcurrentMap();
		List<Arena> arenas = Practice.getInstance().getArenaManager().getArenasForLadder(this.ladder);
		
		int slot = 0;
		for(Arena a : arenas) {
			buttons.put(slot++, new DuelArenaButton(a));
		}
		
		buttons.put(slot, new DuelRandomArenaButton());
		
		return buttons;
	}
}
