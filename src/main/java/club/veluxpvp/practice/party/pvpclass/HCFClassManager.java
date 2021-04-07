package club.veluxpvp.practice.party.pvpclass;

import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.google.common.collect.Maps;

import lombok.Getter;

public class HCFClassManager {

	@Getter private Map<UUID, HCFClassType> activeClass;
	
	public HCFClassManager() {
		this.activeClass = Maps.newConcurrentMap();
	}
	
	public HCFClassType getActiveClass(Player player) {
		if(this.activeClass.containsKey(player.getUniqueId())) {
			return this.activeClass.get(player.getUniqueId());
		}
		
		return null;
	}
	
	public void setActiveClass(Player player, HCFClassType Class) {
		if(Class == null) {
			this.activeClass.remove(player.getUniqueId());
			return;
		}
		
		this.activeClass.put(player.getUniqueId(), Class);
	}
	
	public void tryActivateClass(Player player) {
		System.out.println("[ArmorEvent debug] trying to active a hcf class for " + player.getName());
		
		for(HCFClassType pvpClass : HCFClassType.values()) {
			switch(pvpClass) {
			case BARD:
				if(BardClass.canActivate(player)) {
					System.out.println("[ArmorEvent debug] " + player.getName() + " can active bard class");
					BardClass.activate(player);
					this.setActiveClass(player, HCFClassType.BARD);
					System.out.println("[ArmorEvent debug] " + player.getName() + " has enabled their bard class");
					return;
				} else {
					System.out.println("[ArmorEvent debug] " + player.getName() + " cannot active their bard class");
				}
			default:
				continue;
			}
		}
		
		this.setActiveClass(player, null);
	}
	
	public void tryDeactivateClass(Player player) {
		System.out.println("[ArmorEvent debug] trying to deactivate " + player.getName() + "'s class");
		HCFClassType activeClass = this.getActiveClass(player);
		if(activeClass == null || activeClass == HCFClassType.DIAMOND) return;
		
		switch(activeClass) {
		case BARD:
			if(!BardClass.canActivate(player)) {
				System.out.println("[ArmorEvent debug] " + player.getName() + " cannot active bard class, so deactivating it");
				BardClass.deactivate(player);
				this.setActiveClass(player, null);
				System.out.println("[ArmorEvent debug] " + player.getName() + " bard class deactivated");
				return;
			}
			
			break;
		default:
			break;
		}
	}
}
