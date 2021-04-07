package club.veluxpvp.practice.tournament.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.match.MatchEndReason;
import club.veluxpvp.practice.match.MatchTeam;
import club.veluxpvp.practice.match.event.MatchEndEvent;
import club.veluxpvp.practice.party.Party;
import club.veluxpvp.practice.tournament.Tournament;
import club.veluxpvp.practice.tournament.TournamentManager;

public class TournamentListener implements Listener {

	private TournamentManager tm;
	
	public TournamentListener() {
		this.tm = Practice.getInstance().getTournamentManager();
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler
	public void onMatchEnd(MatchEndEvent event) {
		Match match = event.getMatch();
		Tournament tour = tm.getActiveTournament();
		
		if(tour == null || match.isFfa() || match.isRanked() || match.getEndReason() == MatchEndReason.CANCELLED) return;
		
		MatchTeam winnerTeam = match.getLastAliveTeam();
		
		if(winnerTeam == null) return;
		
		Party party = Practice.getInstance().getPartyManager().getPlayerParty(winnerTeam.getFirstPlayer());
		
		if(party != null && tour.isInTournament(party)) {
			MatchTeam loserTeam = winnerTeam == match.getTeam1() ? match.getTeam2() : match.getTeam1();
			Party loserParty = Practice.getInstance().getPartyManager().getPlayerParty(loserTeam.getFirstPlayer());
			
			if(loserParty != null && tour.isInTournament(loserParty)) tour.removeParticipant(loserParty, true);
			tour.getLiveMatches().remove(match);
			tour.getOpponentsDefeated().put(party, tour.getOpponentsDefeated().getOrDefault(party, 0) + 1);
			tour.tryFinishRound();
		}
	}
}
