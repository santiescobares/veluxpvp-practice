package club.veluxpvp.practice.queue.menu;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.leaderboard.LeaderboardLadder;
import club.veluxpvp.practice.leaderboard.LeaderboardManager;
import club.veluxpvp.practice.menu.Button;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class RankedQueueButton extends Button {

	@Getter private Ladder ladder;
	
	@Override
	public Material getMaterial() {
		return this.ladder.getMaterial();
	}
	
	@Override
	public int getAmount() {
		int amount = Practice.getInstance().getMatchManager().getFighting(this.ladder, true);
		return amount == 0 ? 1 : (amount > 64 ? 64 : amount);
	}
	
	@Override
	public byte getDataValue() {
		return this.ladder.getDataValue();
	}
	
	@Override
	public String getName() {
		return ChatColor.AQUA + this.ladder.name;
	}
	
	@Override
	public List<String> getLore() {
		List<String> top = LeaderboardManager.getTopLadder(LeaderboardLadder.getByLadder(this.ladder), 3);
		
		return Arrays.asList(
				" ",
				"&7* &fQueued&7: &b" + Practice.getInstance().getQueueManager().getQueuedOnLadder(this.ladder, true),
				"&7* &fPlaying&7: &b" + Practice.getInstance().getMatchManager().getFighting(this.ladder, true),
				" ",
				"&b1. &7" + top.get(0),
				"&b2. &7" + top.get(1),
				"&b3. &7" + top.get(2),
				" ",
				"&aClick to join &e" + this.ladder.name + " &aqueue!"
				);
	}
}
