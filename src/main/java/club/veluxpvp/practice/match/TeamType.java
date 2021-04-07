package club.veluxpvp.practice.match;

public enum TeamType {
	TEAM_1,
	TEAM_2;
	
	public static TeamType getByName(String name) {
		TeamType[] types = TeamType.values();
		
		for(int i = 0; i < types.length; i++) {
			if(name.equalsIgnoreCase(types[i].name())) return types[i];
		}
		
		if(name.equalsIgnoreCase("red")) return TeamType.TEAM_1;
		if(name.equalsIgnoreCase("blue")) return TeamType.TEAM_2;
		
		return null;
	}
}
