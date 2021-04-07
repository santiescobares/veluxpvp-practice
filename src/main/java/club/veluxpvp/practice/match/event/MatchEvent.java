package club.veluxpvp.practice.match.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import club.veluxpvp.practice.match.Match;
import lombok.Getter;

public class MatchEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();
	
	@Getter private Match match;
	
	public MatchEvent(Match match) {
		this.match = match;
	}
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
