package club.veluxpvp.practice.event.sumo;

import java.util.List;

import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SumoTeam {

	private List<Player> players;
	private int opponentsDefeated;
	private boolean eliminated;
	
	public SumoTeam() {
		this.players = Lists.newArrayList();
		this.opponentsDefeated = 0;
		this.eliminated = false;
	}
	
	public Player getFirstPlayer() {
		if(this.players.size() == 0) return null;
		
		return this.players.get(0);
	}
}
