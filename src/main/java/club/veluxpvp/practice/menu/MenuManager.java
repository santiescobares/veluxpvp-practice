package club.veluxpvp.practice.menu;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

public class MenuManager {
	
	public static Map<UUID, Menu> openedMenu = new HashMap<>();
	
	public static Menu getOpenedMenu(Player player) {
		return openedMenu.get(player.getUniqueId());
	}
}
