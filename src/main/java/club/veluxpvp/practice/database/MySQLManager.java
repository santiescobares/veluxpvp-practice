package club.veluxpvp.practice.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;

import club.veluxpvp.practice.Settings;
import club.veluxpvp.practice.utilities.ChatUtil;
import lombok.Getter;

public class MySQLManager {

	@Getter private Connection connection;
	
	public MySQLManager() {
		try {
			synchronized(this) {
				if(this.connection != null && !this.connection.isClosed()) {
					return;
				}
				
				Class.forName("com.mysql.jdbc.Driver");
				this.connection = DriverManager.getConnection("jdbc:mysql://" + Settings.DATABASE_HOST + ":" + Settings.DATABASE_PORT + "/" + Settings.DATABASE_NAME, Settings.DATABASE_USERNAME, Settings.DATABASE_PASSWORD);
			
				createProfilesTable();
				createStatsTable();
				createRankedMatchesLogsTable();
				createKitsTable();
				
				Bukkit.getConsoleSender().sendMessage(ChatUtil.TRANSLATE("&aDatabase successfully connected!"));
			}
		} catch(SQLException | ClassNotFoundException e) {
			Bukkit.getConsoleSender().sendMessage(ChatUtil.TRANSLATE("&cAn error has ocurred while connecting to the database!"));
			e.printStackTrace();
		}
	}
	
	private void createProfilesTable() {
		List<String> profilesTable = 
				Arrays.asList(
						"CREATE TABLE IF NOT EXISTS " + Settings.PROFILES_TABLE + "(UUID VARCHAR(40),",
						"Name VARCHAR(20),",
						"PlayTime LONG,",
						"Scoreboard BOOLEAN,",
						"AllowSpectators BOOLEAN,",
						"AllowDuels BOOLEAN,",
						"TournamentMessages BOOLEAN,",
						"PingOnScoreboard BOOLEAN,",
						"RankedSimilarPing BOOLEAN) ",
						"ENGINE = InnoDB CHARACTER SET utf8;"
						);
		
		try {
			String statement = "";
			
			for(int i = 0; i < profilesTable.size(); i++) {
				if(i != (profilesTable.size() - 1)) {
					statement += profilesTable.get(i) + " ";
				} else {
					statement += profilesTable.get(i);
				}
			}

			PreparedStatement s = this.connection.prepareStatement(statement);
			
			s.executeUpdate();
		} catch(SQLException e) {
			Bukkit.getConsoleSender().sendMessage(ChatUtil.TRANSLATE("&cAn error has ocurred while creating profiles table!"));
			e.printStackTrace();
		}
	}
	
	private void createStatsTable() {
		List<String> statsTable = 
				Arrays.asList(
						"CREATE TABLE IF NOT EXISTS " + Settings.STATS_TABLE + "(UUID VARCHAR(40),",
						"Name VARCHAR(20),",
						"UnrankedWins INT,",
						"UnrankedLoses INT,",
						"RankedWins INT,",
						"RankedLoses INT,",
						"NoDebuffElo INT,",
						"DebuffElo INT,",
						"BuildUHCElo INT,",
						"GAppleElo INT,",
						"SumoElo INT,",
						"SoupElo INT,",
						"ArcherElo INT,",
						"ParkourElo INT,",
						"BridgesElo INT) ",
						"ENGINE = InnoDB CHARACTER SET utf8;"
						);
		
		try {
			String statement = "";
			
			for(int i = 0; i < statsTable.size(); i++) {
				if(i != (statsTable.size() - 1)) {
					statement += statsTable.get(i) + " ";
				} else {
					statement += statsTable.get(i);
				}
			}

			PreparedStatement s = this.connection.prepareStatement(statement);
			
			s.executeUpdate();
		} catch(SQLException e) {
			Bukkit.getConsoleSender().sendMessage(ChatUtil.TRANSLATE("&cAn error has ocurred while creating stats table!"));
			e.printStackTrace();
		}
	}
	
	private void createRankedMatchesLogsTable() {
		List<String> rankedMatchesLogsTable = 
				Arrays.asList(
						"CREATE TABLE IF NOT EXISTS " + Settings.RANKED_MATCHES_LOGS_TABLE + "(MatchID VARCHAR(10),",
						"WinnerUUID VARCHAR(40),",
						"LoserUUID VARCHAR(40),",
						"PlayedOn VARCHAR(20),",
						"Arena VARCHAR(25),",
						"Duration VARCHAR(10),",
						"Ladder VARCHAR(20),",
						"HealingType VARCHAR(20),",
						"WinnerTotalHits INT,",
						"LoserTotalHits INT,",
						"WinnerLongestCombo INT,",
						"LoserLongestCombo INT,",
						"WinnerHealingLeft INT,",
						"LoserHealingLeft INT,",
						"WinnerMissedPots INT,",
						"LoserMissedPots INT,",
						"WinnerElo INT,",
						"LoserElo INT,",
						"EloUpdate INT) ",
						"ENGINE = InnoDB CHARACTER SET utf8;"
						);
		
		try {
			String statement = "";
			
			for(int i = 0; i < rankedMatchesLogsTable.size(); i++) {
				if(i != (rankedMatchesLogsTable.size() - 1)) {
					statement += rankedMatchesLogsTable.get(i) + " ";
				} else {
					statement += rankedMatchesLogsTable.get(i);
				}
			}

			PreparedStatement s = this.connection.prepareStatement(statement);
			
			s.executeUpdate();
		} catch(SQLException e) {
			Bukkit.getConsoleSender().sendMessage(ChatUtil.TRANSLATE("&cAn error has ocurred while creating ranked matches logs table!"));
			e.printStackTrace();
		}
	}
	
	private void createKitsTable() {
		List<String> kitsTable = 
				Arrays.asList(
						"CREATE TABLE IF NOT EXISTS " + Settings.KITS_TABLE + "(OwnerUUID VARCHAR(40),",
						"Name VARCHAR(20),",
						"ID VARCHAR(10),",
						"DisplayName VARCHAR(200),",
						"Type VARCHAR(30),",
						"Number INT,",
						"IsDefault BOOLEAN,",
						"Contents VARCHAR(9999),",
						"ArmorContents VARCHAR(9999)) ",
						"ENGINE = InnoDB CHARACTER SET utf8;"
						);
		
		try {
			String statement = "";
			
			for(int i = 0; i < kitsTable.size(); i++) {
				if(i != (kitsTable.size() - 1)) {
					statement += kitsTable.get(i) + " ";
				} else {
					statement += kitsTable.get(i);
				}
			}

			PreparedStatement s = this.connection.prepareStatement(statement);
			
			s.executeUpdate();
		} catch(SQLException e) {
			Bukkit.getConsoleSender().sendMessage(ChatUtil.TRANSLATE("&cAn error has ocurred while creating kits table!"));
			e.printStackTrace();
		}
	}
}
