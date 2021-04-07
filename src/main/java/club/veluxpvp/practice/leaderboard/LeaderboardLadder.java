package club.veluxpvp.practice.leaderboard;

import club.veluxpvp.practice.arena.Ladder;

public enum LeaderboardLadder {
	NO_DEBUFF("No Debuff"),
	DEBUFF("Debuff"),
	BUILD_UHC("Build UHC"),
	HG("HG"),
	GAPPLE("GApple"),
	SUMO("Sumo"),
	SOUP("Soup"),
	ARCHER("Archer"),
	PARKOUR("Parkour"),
	BRIDGES("Bridges"),
	GLOBAL("Global");
	
	public final String name;
	
	private LeaderboardLadder(String name) {
		this.name = name;
	}
	
	public Ladder getLadder() {
		switch(this) {
		case NO_DEBUFF:
			return Ladder.NO_DEBUFF;
		case DEBUFF:
			return Ladder.DEBUFF;
		case BUILD_UHC:
			return Ladder.BUILD_UHC;
		case HG:
			return Ladder.HG;
		case GAPPLE:
			return Ladder.GAPPLE;
		case SUMO:
			return Ladder.SUMO;
		case SOUP:
			return Ladder.SOUP;
		case ARCHER:
			return Ladder.ARCHER;
		case PARKOUR:
			return Ladder.PARKOUR;
		case BRIDGES:
			return Ladder.BRIDGES;
		default:
			return null;
		}
	}
	
	public static LeaderboardLadder getByLadder(Ladder ladder) {
		switch(ladder) {
		case NO_DEBUFF:
			return LeaderboardLadder.NO_DEBUFF;
		case DEBUFF:
			return LeaderboardLadder.DEBUFF;
		case BUILD_UHC:
			return LeaderboardLadder.BUILD_UHC;
		case HG:
			return LeaderboardLadder.HG;
		case GAPPLE:
			return LeaderboardLadder.GAPPLE;
		case SUMO:
			return LeaderboardLadder.SUMO;
		case SOUP:
			return LeaderboardLadder.SOUP;
		case ARCHER:
			return LeaderboardLadder.ARCHER;
		case PARKOUR:
			return LeaderboardLadder.PARKOUR;
		case BRIDGES:
			return LeaderboardLadder.BRIDGES;
		default:
			return null;
		}
	}
}
