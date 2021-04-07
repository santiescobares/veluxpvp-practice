package club.veluxpvp.practice.duel.listener;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Arena;
import club.veluxpvp.practice.duel.Duel;
import club.veluxpvp.practice.duel.DuelManager;
import club.veluxpvp.practice.duel.menu.DuelArenaButton;
import club.veluxpvp.practice.duel.menu.DuelArenaMenu;
import club.veluxpvp.practice.duel.menu.DuelLadderButton;
import club.veluxpvp.practice.duel.menu.DuelLadderMenu;
import club.veluxpvp.practice.duel.menu.DuelRandomArenaButton;
import club.veluxpvp.practice.menu.Button;
import club.veluxpvp.practice.menu.Menu;
import club.veluxpvp.practice.menu.MenuManager;
import club.veluxpvp.practice.utilities.ChatUtil;

public class DuelMenuListener implements Listener {

	public DuelMenuListener() {
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Menu openedMenu = MenuManager.getOpenedMenu(player);
		
		if(openedMenu == null) return;
		if(openedMenu instanceof DuelLadderMenu) {
			event.setCancelled(true);
			
			DuelLadderMenu menu = (DuelLadderMenu) openedMenu;
			Button clickedButton = menu.getClickedButton(event.getSlot());
			
			if(clickedButton != null && clickedButton instanceof DuelLadderButton) {
				DuelLadderButton button = (DuelLadderButton) clickedButton;
				Duel duel = DuelManager.getMakingDuel(player);
				
				if(duel == null) return;
				if(Practice.getInstance().getArenaManager().getArenasForLadder(button.getLadder()).size() == 0) {
					player.sendMessage(ChatUtil.TRANSLATE("&cThere are no avaiable free arenas for this ladder!"));
					return;
				}
				
				duel.setLadder(button.getLadder());
				
				DuelArenaMenu newMenu = new DuelArenaMenu(player, button.getLadder());
				newMenu.openMenu(player);
				return;
			}
			
			return;
		}
		
		if(openedMenu instanceof DuelArenaMenu) {
			event.setCancelled(true);
			
			DuelArenaMenu menu = (DuelArenaMenu) openedMenu;
			Button clickedButton = menu.getClickedButton(event.getSlot());
			
			if(clickedButton == null) return;
			if(clickedButton instanceof DuelArenaButton) {
				DuelArenaButton button = (DuelArenaButton) clickedButton;
				Arena arena = button.getArena();
				
				if(arena == null || arena.isInUse()) {
					player.sendMessage(ChatUtil.TRANSLATE("&cThis arena is no longer avaiable!"));
					return;
				}
				
				Duel duel = DuelManager.getMakingDuel(player);
				
				if(duel == null) return;
				
				duel.setArena(arena);
				player.closeInventory();
				DuelManager.sendDuel(player);
				return;
			}
			
			if(clickedButton instanceof DuelRandomArenaButton) {
				Duel duel = DuelManager.getMakingDuel(player);
				
				if(duel == null) return;
				
				List<Arena> avaiableArenas = Practice.getInstance().getArenaManager().getArenasForLadder(duel.getLadder());
				
				if(avaiableArenas.size() == 0) {
					player.sendMessage(ChatUtil.TRANSLATE("&cThere are no avaiable free arenas for this ladder!"));
					return;
				}
				
				Arena arena = avaiableArenas.get(new Random().nextInt(avaiableArenas.size()));
				
				duel.setArena(arena);
				player.closeInventory();
				DuelManager.sendDuel(player);
				return;
			}
		}
	}
}
