package club.veluxpvp.practice.tournament;

import club.veluxpvp.practice.arena.Ladder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TournamentManager {

	private Tournament activeTournament;
	
	public TournamentManager() {
		this.activeTournament = null;
	}
	
	public void startTournament(Ladder ladder, int teamSize, int teamLimit) {
		Tournament tour = new Tournament(ladder, teamSize, teamLimit);
		this.activeTournament = tour;
	}
}
