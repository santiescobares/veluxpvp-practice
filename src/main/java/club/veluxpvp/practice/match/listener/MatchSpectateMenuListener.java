package club.veluxpvp.practice.match.listener;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.match.menu.SpectateMatchButton;
import club.veluxpvp.practice.match.menu.SpectateMatchMenu;
import club.veluxpvp.practice.match.menu.SpectateRandomMatchButton;
import club.veluxpvp.practice.menu.Button;
import club.veluxpvp.practice.menu.Menu;
import club.veluxpvp.practice.menu.MenuManager;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.Preconditions;

public class MatchSpectateMenuListener implements Listener {

	public MatchSpectateMenuListener() {
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Menu openedMenu = MenuManager.getOpenedMenu(player);
		
		if(openedMenu != null && openedMenu instanceof SpectateMatchMenu) {
			event.setCancelled(true);
			
			SpectateMatchMenu menu = (SpectateMatchMenu) openedMenu;
			Button clickedButton = menu.getClickedButton(event.getSlot());
			
			if(clickedButton == null) return;
			if(clickedButton instanceof SpectateMatchButton) {
				SpectateMatchButton button = (SpectateMatchButton) clickedButton;
				Match match = button.getMatch();
				
				if(!Preconditions.canSpectate(player, match.getAlivePlayers().get(0))) return;
				
				player.closeInventory();
				match.addSpectator(player);
				return;
			}
			
			if(clickedButton instanceof SpectateRandomMatchButton) {
				List<Match> matches = Practice.getInstance().getMatchManager().getMatches().stream().collect(Collectors.toList());
				
				if(matches.size() == 0) {
					player.sendMessage(ChatUtil.TRANSLATE("&cThere are no matches for spectate!"));
					return;
				}
				
				Match match = matches.get(new Random().nextInt(matches.size()));
				
				if(!Preconditions.canSpectate(player, match.getAlivePlayers().get(0))) return;
				
				player.closeInventory();
				match.addSpectator(player);
				return;
			}
		}
	}
}
