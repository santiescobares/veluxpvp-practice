package club.veluxpvp.practice.party.pvpclass;

import org.bukkit.ChatColor;

public enum HCFClassType {
	DIAMOND("Diamond"),
	BARD("Bard"),
	ROGUE("Rogue"),
	ARCHER("Archer");
	
	public final String name;
	
	private HCFClassType(String name) {
		this.name = name;
	}
	
	public ChatColor getColor() {
		switch(this) {
		case DIAMOND:
			return ChatColor.AQUA;
		case BARD:
			return ChatColor.YELLOW;
		case ARCHER:
			return ChatColor.RED;
		default:
			return ChatColor.GRAY;
		}
	}
	
	public static HCFClassType getByName(String name) {
		switch(name.toLowerCase()) {
		case "diamond":
			return HCFClassType.DIAMOND;
		case "bard":
			return HCFClassType.BARD;
		case "rogue":
			return HCFClassType.ROGUE;
		case "archer":
			return HCFClassType.ARCHER;
		default:
			return null;
		}
	}
}
