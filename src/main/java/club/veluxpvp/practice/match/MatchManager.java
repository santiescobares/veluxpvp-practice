package club.veluxpvp.practice.match;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.Settings;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.Hastebin;
import lombok.Getter;

public class MatchManager {

	@Getter private Set<Match> matches;
	@Getter private Map<UUID, Match> playerLastMatch;
	@Getter private List<RankedMatchLog> rankedMatchesLogs;
	
	public MatchManager() {
		this.matches = Sets.newHashSet();
		this.playerLastMatch = Maps.newHashMap();
		this.rankedMatchesLogs = Lists.newArrayList();
		
		loadRankedMatchesLogs();
	}
	
	public Match getPlayerMatch(Player player) {
		for(Match m : this.matches) {
			for(Player p : m.getPlayers()) {
				if(p == player) return m;
			}
		}
		
		return null;
	}
	
	public Match getLastMatch(OfflinePlayer player) {
		return this.playerLastMatch.get(player.getUniqueId());
	}
	
	public int getPlayingOnLadder(Ladder ladder, boolean ranked) {
		return (int) this.matches.stream().filter(m -> m.getLadder() == ladder && m.isRanked() == ranked).count();
	}
	
	public int getFighting() {
		int fighting = 0;
		
		for(Match m : this.matches) {
			fighting += m.getAlivePlayers().size();
		}
		
		return fighting;
	}
	
	public int getFighting(Ladder ladder, boolean ranked) {
		int fighting = 0;
		
		for(Match m : this.matches) {
			if(m.getLadder() == ladder && m.isRanked() == ranked) {
				fighting += m.getAlivePlayers().size();
			}
		}
		
		return fighting;
	}
	
	public void saveAsync() {
		Bukkit.getScheduler().runTaskAsynchronously(Practice.getInstance(), () -> this.saveRankedMatchesLogs());
	}
	
	public List<RankedMatchLog> getLastRankedMatchesLogs(OfflinePlayer player) {
		return this.rankedMatchesLogs.stream()
				.filter(rml -> rml.getWinnerUUID().equals(player.getUniqueId()) || rml.getLoserUUID().equals(player.getUniqueId()))
				.sorted((rml1, rml2) -> {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					
					try {
						Date d1 = sdf.parse(rml1.getPlayedOn());
						Date d2 = sdf.parse(rml2.getPlayedOn());
						
						return (int) (d2.getTime() - d1.getTime());
					} catch(Exception e) {
						e.printStackTrace();
						return 0;
					}
				})
				.collect(Collectors.toList());
	}
	
	public String uploadAndGetPostMatchURL(RankedMatchLog match) {
		List<String> textArr = Arrays.asList(
				"-------------------------------------------------------",
				"Match Log - (" + Bukkit.getOfflinePlayer(match.getWinnerUUID()).getName() + " vs " + Bukkit.getOfflinePlayer(match.getLoserUUID()).getName() + ") - ID: #" + match.getMatchID(),
				"-------------------------------------------------------",
				"Winner: " + Bukkit.getOfflinePlayer(match.getWinnerUUID()).getName() + " - ELO Update: " + (match.getWinnerElo() + match.getEloUpdate()) + " (+" + match.getEloUpdate() + ")",
				"Loser: " + Bukkit.getOfflinePlayer(match.getLoserUUID()).getName() + " - ELO Update: " + (match.getLoserElo() - match.getEloUpdate()) + " (-" + match.getEloUpdate() + ")",
				"-------------------------------------------------------",
				"Match Statistics (Winner - Loser)",
				" ",
				"Total Hits: " + match.getWinnerTotalHits() + " | " + match.getLoserTotalHits(),
				"Longest Combo: " + match.getWinnerLongestCombo() + " | " + match.getLoserLongestCombo(),
				"Healing Left: " + match.getWinnerHealingLeft() + " | " + match.getLoserHealingLeft(),
				"Missed Pots: " + match.getWinnerMissedPots() + " | " + match.getLoserMissedPots(),
				" ",
				"Played On: " + match.getPlayedOn() + " (GMT-3)",
				"Ladder: " + match.getLadder().name,
				"Duration: " + match.getDuration(),
				"Arena: " + match.getArena(),
				"-------------------------------------------------------"
				);
		
		String text = "";
		for(String t : textArr) {
			text += t + "\n";
		}
		
		try {
			return new Hastebin().post(text, false);
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	public void loadRankedMatchesLogs() {
		Connection connection = Practice.getInstance().getMySQLManager().getConnection();
		
		try {
			PreparedStatement s = connection.prepareStatement("SELECT * FROM " + Settings.RANKED_MATCHES_LOGS_TABLE);
			ResultSet r = s.executeQuery();
			
			while(r.next()) {
				String matchID = r.getString("MatchID");
				UUID winnerUUID = UUID.fromString(r.getString("WinnerUUID"));
				UUID loserUUID = UUID.fromString(r.getString("LoserUUID"));
				String playedOn = r.getString("PlayedOn");
				String arena = r.getString("Arena");
				String duration = r.getString("Duration");
				Ladder ladder = Ladder.valueOf(r.getString("Ladder").toUpperCase());
				HealingType healingType = HealingType.valueOf(r.getString("HealingType").toUpperCase());
				int winnerTotalHits = r.getInt("WinnerTotalHits");
				int loserTotalHits = r.getInt("LoserTotalHits");
				int winnerLongestCombo = r.getInt("WinnerLongestCombo");
				int loserLongestCombo = r.getInt("LoserLongestCombo");
				int winnerHealingLeft = r.getInt("WinnerHealingLeft");
				int loserHealingLeft = r.getInt("LoserHealingLeft");
				int winnerMissedPots = r.getInt("WinnerMissedPots");
				int loserMissedPots = r.getInt("LoserMissedPots");
				int winnerElo = r.getInt("WinnerElo");
				int loserElo = r.getInt("LoserElo");
				int eloUpdate = r.getInt("EloUpdate");
				
				RankedMatchLog rml = new RankedMatchLog(matchID, winnerUUID, loserUUID, playedOn, arena, duration, ladder, healingType, winnerTotalHits, loserTotalHits, winnerLongestCombo, loserLongestCombo, winnerHealingLeft, loserHealingLeft, winnerMissedPots, loserMissedPots, winnerElo, loserElo, eloUpdate);
				
				this.rankedMatchesLogs.add(rml);
			}
			
			r.close();
			s.close();
		} catch(SQLException e) {
			Bukkit.getConsoleSender().sendMessage(ChatUtil.TRANSLATE("&b[Practice] &cAn error has ocurred while loading ranked matches logs from the database!"));
			e.printStackTrace();
		}
	}
	
	public void saveRankedMatchesLogs() {
		Connection connection = Practice.getInstance().getMySQLManager().getConnection();
		
		try {
			if(this.rankedMatchesLogs.size() > 0) {
				for(RankedMatchLog r : this.rankedMatchesLogs) {
					if(matchLogExists(r)) continue;
					
					PreparedStatement s = connection.prepareStatement("INSERT INTO " + Settings.RANKED_MATCHES_LOGS_TABLE + " VALUE (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
					
					s.setString(1, r.getMatchID());
					s.setString(2, r.getWinnerUUID().toString());
					s.setString(3, r.getLoserUUID().toString());
					s.setString(4, r.getPlayedOn());
					s.setString(5, r.getArena());
					s.setString(6, r.getDuration());
					s.setString(7, r.getLadder().name());
					s.setString(8, r.getHealingType().name());
					s.setInt(9, r.getWinnerTotalHits());
					s.setInt(10, r.getLoserTotalHits());
					s.setInt(11, r.getWinnerLongestCombo());
					s.setInt(12, r.getLoserLongestCombo());
					s.setInt(13, r.getWinnerHealingLeft());
					s.setInt(14, r.getLoserHealingLeft());
					s.setInt(15, r.getWinnerMissedPots());
					s.setInt(16, r.getLoserMissedPots());
					s.setInt(17, r.getWinnerElo());
					s.setInt(18, r.getLoserElo());
					s.setInt(19, r.getEloUpdate());
					
					s.executeUpdate();
					
					s.close();
				}
			}
		} catch(SQLException e) {
			Bukkit.getConsoleSender().sendMessage(ChatUtil.TRANSLATE("&b[Practice] &cAn error has ocurred while saving ranked matches logs into the database!"));
			e.printStackTrace();
		}
	}
	
	public boolean matchLogExists(RankedMatchLog r) {
		Connection connection = Practice.getInstance().getMySQLManager().getConnection();
		
		try {
			PreparedStatement s = connection.prepareStatement("SELECT * FROM " + Settings.RANKED_MATCHES_LOGS_TABLE + " WHERE (MatchID=?)");
			
			s.setString(1, r.getMatchID());

			if(s.executeQuery().next()) {
				s.close();
				return true;
			} else {
				s.close();
				return false;
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
}
