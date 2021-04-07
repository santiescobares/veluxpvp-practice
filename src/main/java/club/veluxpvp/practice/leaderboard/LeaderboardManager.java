package club.veluxpvp.practice.leaderboard;

import java.util.List;

import org.bukkit.Bukkit;

import com.google.common.collect.Lists;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.profile.ProfileManager;

public class LeaderboardManager {

	private static ProfileManager pm = Practice.getInstance().getProfileManager();
	
	public static List<String> getTopLadder(LeaderboardLadder ladder, int limit) {
		List<String> lines = Lists.newArrayList();
		
		pm.getProfiles()
		.stream()
		.sorted((p1, p2) -> {
			int elo1 = ladder == LeaderboardLadder.GLOBAL ? p1.getGlobalElo() : p1.getElo(ladder.getLadder());
			int elo2 = ladder == LeaderboardLadder.GLOBAL ? p2.getGlobalElo() : p2.getElo(ladder.getLadder());
			
			return elo2 - elo1;
		})
		.limit(limit)
		.forEach(p -> {
			int elo = ladder == LeaderboardLadder.GLOBAL ? p.getGlobalElo() : p.getElo(ladder.getLadder());
			
			lines.add("&f" + Bukkit.getOfflinePlayer(p.getUuid()).getName() + " &7- &b" + elo);
		});
		
		if(lines.size() < limit) {
			while(lines.size() < limit) {
				lines.add("&fNone");
			}
		}
		
		return lines;
	}
}
