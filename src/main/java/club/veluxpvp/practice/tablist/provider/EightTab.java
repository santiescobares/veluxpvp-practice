package club.veluxpvp.practice.tablist.provider;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;

@Getter
public class EightTab {

	@Getter
	private static EightTab instance;
	
	private TabAdapter adapter;
	
	public EightTab(JavaPlugin plugin, TabAdapter adapter) {
		instance = this;
		this.adapter = adapter;
		
		new TabPacket(plugin);
				
		Bukkit.getServer().getPluginManager().registerEvents(new TabListener(this), plugin);
		Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new TabRunnable(adapter), 20L, 20L); //TODO: async to run 1 millis
	}
	
	public void onDisable() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			removePlayer(player);
		}
	}
	
	public void removePlayer(Player player) {
		boolean continueAt = false;
		if (TabLayout.getLayoutMapping().containsKey(player.getUniqueId())) {
			continueAt = true;
		}

		if (continueAt) {
			TabLayout.getLayoutMapping().remove(player.getUniqueId());
		}
	}
}