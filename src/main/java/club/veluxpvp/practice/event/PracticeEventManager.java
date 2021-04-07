package club.veluxpvp.practice.event;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;

public class PracticeEventManager {

	@Getter @Setter private PracticeEvent activeEvent;
	private long eventCooldown;
	
	public PracticeEventManager() {
		this.activeEvent = null;
		this.eventCooldown = System.currentTimeMillis();
	}
	
	public boolean isEventCooldown() {
		return System.currentTimeMillis() < this.eventCooldown;
	}
	
	public void setEventCooldown() {
		this.eventCooldown = System.currentTimeMillis() + (30 * 1000);
	}
	
	public boolean isOnEvent(Player player) {
		return this.activeEvent != null && this.activeEvent.getPlayers().contains(player);
	}
	
	public boolean isPlaying(Player player) {
		return this.activeEvent != null && this.activeEvent.isParticipating(player);
	}
}
