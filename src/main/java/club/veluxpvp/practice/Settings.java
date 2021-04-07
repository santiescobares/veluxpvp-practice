package club.veluxpvp.practice;

public final class Settings {

	public static final String DATABASE_HOST = Practice.getInstance().getConfig().getString("DATABASE.HOST");
	public static final String DATABASE_NAME = Practice.getInstance().getConfig().getString("DATABASE.NAME");
	public static final String DATABASE_USERNAME = Practice.getInstance().getConfig().getString("DATABASE.AUTH.USERNAME");
	public static final String DATABASE_PASSWORD = Practice.getInstance().getConfig().getString("DATABASE.AUTH.PASSWORD");
	public static final int DATABASE_PORT = Practice.getInstance().getConfig().getInt("DATABASE.PORT");
	
	public static final String TABLE_PREFIX = Practice.getInstance().getConfig().getString("DATABASE.TABLE_PREFIX");
	public static final String PROFILES_TABLE = TABLE_PREFIX + "profiles";
	public static final String STATS_TABLE = TABLE_PREFIX + "stats";
	public static final String RANKED_MATCHES_LOGS_TABLE = TABLE_PREFIX + "rankedMatchesLogs";
	public static final String KITS_TABLE = TABLE_PREFIX + "kits";
	
	public static final int DEFAULT_ELO = 1000;
	public static final int REQUIRED_UNRANKED_WINS = 10;
}
