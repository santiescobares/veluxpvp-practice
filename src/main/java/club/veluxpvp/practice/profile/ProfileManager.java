package club.veluxpvp.practice.profile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.google.common.collect.Sets;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.Settings;
import club.veluxpvp.practice.utilities.ChatUtil;
import lombok.Getter;

public class ProfileManager {

	@Getter private Set<Profile> profiles;
	
	public ProfileManager() {
		this.profiles = Sets.newHashSet();
		
		loadProfiles();
	}
	
	public Profile getProfile(OfflinePlayer player) {
		return this.profiles.stream().filter(p -> p.getUuid().equals(player.getUniqueId())).findFirst().orElse(null);
	}
	
	public void saveAsync() {
		Bukkit.getScheduler().runTaskAsynchronously(Practice.getInstance(), () -> this.saveProfiles());
	}
	
	public void loadProfiles() {
		Connection connection = Practice.getInstance().getMySQLManager().getConnection();
		
		try {
			PreparedStatement s1 = connection.prepareStatement("SELECT * FROM " + Settings.PROFILES_TABLE);
			ResultSet r1 = s1.executeQuery();
			
			while(r1.next()) {
				Profile p = new Profile(UUID.fromString(r1.getString("UUID")));
				
				p.setPlayTime(r1.getLong("PlayTime"));
				p.setScoreboard(r1.getBoolean("Scoreboard"));
				p.setAllowSpectators(r1.getBoolean("AllowSpectators"));
				p.setAllowDuels(r1.getBoolean("AllowDuels"));
				p.setTournamentMessages(r1.getBoolean("TournamentMessages"));
				p.setPingOnScoreboard(r1.getBoolean("PingOnScoreboard"));
				p.setRankedSimilarPing(r1.getBoolean("RankedSimilarPing"));
				
				this.profiles.add(p);
			}
			
			r1.close();
			s1.close();
			
			PreparedStatement s2 = connection.prepareStatement("SELECT * FROM " + Settings.STATS_TABLE);
			ResultSet r2 = s2.executeQuery();
			
			while(r2.next()) {
				Profile p = this.getProfile(Bukkit.getOfflinePlayer(UUID.fromString(r2.getString("UUID"))));
				
				p.setUnrankedWins(r2.getInt("UnrankedWins"));
				p.setUnrankedLoses(r2.getInt("UnrankedLoses"));
				p.setRankedWins(r2.getInt("RankedWins"));
				p.setRankedLoses(r2.getInt("RankedLoses"));
				p.setNoDebuffElo(r2.getInt("NoDebuffElo"));
				p.setDebuffElo(r2.getInt("DebuffElo"));
				p.setBuildUHCElo(r2.getInt("BuildUHCElo"));
				p.setGappleElo(r2.getInt("GAppleElo"));
				p.setSumoElo(r2.getInt("SumoElo"));
				p.setSoupElo(r2.getInt("SoupElo"));
				p.setArcherElo(r2.getInt("ArcherElo"));
				p.setParkourElo(r2.getInt("ParkourElo"));
				p.setBridgesElo(r2.getInt("BridgesElo"));
				p.setHgElo(r2.getInt("HGElo"));
			}
			
			r2.close();
			s2.close();
		} catch(SQLException e) {
			Bukkit.getConsoleSender().sendMessage(ChatUtil.TRANSLATE("&b[Practice] &cAn error has ocurred while loading profiles from the database!"));
			e.printStackTrace();
		}
	}
	
	public void saveProfiles() {
		Connection connection = Practice.getInstance().getMySQLManager().getConnection();
		
		try {
			for(Profile p : this.profiles) {
				// Profile doesn't exist
				if(!this.profileExists(p)) {
					// Profiles Table
					PreparedStatement s1 = connection.prepareStatement("INSERT INTO " + Settings.PROFILES_TABLE + " VALUE (?,?,?,?,?,?,?,?,?)");
					
					s1.setString(1, p.getUuid().toString());
					s1.setString(2, Bukkit.getOfflinePlayer(p.getUuid()).getName());
					s1.setLong(3, p.getPlayTime());
					s1.setBoolean(4, p.isScoreboard());
					s1.setBoolean(5, p.isAllowSpectators());
					s1.setBoolean(6, p.isAllowDuels());
					s1.setBoolean(7, p.isTournamentMessages());
					s1.setBoolean(8, p.isPingOnScoreboard());
					s1.setBoolean(9, p.isRankedSimilarPing());
					
					s1.executeUpdate();
					
					// Stats table
					PreparedStatement s2 = connection.prepareStatement("INSERT INTO " + Settings.STATS_TABLE + " VALUE (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				
					s2.setString(1, p.getUuid().toString());
					s2.setString(2, Bukkit.getOfflinePlayer(p.getUuid()).getName());
					s2.setInt(3, p.getUnrankedWins());
					s2.setInt(4, p.getUnrankedLoses());
					s2.setInt(5, p.getRankedWins());
					s2.setInt(6, p.getRankedLoses());
					s2.setInt(7, p.getNoDebuffElo());
					s2.setInt(8, p.getDebuffElo());
					s2.setInt(9, p.getBuildUHCElo());
					s2.setInt(10, p.getGappleElo());
					s2.setInt(11, p.getSumoElo());
					s2.setInt(12, p.getSoupElo());
					s2.setInt(13, p.getArcherElo());
					s2.setInt(14, p.getParkourElo());
					s2.setInt(15, p.getBridgesElo());
					s2.setInt(16, p.getHgElo());
					
					s2.executeUpdate();
					
					s1.close();
					s2.close();
				// Profile exists
				} else {
					// Profiles table
					PreparedStatement s1 = connection.prepareStatement("UPDATE " + Settings.PROFILES_TABLE + " SET Name=?, PlayTime=?, Scoreboard=?, AllowSpectators=?, AllowDuels=?, TournamentMessages=?, PingOnScoreboard=?, RankedSimilarPing=? WHERE UUID=?");
				
					s1.setString(1, Bukkit.getOfflinePlayer(p.getUuid()).getName());
					s1.setLong(2, p.getPlayTime());
					s1.setBoolean(3, p.isScoreboard());
					s1.setBoolean(4, p.isAllowSpectators());
					s1.setBoolean(5, p.isAllowDuels());
					s1.setBoolean(6, p.isTournamentMessages());
					s1.setBoolean(7, p.isPingOnScoreboard());
					s1.setBoolean(8, p.isRankedSimilarPing());
					s1.setString(9, p.getUuid().toString());
					
					s1.executeUpdate();
					
					// Stats table
					PreparedStatement s2 = connection.prepareStatement("UPDATE " + Settings.STATS_TABLE + " SET Name=?, UnrankedWins=?, UnrankedLoses=?, RankedWins=?, RankedLoses=?, NoDebuffElo=?, DebuffElo=?, BuildUHCElo=?, GAppleElo=?, SumoElo=?, SoupElo=?, ArcherElo=?, ParkourElo=?, BridgesElo=?, HGElo=? WHERE UUID=?");
				
					s2.setString(1, Bukkit.getOfflinePlayer(p.getUuid()).getName());
					s2.setInt(2, p.getUnrankedWins());
					s2.setInt(3, p.getUnrankedLoses());
					s2.setInt(4, p.getRankedWins());
					s2.setInt(5, p.getRankedLoses());
					s2.setInt(6, p.getNoDebuffElo());
					s2.setInt(7, p.getDebuffElo());
					s2.setInt(8, p.getBuildUHCElo());
					s2.setInt(9, p.getGappleElo());
					s2.setInt(10, p.getSumoElo());
					s2.setInt(11, p.getSoupElo());
					s2.setInt(12, p.getArcherElo());
					s2.setInt(13, p.getParkourElo());
					s2.setInt(14, p.getBridgesElo());
					s2.setInt(15, p.getHgElo());
					s2.setString(16, p.getUuid().toString());
					
					s2.executeUpdate();
					
					s1.close();
					s2.close();
				}
			}
		} catch(SQLException e) {
			Bukkit.getConsoleSender().sendMessage(ChatUtil.TRANSLATE("&b[Practice] &cAn error has ocurred while saving profiles into the database!"));
			e.printStackTrace();
		}
	}
	
	public boolean profileExists(Profile p) {
		Connection connection = Practice.getInstance().getMySQLManager().getConnection();
		
		try {
			PreparedStatement s = connection.prepareStatement("SELECT * FROM " + Settings.PROFILES_TABLE + " WHERE (UUID=?)");
			
			s.setString(1, p.getUuid().toString());
			
			ResultSet r = s.executeQuery();
			
			return r.next();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
}
