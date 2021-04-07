package club.veluxpvp.practice.queue;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.profile.Profile;
import lombok.Getter;

@Getter
public class QueuedPlayer {

	private Player player;
	private Ladder ladder;
	private boolean ranked, similarPing;
	private int time;
	private BukkitTask task;
	
	private int minEloRange, maxEloRange;
	
	public QueuedPlayer(Player player, Ladder ladder, boolean ranked, boolean similarPing) {
		this.player = player;
		this.ladder = ladder;
		this.ranked = ranked;
		this.similarPing = similarPing;
		this.time = 0;
		
		if(ranked) {
			Profile profile = Practice.getInstance().getProfileManager().getProfile(player);
			
			this.minEloRange = profile.getElo(ladder) - 5;
			this.maxEloRange = profile.getElo(ladder) + 5;
		} else {
			this.minEloRange = 0;
			this.maxEloRange = 0;
		}
		
		this.task = new BukkitRunnable() {

			@Override
			public void run() {
				time++;
				
				if(ranked) {
					minEloRange -= 5;
					maxEloRange += 5;
				}
			}
			
		}.runTaskTimerAsynchronously(Practice.getInstance(), 20L, 20L);
	}
	
	public void cancel() {
		this.task.cancel();
	}
	
	public boolean isInsideEloRange(int elo) {
		return elo >= this.minEloRange && elo <= this.maxEloRange;
	}
}
