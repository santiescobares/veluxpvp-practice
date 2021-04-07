package club.veluxpvp.practice.arena;

import org.bukkit.Material;

public enum Ladder {
	// Name - Can Edit Kit
	
	// Ranked Ladders: NoDebuff, Debuff, BuildUHC, HG, GApple, Sumo, Soup, Archer, Parkour, Bridges
	
	NO_DEBUFF("No Debuff", true),
	DEBUFF("Debuff", true),
	BUILD_UHC("Build UHC", true),
	FINAL_UHC("Final UHC", true),
	HG("HG", true),
	GAPPLE("GApple", true),
	COMBO_FLY("Combo Fly", true),
	SUMO("Sumo", false),
	SOUP("Soup", true),
	ARCHER("Archer", true),
	HCF("HCF", true),
	PARKOUR("Parkour", false),
	BRIDGES("Bridges", true),
	HCT_NO_DEBUFF("HCF TeamFight (NoDebuff)", true),
	HCT_DEBUFF("HCF TeamFight (Debuff)", true);
	
	public final String name;
	public final boolean canEditKit;
	
	private Ladder(String name, boolean canEditKit) {
		this.name = name;
		this.canEditKit = canEditKit;
	}
	
	public Material getMaterial() {
		switch(this) {
		case NO_DEBUFF:
			return Material.POTION;
		case DEBUFF:
			return Material.POTION;
		case BUILD_UHC:
			return Material.LAVA_BUCKET;
		case FINAL_UHC:
			return Material.WATER_BUCKET;
		case HG:
			return Material.RED_MUSHROOM;
		case GAPPLE:
			return Material.GOLDEN_APPLE;
		case COMBO_FLY:
			return Material.RAW_FISH;
		case SUMO:
			return Material.LEASH;
		case SOUP:
			return Material.MUSHROOM_SOUP;
		case ARCHER:
			return Material.BOW;
		case HCF:
			return Material.FENCE_GATE;
		case PARKOUR:
			return Material.IRON_PLATE;
		case BRIDGES:
			return Material.STAINED_CLAY;
		default:
			return Material.BEACON;
		}
	}
	
	public byte getDataValue() {
		switch(this) {
		case NO_DEBUFF:
			return (byte) 8229;
		case DEBUFF:
			return (byte) 8196;
		case GAPPLE:
			return (byte) 1;
		case COMBO_FLY:
			return (byte) 3;
		default:
			return (byte) 0;
		}
	}
	
	public static Ladder getByName(String name) {
		Ladder[] ladders = Ladder.values();
		
		for(int i = 0; i < ladders.length; i++) {
			if(ladders[i].name().equalsIgnoreCase(name)) return ladders[i];
		}
		
		return null;
	}
}
