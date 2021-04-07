package club.veluxpvp.practice.match.event;

import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.match.MatchEndReason;
import lombok.Getter;

@Getter
public class MatchEndEvent extends MatchEvent {

	private MatchEndReason endReason;
	
	public MatchEndEvent(Match match, MatchEndReason endReason) {
		super(match);
		
		this.endReason = endReason;
	}
}
