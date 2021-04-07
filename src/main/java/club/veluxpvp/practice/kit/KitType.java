package club.veluxpvp.practice.kit;

import club.veluxpvp.practice.arena.Ladder;

public enum KitType {
	NO_DEBUFF("No Debuff"),
	DEBUFF("Debuff"),
	BUILD_UHC("Build UHC"),
	FINAL_UHC("Final UHC"),
	HG("HG"),
	GAPPLE("GApple"),
	COMBO_FLY("Combo Fly"),
	SUMO("Sumo"),
	SOUP("Soup"),
	ARCHER("Archer"),
	PARKOUR("Parkour"),
	HCF("HCF"),
	BRIDGES_RED("Bridges"),
	BRIDGES_BLUE("Bridges"),
	HCT_DIAMOND_NO_DEBUFF("HCF TeamFight (Diamond - No Debuff)"),
	HCT_DIAMOND_DEBUFF("HCF TeamFight (Diamond - Debuff)"),
	HCT_BARD("HCF TeamFight (Bard)"),
	HCT_ROGUE("HCF TeamFight (Rogue)"),
	HCT_ARCHER("HCF TeamFight (Archer)");
	
	public final String name;
	
	private KitType(String name) {
		this.name = name;
	}
	
	public static KitType getByName(String name) {
		KitType[] types = KitType.values();
		
		for(int i = 0; i < types.length; i++) {
			if(name.equalsIgnoreCase(types[i].name())) return types[i];
		}
		
		return null;
	}
	
	public static KitType getKitLadder(Ladder ladder) {
		switch(ladder) {
		case NO_DEBUFF:
			return KitType.NO_DEBUFF;
		case DEBUFF:
			return KitType.DEBUFF;
		case BUILD_UHC:
			return KitType.BUILD_UHC;
		case FINAL_UHC:
			return KitType.FINAL_UHC;
		case HG:
			return KitType.HG;
		case GAPPLE:
			return KitType.GAPPLE;
		case COMBO_FLY:
			return KitType.COMBO_FLY;
		case SUMO:
			return KitType.SUMO;
		case SOUP:
			return KitType.SOUP;
		case ARCHER:
			return KitType.ARCHER;
		case PARKOUR:
			return KitType.PARKOUR;
		case HCF:
			return KitType.HCF;
		case BRIDGES:
			return KitType.BRIDGES_RED;
		case HCT_NO_DEBUFF:
			return KitType.HCT_DIAMOND_NO_DEBUFF;
		case HCT_DEBUFF:
			return KitType.HCT_DIAMOND_DEBUFF;
		default:
			return null;
		}
	}
	
	public Ladder getLadder() {
		switch(this) {
		case NO_DEBUFF:
			return Ladder.NO_DEBUFF;
		case DEBUFF:
			return Ladder.DEBUFF;
		case BUILD_UHC:
			return Ladder.BUILD_UHC;
		case FINAL_UHC:
			return Ladder.FINAL_UHC;
		case HG:
			return Ladder.HG;
		case GAPPLE:
			return Ladder.GAPPLE;
		case COMBO_FLY:
			return Ladder.COMBO_FLY;
		case SUMO:
			return Ladder.SUMO;
		case SOUP:
			return Ladder.SOUP;
		case ARCHER:
			return Ladder.ARCHER;
		case PARKOUR:
			return Ladder.PARKOUR;
		case HCF:
			return Ladder.HCF;
		case BRIDGES_RED:
			return Ladder.BRIDGES;
		case BRIDGES_BLUE:
			return Ladder.BRIDGES;
		case HCT_DIAMOND_NO_DEBUFF:
			return Ladder.HCT_NO_DEBUFF;
		case HCT_DIAMOND_DEBUFF:
			return Ladder.HCT_DEBUFF;
		default:
			return Ladder.HCT_NO_DEBUFF;
		}
	}
}
