package club.veluxpvp.practice.match;

import java.util.UUID;

import club.veluxpvp.practice.arena.Ladder;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class RankedMatchLog {

	private final String matchID;
	private UUID winnerUUID, loserUUID;
	private final String playedOn, arena, duration;
	private final Ladder ladder;
	private final HealingType healingType;
	private final int winnerTotalHits, loserTotalHits, winnerLongestCombo, loserLongestCombo, winnerHealingLeft, loserHealingLeft, winnerMissedPots, loserMissedPots;
	private int winnerElo, loserElo, eloUpdate;
}
