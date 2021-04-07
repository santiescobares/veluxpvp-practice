package club.veluxpvp.practice.queue.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import club.veluxpvp.practice.Practice;

public class QueueListener implements Listener {

	public QueueListener() {
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent event) { Practice.getInstance().getQueueManager().removePlayer(event.getPlayer(), false, true); }

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerKick(PlayerKickEvent event) { Practice.getInstance().getQueueManager().removePlayer(event.getPlayer(), false, true); }
}
