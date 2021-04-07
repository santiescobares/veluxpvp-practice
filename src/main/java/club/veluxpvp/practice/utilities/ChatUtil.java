package club.veluxpvp.practice.utilities;

import net.md_5.bungee.api.ChatColor;

public class ChatUtil {

	public static String TRANSLATE(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	
	public static String LINE() {
		return ChatColor.translateAlternateColorCodes('&', "&7&m-----------------------------------------------------");
	}
	
	public static String SHORTER_LINE() {
		return ChatColor.translateAlternateColorCodes('&', "&7&m---------------------------------------");
	}
	
	public static String INV_LINE() {
		return ChatColor.translateAlternateColorCodes('&', "&7&m------------------------------");
	}
	
	public static String NO_PERMISSION() {
		return ChatColor.translateAlternateColorCodes('&', "Unknown command.");
	}
	
	public static String NO_CONSOLE() {
		return ChatColor.translateAlternateColorCodes('&', "&cThis action only can be performed by players!");
	}
	
	public static String NO_PLAYERS() {
		return ChatColor.translateAlternateColorCodes('&', "&cThis action only can be performed by console!");
	}
}
