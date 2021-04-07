package club.veluxpvp.practice.scoreboard.provider;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.profile.Profile;
import club.veluxpvp.practice.scoreboard.provider.event.AssembleBoardCreateEvent;
import club.veluxpvp.practice.scoreboard.provider.event.AssembleBoardDestroyEvent;

@Getter
public class AssembleListener implements Listener {

	private Assemble assemble;

	/**
	 * Assemble Listener.
	 *
	 * @param assemble instance.
	 */
	public AssembleListener(Assemble assemble) {
		this.assemble = assemble;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Profile profile = Practice.getInstance().getProfileManager().getProfile(event.getPlayer());
		
		if(profile != null && !profile.isScoreboard()) return;
		
		AssembleBoardCreateEvent createEvent = new AssembleBoardCreateEvent(event.getPlayer());

		Bukkit.getPluginManager().callEvent(createEvent);
		if (createEvent.isCancelled()) {
			return;
		}

		getAssemble().getBoards().put(event.getPlayer().getUniqueId(), new AssembleBoard(event.getPlayer(), getAssemble()));
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Profile profile = Practice.getInstance().getProfileManager().getProfile(event.getPlayer());
		
		if(profile != null && !profile.isScoreboard()) return;
		
		AssembleBoardDestroyEvent destroyEvent = new AssembleBoardDestroyEvent(event.getPlayer());

		Bukkit.getPluginManager().callEvent(destroyEvent);
		if (destroyEvent.isCancelled()) {
			return;
		}

		getAssemble().getBoards().remove(event.getPlayer().getUniqueId());
		if(Bukkit.getScoreboardManager().getMainScoreboard() != null) event.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
	}

}
