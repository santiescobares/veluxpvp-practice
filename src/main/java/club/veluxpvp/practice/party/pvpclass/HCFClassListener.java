package club.veluxpvp.practice.party.pvpclass;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.bukkitevent.ArmorEquipEvent;
import club.veluxpvp.practice.match.Match;

public class HCFClassListener implements Listener {

	private final HCFClassManager hcfClassManager;
	
	public HCFClassListener() {
		this.hcfClassManager = Practice.getInstance().getHcfClassManager();
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler
	public void onArmorEquip(ArmorEquipEvent event) {
		Player player = event.getPlayer();
		Match match = Practice.getInstance().getMatchManager().getPlayerMatch(player);
		ItemStack newArmorPiece = event.getNewArmorPiece();
		ItemStack oldArmorPiece = event.getOldArmorPiece();
		
		if(match == null) return;
		if(match.isSpectating(player)) return;
		if(match.getLadder() != Ladder.HCT_NO_DEBUFF && match.getLadder() != Ladder.HCT_DEBUFF) return;
		
		// Is equiping
		if(newArmorPiece != null && newArmorPiece.getType() != Material.AIR) {
			System.out.println("[ArmorEvent debug] " + player.getName() + " is trying to enable a hcf class");
			if(this.hcfClassManager.getActiveClass(player) == null) this.hcfClassManager.tryActivateClass(player);
			return;
		}
		
		// Is unequiping
		if(oldArmorPiece != null && oldArmorPiece.getType() != Material.AIR) {
			System.out.println("[ArmorEvent debug] " + player.getName() + " is trying to disable a hcf class");
			if(this.hcfClassManager.getActiveClass(player) != null) this.hcfClassManager.tryDeactivateClass(player);
			return;
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		HCFClassType activeClass = this.hcfClassManager.getActiveClass(player);
		
		if(activeClass != null) {
			switch(activeClass) {
			case BARD:
				BardClass.deactivate(player);
				break;
			default:
				break;
			}
		}
		
		this.hcfClassManager.setActiveClass(player, null);
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		Player player = event.getPlayer();
		HCFClassType activeClass = this.hcfClassManager.getActiveClass(player);
		
		if(activeClass != null) {
			switch(activeClass) {
			case BARD:
				BardClass.deactivate(player);
				break;
			default:
				break;
			}
		}
		
		this.hcfClassManager.setActiveClass(player, null);
	}
}
