package club.veluxpvp.practice.tablist.provider;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import club.veluxpvp.practice.Practice;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TabListener implements Listener {

	private EightTab instance;

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		TabLayout layout = (new TabLayout(instance, player));
		boolean validate = false;
		
		if(TabLayout.getLayoutMapping().containsKey(player.getUniqueId())) {
			validate = true;
		}
		
		if(TabLayout.getLayoutMapping().get(player.getUniqueId()) != null) {
			validate = true;
		}
		
		if(!validate) {
			Bukkit.getScheduler().runTaskAsynchronously(Practice.getInstance(), () -> {
				layout.create();
				layout.setHeaderAndFooter();
			});
		}

		TabLayout.getLayoutMapping().put(player.getUniqueId(), layout);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		instance.removePlayer(player);
	}

	@EventHandler
	public void onKick(PlayerKickEvent event) {
		Player player = event.getPlayer();
		instance.removePlayer(player);
	}
}
