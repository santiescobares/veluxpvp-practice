package club.veluxpvp.practice.kit.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.kit.Kit;
import club.veluxpvp.practice.kit.KitManager;
import club.veluxpvp.practice.kit.menu.EditKitsMenu;
import club.veluxpvp.practice.kit.menu.KitItemsMenu;
import club.veluxpvp.practice.kit.menu.listener.EditKitsMenuListener;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.match.event.MatchPreStartEvent;
import club.veluxpvp.practice.utilities.ChatUtil;

public class KitEditorListener implements Listener {

	private KitManager km;
	
	public KitEditorListener() {
		this.km = Practice.getInstance().getKitManager();
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if(!km.isEditingKit(player)) return;
		
		event.setCancelled(true);
		event.setUseInteractedBlock(Result.DENY);
		event.setUseItemInHand(Result.DENY);
		player.updateInventory();
		
		Block block = event.getClickedBlock();
		
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if(block == null || block.getType() == Material.AIR) return;
		
		if(block.getType() == Material.SIGN || block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN) {
			km.sendAgainToSpawn(player, false);
			return;
		}
		
		if(block.getType() == Material.CHEST) {
			KitItemsMenu menu = new KitItemsMenu(player, km.getEditingKitType(player));
			
			if(menu.getButtons().size() == 0) {
				player.sendMessage(ChatUtil.TRANSLATE("&cThis kit type doesn't have items to add/change! You can only reorganize your inventory."));
				return;
			}
			
			menu.openMenu(player);
			return;
		}
		
		if(block.getType() == Material.ANVIL) {
			EditKitsMenu menu = new EditKitsMenu(player, km.getEditingKitType(player));
			menu.openMenu(player);
			return;
		}
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		
		if(!km.isEditingKit(player)) return;
		
		event.getItemDrop().remove();
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		String message = event.getMessage();
		Kit renamingKit = EditKitsMenuListener.renamingKit.get(player.getUniqueId());
		
		if(!km.isEditingKit(player)) return;
		if(renamingKit == null) return;
		
		event.setCancelled(true);
		
		if(message.equalsIgnoreCase("Cancel")) {
			player.sendMessage(ChatUtil.TRANSLATE("&cRenaming cancelled!"));
			
			EditKitsMenuListener.renamingKit.remove(player.getUniqueId());
			
			EditKitsMenu menu = new EditKitsMenu(player, renamingKit.getType());
			menu.openMenu(player);
			return;
		}
		
		String translatedName = ChatUtil.TRANSLATE(message);
		
		if(translatedName.length() > 32) {
			player.sendMessage(ChatUtil.TRANSLATE("&cThe kit name can't be longer than 32 characters (ignoring color codes)."));
			return;
		}
		
		renamingKit.setDisplayName(message);

		player.sendMessage(ChatUtil.TRANSLATE("&aKit successfully renamed to " + message + "&a!"));
		EditKitsMenuListener.renamingKit.remove(player.getUniqueId());
		EditKitsMenu menu = new EditKitsMenu(player, renamingKit.getType());
		
		menu.openMenu(player);
		return;
	}
	
	@EventHandler
	public void onMatchPreStart(MatchPreStartEvent event) {
		Match match = event.getMatch();
		
		for(Player p : match.getAlivePlayers()) {
			if(km.isEditingKit(p)) km.sendAgainToSpawn(p, true);
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) { if(km.isEditingKit(event.getPlayer())) km.sendAgainToSpawn(event.getPlayer(), true); }
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) { if(km.isEditingKit(event.getPlayer())) km.sendAgainToSpawn(event.getPlayer(), true); }
}
