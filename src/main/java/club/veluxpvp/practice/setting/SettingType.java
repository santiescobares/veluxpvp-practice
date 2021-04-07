package club.veluxpvp.practice.setting;

import org.bukkit.Material;

public enum SettingType {
	SCOREBOARD("Scoreboard"),
	ALLOW_SPECTATORS("Spectators"),
	ALLOW_DUELS("Duel Requests"),
	TOURNAMENT_MESSAGES("Tournament Messages"),
	PING_ON_SCOREBOARD("Ping on Scoreboard"),
	RANKED_SIMILAR_PING("Similar Ping (Ranked)");
	
	public final String displayName;
	
	private SettingType(String displayName) {
		this.displayName = displayName;
	}
	
	public Material getMaterial() {
		switch(this) {
		case SCOREBOARD:
			return Material.PAPER;
		case ALLOW_SPECTATORS:
			return Material.LEASH;
		case ALLOW_DUELS:
			return Material.INK_SACK;
		case TOURNAMENT_MESSAGES:
			return Material.FEATHER;
		case PING_ON_SCOREBOARD:
			return Material.LEVER;
		default:
			return Material.DIAMOND_SWORD;
		}
	}
}
