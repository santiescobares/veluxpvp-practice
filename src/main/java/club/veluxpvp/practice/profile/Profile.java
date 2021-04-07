package club.veluxpvp.practice.profile;

import java.util.UUID;

import club.veluxpvp.practice.Settings;
import club.veluxpvp.practice.arena.Ladder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Profile {

	private UUID uuid;
	private long playTime, joinedAt, leftAt;
	private boolean scoreboard, allowSpectators, allowDuels, tournamentMessages, pingOnScoreboard, rankedSimilarPing;
	private int unrankedWins, unrankedLoses, rankedWins, rankedLoses, noDebuffElo, debuffElo, buildUHCElo, gappleElo, sumoElo, soupElo, archerElo, parkourElo, bridgesElo, hgElo;
	
	public Profile(UUID uuid) {
		this.uuid = uuid;
		this.playTime = 0L;
		this.joinedAt = System.currentTimeMillis();
		this.leftAt = System.currentTimeMillis();
		this.scoreboard = true;
		this.allowSpectators = true;
		this.allowDuels = true;
		this.tournamentMessages = true;
		this.pingOnScoreboard = true;
		this.rankedSimilarPing = false;
		this.unrankedWins = 0;
		this.unrankedLoses = 0;
		this.rankedWins = 0;
		this.rankedLoses = 0;
		this.noDebuffElo = Settings.DEFAULT_ELO;
		this.debuffElo = Settings.DEFAULT_ELO;
		this.buildUHCElo = Settings.DEFAULT_ELO;
		this.gappleElo = Settings.DEFAULT_ELO;
		this.sumoElo = Settings.DEFAULT_ELO;
		this.soupElo = Settings.DEFAULT_ELO;
		this.archerElo = Settings.DEFAULT_ELO;
		this.parkourElo = Settings.DEFAULT_ELO;
		this.bridgesElo = Settings.DEFAULT_ELO;
		this.hgElo = Settings.DEFAULT_ELO;
	}
	
	public int getGlobalElo() {
		int totalElo = this.noDebuffElo + this.debuffElo + this.buildUHCElo + this.gappleElo + this.sumoElo + this.soupElo + this.archerElo + this.parkourElo + this.bridgesElo + this.hgElo;
	
		return totalElo / 10;
	}
	
	public double getUnrankedWLR() {
		double wins = this.unrankedWins;
		double losses = this.unrankedLoses;
		
		return wins / Math.max(losses, 1);
	}
	
	public double getRankedWLR() {
		double wins = this.unrankedWins;
		double losses = this.unrankedLoses;
		
		return wins / Math.max(losses, 1);
	}
	
	public void setElo(Ladder ladder, int elo) {
		if(ladder == Ladder.NO_DEBUFF) this.noDebuffElo = elo;
		if(ladder == Ladder.DEBUFF) this.debuffElo = elo;
		if(ladder == Ladder.BUILD_UHC) this.buildUHCElo = elo;
		if(ladder == Ladder.GAPPLE) this.gappleElo = elo;
		if(ladder == Ladder.SUMO) this.sumoElo = elo;
		if(ladder == Ladder.SOUP) this.soupElo = elo;
		if(ladder == Ladder.ARCHER) this.archerElo = elo;
		if(ladder == Ladder.PARKOUR) this.parkourElo = elo;
		if(ladder == Ladder.BRIDGES) this.bridgesElo = elo;
		if(ladder == Ladder.HG) this.hgElo = elo;
	}
	
	public void addElo(Ladder ladder, int elo) {
		if(ladder == Ladder.NO_DEBUFF) this.noDebuffElo += elo;
		if(ladder == Ladder.DEBUFF) this.debuffElo += elo;
		if(ladder == Ladder.BUILD_UHC) this.buildUHCElo += elo;
		if(ladder == Ladder.GAPPLE) this.gappleElo += elo;
		if(ladder == Ladder.SUMO) this.sumoElo += elo;
		if(ladder == Ladder.SOUP) this.soupElo += elo;
		if(ladder == Ladder.ARCHER) this.archerElo += elo;
		if(ladder == Ladder.PARKOUR) this.parkourElo += elo;
		if(ladder == Ladder.BRIDGES) this.bridgesElo += elo;
		if(ladder == Ladder.HG) this.hgElo += elo;
	}
	
	public void removeElo(Ladder ladder, int elo) {
		if(ladder == Ladder.NO_DEBUFF) this.noDebuffElo -= elo;
		if(ladder == Ladder.DEBUFF) this.debuffElo -= elo;
		if(ladder == Ladder.BUILD_UHC) this.buildUHCElo -= elo;
		if(ladder == Ladder.GAPPLE) this.gappleElo -= elo;
		if(ladder == Ladder.SUMO) this.sumoElo -= elo;
		if(ladder == Ladder.SOUP) this.soupElo -= elo;
		if(ladder == Ladder.ARCHER) this.archerElo -= elo;
		if(ladder == Ladder.PARKOUR) this.parkourElo -= elo;
		if(ladder == Ladder.BRIDGES) this.bridgesElo -= elo;
		if(ladder == Ladder.HG) this.hgElo -= elo;
	}
	
	public void resetElo() {
		this.noDebuffElo = Settings.DEFAULT_ELO;
		this.debuffElo = Settings.DEFAULT_ELO;
		this.buildUHCElo = Settings.DEFAULT_ELO;
		this.gappleElo = Settings.DEFAULT_ELO;
		this.sumoElo = Settings.DEFAULT_ELO;
		this.soupElo = Settings.DEFAULT_ELO;
		this.archerElo = Settings.DEFAULT_ELO;
		this.parkourElo = Settings.DEFAULT_ELO;
		this.bridgesElo = Settings.DEFAULT_ELO;
		this.hgElo = Settings.DEFAULT_ELO;
	}
	
	public int getElo(Ladder ladder) {
		if(ladder == Ladder.NO_DEBUFF) return this.noDebuffElo;
		if(ladder == Ladder.DEBUFF) return this.debuffElo;
		if(ladder == Ladder.BUILD_UHC) return this.buildUHCElo;
		if(ladder == Ladder.GAPPLE) return this.gappleElo;
		if(ladder == Ladder.SUMO) return this.sumoElo;
		if(ladder == Ladder.SOUP) return this.soupElo;
		if(ladder == Ladder.ARCHER) return this.archerElo;
		if(ladder == Ladder.PARKOUR) return this.parkourElo;
		if(ladder == Ladder.BRIDGES) return this.bridgesElo;
		if(ladder == Ladder.HG) return this.hgElo;
		
		return Settings.DEFAULT_ELO;
	}
}
