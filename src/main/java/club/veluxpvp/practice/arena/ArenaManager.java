package club.veluxpvp.practice.arena;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.event.EventType;
import club.veluxpvp.practice.match.TeamType;
import club.veluxpvp.practice.utilities.Serializer;
import lombok.Getter;

public class ArenaManager {

	@Getter private Set<Arena> arenas;
	
	public ArenaManager() {
		this.arenas = new HashSet<>();
		
		loadArenas();
	}
	
	public Arena getByName(String name) {
		return this.arenas.stream().filter(a -> a.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	
	public List<Arena> getEnabledArenas() {
		return this.arenas.stream().filter(a -> a.isEnabled()).collect(Collectors.toList());
	}
	
	public Arena getRandomArena(Ladder ladder, boolean ranked) {
		List<Arena> arenasForLadder = Lists.newArrayList();
		
		if(ladder != Ladder.BUILD_UHC && ladder != Ladder.BRIDGES) {
			arenasForLadder = !ranked ? this.getEnabledArenas().stream().filter(a -> a.isLadderSupported(ladder)).collect(Collectors.toList()) : this.getEnabledArenas().stream().filter(a -> a.isLadderSupported(ladder) && a.isAllowsRanked()).collect(Collectors.toList());
		} else {
			arenasForLadder = !ranked ? this.getEnabledArenas().stream().filter(a -> a.isLadderSupported(ladder) && !a.isInUse()).collect(Collectors.toList()) : this.getEnabledArenas().stream().filter(a -> a.isLadderSupported(ladder) && a.isAllowsRanked() && !a.isInUse()).collect(Collectors.toList());
		}
		
		if(arenasForLadder.size() == 0) return null;
		
		return arenasForLadder.get(new Random().nextInt(arenasForLadder.size()));
	}
	
	public Arena getRandomPartyArena(Ladder ladder) {
		List<Arena> arenasForLadder = ladder != Ladder.BUILD_UHC && ladder != Ladder.BRIDGES ? this.getEnabledArenas().stream().filter(a -> a.isLadderSupported(ladder) && a.isAllowsParties()).collect(Collectors.toList()) : this.getEnabledArenas().stream().filter(a -> a.isLadderSupported(ladder) && a.isAllowsParties() && !a.isInUse()).collect(Collectors.toList());
		
		if(arenasForLadder.size() == 0) return null;
		
		return arenasForLadder.get(new Random().nextInt(arenasForLadder.size()));
	}
	
	public Arena getRandomTournamentArena(Ladder ladder) {
		List<Arena> arenasForLadder = ladder != Ladder.BUILD_UHC && ladder != Ladder.BRIDGES ? this.getEnabledArenas().stream().filter(a -> a.isLadderSupported(ladder) && a.isAllowsTournaments()).collect(Collectors.toList()) : this.getEnabledArenas().stream().filter(a -> a.isLadderSupported(ladder) && a.isAllowsTournaments() && !a.isInUse()).collect(Collectors.toList());
	
		if(arenasForLadder.size() == 0) return null;
		
		return arenasForLadder.get(new Random().nextInt(arenasForLadder.size()));
	}
	
	public Arena getRandomEventsArena(EventType type) {
		List<Arena> arenasForEvents = this.arenas.stream().filter(a -> a.isEventSupported(type) && !a.isInUse()).collect(Collectors.toList());
		
		if(arenasForEvents.size() == 0) return null;
		
		return arenasForEvents.get(new Random().nextInt(arenasForEvents.size()));
	}
	
	public List<Arena> getArenasForLadder(Ladder ladder) {
		return this.getEnabledArenas().stream().filter(a -> a.isLadderSupported(ladder)).collect(Collectors.toList());
	}
	
	public boolean isBlockOnSomeArena(Block block) {
		for(Arena a : this.arenas) {
			if(a.getBounds().contains(block)) return true;
		}
		
		return false;
	}
	
	public void loadArenas() {
		FileConfiguration arenas = Practice.getInstance().getConfigurationManager().getArenas();
		
		if(arenas.contains("ARENAS")) {
			for(String key : arenas.getConfigurationSection("ARENAS").getKeys(false)) {
				Arena a = new Arena(key);
				
				ItemStack icon = null;
				if(arenas.contains("ARENAS." + key + ".ICON")) {
					String[] iconArr = arenas.getString("ARENAS." + key + ".ICON").split(":");
					
					icon = new ItemStack(Material.valueOf(iconArr[0].toUpperCase()), 1, (byte) Byte.parseByte(iconArr[1]));
				}
				
				if(icon != null) a.setIcon(icon);
				
				a.setEnderpearls(arenas.getBoolean("ARENAS." + key + ".ENDERPEARLS"));
				a.setEnabled(arenas.getBoolean("ARENAS." + key + ".ENABLED"));
				a.setMaxBuildHeight(arenas.getInt("ARENAS." + key + ".MAX_BUILD_HEIGHT"));
				
				if(arenas.contains("ARENAS." + key + ".BOUNDS")) a.setBounds(Serializer.deserializeCuboid(arenas.getString("ARENAS." + key + ".BOUNDS")));
				if(arenas.contains("ARENAS." + key + ".LOCATIONS.CORNER_1")) a.setCorner1(Serializer.deserializeLocation(arenas.getString("ARENAS." + key + ".LOCATIONS.CORNER_1")));
				if(arenas.contains("ARENAS." + key + ".LOCATIONS.CORNER_2")) a.setCorner2(Serializer.deserializeLocation(arenas.getString("ARENAS." + key + ".LOCATIONS.CORNER_2")));
				if(arenas.contains("ARENAS." + key + ".LOCATIONS.SPECTATORS_SPAWN")) a.setSpectatorsSpawn(Serializer.deserializeLocation(arenas.getString("ARENAS." + key + ".LOCATIONS.SPECTATORS_SPAWN")));
				if(arenas.contains("ARENAS." + key + ".LOCATIONS.EVENTS_SPAWN")) a.setEventsSpawn(Serializer.deserializeLocation(arenas.getString("ARENAS." + key + ".LOCATIONS.EVENTS_SPAWN")));
			
				a.setAllowsRanked(arenas.getBoolean("ARENAS." + key + ".OPTIONS.ALLOWS_RANKED"));
				a.setAllowsParties(arenas.getBoolean("ARENAS." + key + ".OPTIONS.ALLOWS_PARTIES"));
				a.setAllowsTournaments(arenas.getBoolean("ARENAS." + key + ".OPTIONS.ALLOWS_TOURNAMENTS"));
				
				if(arenas.contains("ARENAS." + key + ".SUPPORTED_LADDERS")) {
					List<String> ladders = arenas.getStringList("ARENAS." + key + ".SUPPORTED_LADDERS");
					
					for(String l : ladders) {
						Ladder ladder = Ladder.valueOf(l.toUpperCase());
						
						if(ladder != null) a.addSupportedLadder(ladder);
					}
				}
				
				if(arenas.contains("ARENAS." + key + ".SUPPORTED_EVENTS")) {
					List<String> events = arenas.getStringList("ARENAS." + key + ".SUPPORTED_EVENTS");
					
					for(String e : events) {
						EventType type = EventType.valueOf(e.toUpperCase());
						
						if(type != null) a.addSupportedEvent(type);
					}
				}
				
				if(arenas.contains("ARENAS." + key + ".PARKOUR_CHECKPOINTS")) {
					List<String> checkpoints = arenas.getStringList("ARENAS." + key + ".PARKOUR_CHECKPOINTS");
					
					for(String c : checkpoints) {
						a.addCheckpoint(Serializer.deserializeLocation(c));
					}
				}
				
				if(arenas.contains("ARENAS." + key + ".BRIDGES_CLAIMS.RED")) a.setBridgesRedClaim(Serializer.deserializeClaim(TeamType.TEAM_1, arenas.getString("ARENAS." + key + ".BRIDGES_CLAIMS.RED")));
				if(arenas.contains("ARENAS." + key + ".BRIDGES_CLAIMS.BLUE")) a.setBridgesBlueClaim(Serializer.deserializeClaim(TeamType.TEAM_2, arenas.getString("ARENAS." + key + ".BRIDGES_CLAIMS.BLUE")));
				
				this.arenas.add(a);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void saveArenas() {
		FileConfiguration arenas = Practice.getInstance().getConfigurationManager().getArenas();
		
		arenas.set("ARENAS", null);
		
		for(Arena a : this.arenas) {
			arenas.set("ARENAS." + a.getName() + ".ENDERPEARLS", a.isEnderpearls());
			arenas.set("ARENAS." + a.getName() + ".ENABLED", a.isEnabled());
			arenas.set("ARENAS." + a.getName() + ".MAX_BUILD_HEIGHT", a.getMaxBuildHeight());
			if(a.getIcon() != null) arenas.set("ARENAS." + a.getName() + ".ICON", a.getIcon().getType().name() + ":" + a.getIcon().getData().getData());
			if(a.getBounds() != null) arenas.set("ARENAS." + a.getName() + ".BOUNDS", Serializer.serializeCuboid(a.getBounds()));
			if(a.getCorner1() != null) arenas.set("ARENAS." + a.getName() + ".LOCATIONS.CORNER_1", Serializer.serializeLocation(a.getCorner1()));
			if(a.getCorner2() != null) arenas.set("ARENAS." + a.getName() + ".LOCATIONS.CORNER_2", Serializer.serializeLocation(a.getCorner2()));
			if(a.getSpectatorsSpawn() != null) arenas.set("ARENAS." + a.getName() + ".LOCATIONS.SPECTATORS_SPAWN", Serializer.serializeLocation(a.getSpectatorsSpawn()));
			if(a.getEventsSpawn() != null) arenas.set("ARENAS." + a.getName() + ".LOCATIONS.EVENTS_SPAWN", Serializer.serializeLocation(a.getEventsSpawn()));
			arenas.set("ARENAS." + a.getName() + ".OPTIONS.ALLOWS_RANKED", a.isAllowsRanked());
			arenas.set("ARENAS." + a.getName() + ".OPTIONS.ALLOWS_PARTIES", a.isAllowsParties());
			arenas.set("ARENAS." + a.getName() + ".OPTIONS.ALLOWS_TOURNAMENTS", a.isAllowsTournaments());
			
			if(a.getSupportedLadders().size() > 0) {
				List<String> ladders = new ArrayList<>();
				
				for(Ladder l : a.getSupportedLadders()) {
					ladders.add(l.name());
				}
				
				arenas.set("ARENAS." + a.getName() + ".SUPPORTED_LADDERS", ladders);
			}
			
			if(a.getSupportedEvents().size() > 0) {
				List<String> events = new ArrayList<>();
				
				for(EventType e : a.getSupportedEvents()) {
					events.add(e.name());
				}
				
				arenas.set("ARENAS." + a.getName() + ".SUPPORTED_EVENTS", events);
			}
			
			if(a.getParkourCheckpoints().size() > 0) {
				List<String> checkpoints = new ArrayList<>();
				
				for(Location l : a.getParkourCheckpoints()) {
					checkpoints.add(Serializer.serializeLocation(l));
				}
				
				arenas.set("ARENAS." + a.getName() + ".PARKOUR_CHECKPOINTS", checkpoints);
			}
			
			if(a.getBridgesRedClaim() != null) {
				arenas.set("ARENAS." + a.getName() + ".BRIDGES_CLAIMS.RED", Serializer.serializeClaim(a.getBridgesRedClaim()));
			}
			
			if(a.getBridgesBlueClaim() != null) {
				arenas.set("ARENAS." + a.getName() + ".BRIDGES_CLAIMS.BLUE", Serializer.serializeClaim(a.getBridgesBlueClaim()));
			}
		}
		
		Practice.getInstance().getConfigurationManager().saveArenas();
	}
}
