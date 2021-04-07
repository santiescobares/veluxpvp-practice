package club.veluxpvp.practice.arena;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.event.EventType;
import club.veluxpvp.practice.utilities.Cuboid;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Arena {

	private String name;
	private ItemStack icon;
	private Location corner1, corner2, spectatorsSpawn, eventsSpawn;
	private boolean enabled;
	private boolean allowsRanked, allowsParties, allowsTournaments, enderpearls;
	private boolean inUse;
	private int maxBuildHeight;
	private List<Ladder> supportedLadders;
	private List<EventType> supportedEvents;
	private List<Location> parkourCheckpoints;
	private Cuboid bounds;
	private Claim bridgesRedClaim, bridgesBlueClaim;
	
	public Arena(String name) {
		this.name = name;
		this.icon = null;
		this.corner1 = null;
		this.corner2 = null;
		this.spectatorsSpawn = null;
		this.eventsSpawn = null;
		this.enabled = false;
		this.allowsRanked = false;
		this.allowsParties = false;
		this.allowsTournaments = false;
		this.enderpearls = true;
		this.inUse = false;
		this.maxBuildHeight = 100;
		this.supportedLadders = Lists.newArrayList();
		this.supportedEvents = Lists.newArrayList();
		this.parkourCheckpoints = Lists.newArrayList();
		this.bounds = null;
		this.bridgesRedClaim = null;
		this.bridgesBlueClaim = null;
	}
	
	public boolean addSupportedLadder(Ladder ladder) {
		if(!this.supportedLadders.contains(ladder)) {
			this.supportedLadders.add(ladder);
			return true;
		}
		
		return false;
	}
	
	public boolean removeSupportedLadder(Ladder ladder) {
		if(this.supportedLadders.contains(ladder)) {
			this.supportedLadders.remove(ladder);
			return true;
		}
		
		return false;
	}
	
	public boolean addSupportedEvent(EventType type) {
		if(!this.supportedEvents.contains(type)) {
			this.supportedEvents.add(type);
			return true;
		}
		
		return false;
	}
	
	public boolean removeSupportedEvent(EventType type) {
		if(this.supportedEvents.contains(type)) {
			this.supportedEvents.remove(type);
			return true;
		}
		
		return false;
	}
	
	public boolean addCheckpoint(Location checkpoint) {
		if(!this.parkourCheckpoints.contains(checkpoint)) {
			this.parkourCheckpoints.add(checkpoint);
			return true;
		}
		
		return false;
	}
	
	public boolean removeCheckpoint(Location checkpoint) {
		if(this.parkourCheckpoints.contains(checkpoint)) {
			this.parkourCheckpoints.remove(checkpoint);
			return true;
		}
		
		return false;
	}
	
	public boolean isLadderSupported(Ladder ladder) {
		return this.supportedLadders.contains(ladder);
	}
	
	public boolean isEventSupported(EventType type) {
		return this.supportedEvents.contains(type);
	}
	
	public boolean canEnable() {
		if(this.corner1 == null) return false;
		if(this.corner2 == null) return false;
		if(this.spectatorsSpawn == null) return false;
		if(this.bounds == null) return false;
		if(!Practice.getInstance().getConfig().contains("SPAWN")) return false;
		
		if(this.isLadderSupported(Ladder.PARKOUR)) {
			if(this.parkourCheckpoints.size() == 0) return false;
		}
		
		if(this.isLadderSupported(Ladder.BRIDGES)) {
			if(this.bridgesRedClaim == null || this.bridgesBlueClaim == null) return false;
		}
		
		if(this.supportedEvents.size() > 0) {
			if(this.eventsSpawn == null) return false;
			
			return true;
		}
		
		if(this.supportedLadders.size() == 0) return false;
		
		return true;
	}
	
	public boolean isCheckpoint(Location location) {
		return this.parkourCheckpoints.contains(location);
	}
	
	public Claim getClaimAt(Location location) {
		if(this.bridgesRedClaim != null) {
			if(this.bridgesRedClaim.isInsideClaim(location)) return this.bridgesRedClaim;
		}
		
		if(this.bridgesBlueClaim != null) {
			if(this.bridgesBlueClaim.isInsideClaim(location)) return this.bridgesBlueClaim;
		}
		
		return null;
	}
}
