package club.veluxpvp.practice.listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.Settings;
import club.veluxpvp.practice.elo.menu.EloMenu;
import club.veluxpvp.practice.item.ItemManager;
import club.veluxpvp.practice.kit.menu.KitEditorMenu;
import club.veluxpvp.practice.leaderboard.menu.LeaderboardMenu;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.match.MatchManager;
import club.veluxpvp.practice.match.menu.SpectateMatchMenu;
import club.veluxpvp.practice.menu.Menu;
import club.veluxpvp.practice.menu.MenuManager;
import club.veluxpvp.practice.party.Party;
import club.veluxpvp.practice.profile.Profile;
import club.veluxpvp.practice.profile.ProfileManager;
import club.veluxpvp.practice.queue.menu.RankedQueueMenu;
import club.veluxpvp.practice.queue.menu.UnrankedQueueMenu;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.PlayerUtil;

public class GeneralListener implements Listener {

	private ProfileManager pm;
	private MatchManager mm;
	
	public GeneralListener() {
		this.pm = Practice.getInstance().getProfileManager();
		this.mm = Practice.getInstance().getMatchManager();
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Profile profile = pm.getProfile(player);
		
		event.setJoinMessage(null);
		
		if(profile == null) {
			profile = new Profile(player.getUniqueId());
			pm.getProfiles().add(profile);
		} else {
			profile.setJoinedAt(System.currentTimeMillis());
		}
		
		PlayerUtil.reset(player, GameMode.SURVIVAL, false);
		ItemManager.loadLobbyItems(player);
		
		if(player.hasPermission("practice.flyonjoin")) {
			player.setAllowFlight(true);
			player.setFlying(true);
		}
		
		PlayerUtil.updateVisibilityFlicker(player);
		PlayerUtil.updateKnockback(player);
		Practice.getInstance().getNametagManager().updateNametag(player);
		Bukkit.getScheduler().runTaskLater(Practice.getInstance(), () -> {
			PlayerUtil.sendToSpawn(player);
			player.setMaximumNoDamageTicks(20);
		}, 2L);
		
		player.sendMessage(ChatUtil.SHORTER_LINE());
		player.sendMessage(" ");
		player.sendMessage(ChatUtil.TRANSLATE(" &b&lWelcome " + player.getName() + " to VeluxPvP!"));
		player.sendMessage(" ");
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &fStore&7: &bstore.veluxpvp.club"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &fTwitter&7: &btwitter.com/VeluxPvPNet"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &fDiscord&7: &bdiscord.veluxpvp.club"));
		player.sendMessage(" ");
		player.sendMessage(ChatUtil.SHORTER_LINE());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		event.getPlayer().setMaximumNoDamageTicks(20);
		Profile profile = pm.getProfile(event.getPlayer());
		
		event.setQuitMessage(null);
		
		profile.setLeftAt(System.currentTimeMillis());
		profile.setPlayTime(profile.getPlayTime() + (profile.getLeftAt() - profile.getJoinedAt()));
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		event.getPlayer().setMaximumNoDamageTicks(20);
		Profile profile = pm.getProfile(event.getPlayer());
		
		event.setLeaveMessage(null);
		
		profile.setLeftAt(System.currentTimeMillis());
		profile.setPlayTime(profile.getPlayTime() + (profile.getLeftAt() - profile.getJoinedAt()));
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) { event.setDeathMessage(null); }
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		boolean inEvent = Practice.getInstance().getEventManager().isOnEvent(player);
		
		if(match != null || inEvent) return;
		
		if(player.hasPermission("practice.bypass.restrictions")) {
			if(player.getGameMode() == GameMode.CREATIVE) {
				return;
			}
		}
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		boolean inEvent = Practice.getInstance().getEventManager().isOnEvent(player);
		
		if(match != null || inEvent) return;
		
		if(player.hasPermission("practice.bypass.restrictions")) {
			if(player.getGameMode() == GameMode.CREATIVE) {
				return;
			}
		}
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		boolean inEvent = Practice.getInstance().getEventManager().isOnEvent(player);
		
		if(match != null || inEvent) return;
		if(Practice.getInstance().getKitManager().isEditingKit(player)) return;
		
		if(player.hasPermission("practice.bypass.restrictions")) {
			if(player.getGameMode() == GameMode.CREATIVE) {
				return;
			}
		}
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		boolean inEvent = Practice.getInstance().getEventManager().isOnEvent(player);
		
		if(match != null || inEvent) return;
		
		if(player.hasPermission("practice.bypass.restrictions")) {
			if(player.getGameMode() == GameMode.CREATIVE) {
				return;
			}
		}
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(event.getEntity() != null && event.getEntity() instanceof Player && event.getDamager() != null) {
			Player player = (Player) event.getEntity();
			Match match = mm.getPlayerMatch(player);
			boolean inEvent = Practice.getInstance().getEventManager().isOnEvent(player);
			
			if(match != null || inEvent) return;
			
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.getEntity() != null && event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Match match = mm.getPlayerMatch(player);
			boolean inEvent = Practice.getInstance().getEventManager().isOnEvent(player);
			
			if(match != null || inEvent) return;
			
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Match match = mm.getPlayerMatch(player);
		boolean inEvent = Practice.getInstance().getEventManager().isOnEvent(player);
		Menu menu = MenuManager.getOpenedMenu(player);
		
		if(menu != null && menu instanceof EloMenu || menu instanceof LeaderboardMenu) {
			event.setCancelled(true);
			return;
		}
		
		if(match != null || inEvent) return;
		if(Practice.getInstance().getKitManager().isEditingKit(player)) return;
		if(MenuManager.getOpenedMenu(player) != null) return;
		
		if(player.hasPermission("practice.bypass.restrictions")) {
			if(player.getGameMode() == GameMode.CREATIVE) {
				return;
			}
		}
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		boolean inEvent = Practice.getInstance().getEventManager().isOnEvent(player);
		
		if(match != null || inEvent) return;
		if(Practice.getInstance().getKitManager().isEditingKit(player)) return;
		
		if(event.getAction().name().startsWith("RIGHT_CLICK")) {
			ItemStack item = event.getItem();
			
			if(item == null || item.getType() == Material.AIR) return;
			
			// ~ In Lobby ~
			// Unranked Queue
			if(item.equals(ItemManager.getLobbyUnranked())) {
				event.setCancelled(true);
				event.setUseInteractedBlock(Result.DENY);
				event.setUseItemInHand(Result.DENY);
				
				UnrankedQueueMenu menu = new UnrankedQueueMenu(player);
				menu.openMenu(player);
				
				return;
			}
			
			// Ranked Queue
			if(item.equals(ItemManager.getLobbyRanked())) {
				event.setCancelled(true);
				event.setUseInteractedBlock(Result.DENY);
				event.setUseItemInHand(Result.DENY);
				
				int unrankedWins = pm.getProfile(player).getUnrankedWins();
				if(unrankedWins < Settings.REQUIRED_UNRANKED_WINS) {
					if(!player.hasPermission("practice.bypass.unrankedrequired")) {
						player.sendMessage(ChatUtil.TRANSLATE("&cYou must have at least " + Settings.REQUIRED_UNRANKED_WINS + " unranked wins to join a ranked queue! You have " + unrankedWins + "."));
						return;
					}
				}
				
				RankedQueueMenu menu = new RankedQueueMenu(player);
				menu.openMenu(player);
				
				return;
			}
			
			// Create a Party
			if(item.equals(ItemManager.getLobbyParty())) {
				event.setCancelled(true);
				event.setUseInteractedBlock(Result.DENY);
				event.setUseItemInHand(Result.DENY);
				
				Party party = new Party(player);
				Practice.getInstance().getPartyManager().getParties().add(party);
				
				if(PlayerUtil.isInLobby(player)) {
					PlayerUtil.reset(player, player.getGameMode(), false);
					ItemManager.loadPartyItems(player);
				}
				
				return;
			}
			
			// Spectate Menu
			if(item.equals(ItemManager.getLobbySpectateMenu())) {
				event.setCancelled(true);
				event.setUseInteractedBlock(Result.DENY);
				event.setUseItemInHand(Result.DENY);
				
				SpectateMatchMenu menu = new SpectateMatchMenu(player);
				menu.openMenu(player);
				return;
			}
			
			// Your Statistics
			if(item.getType() == Material.SKULL_ITEM && item.hasItemMeta()) {
				event.setCancelled(true);
				event.setUseInteractedBlock(Result.DENY);
				event.setUseItemInHand(Result.DENY);
				
				EloMenu menu = new EloMenu(player, Practice.getInstance().getProfileManager().getProfile(player));
				menu.openMenu(player);
				return;
			}
			
			// Kit Editor
			if(item.equals(ItemManager.getLobbyKitEditor())) {
				event.setCancelled(true);
				event.setUseInteractedBlock(Result.DENY);
				event.setUseItemInHand(Result.DENY);
				
				KitEditorMenu menu = new KitEditorMenu(player);
				menu.openMenu(player);
				return;
			}
			
			// ~ In Queue ~
			// Leave Queue
			if(item.equals(ItemManager.getQueueLeaveQueue())) {
				event.setCancelled(true);
				event.setUseInteractedBlock(Result.DENY);
				event.setUseItemInHand(Result.DENY);
				
				Practice.getInstance().getQueueManager().removePlayer(player, false, false);
				return;
			}
		}
		
		Block block = event.getClickedBlock();
		
		if(block == null || block.getType() == Material.AIR) return;
		
		if(event.getAction() == Action.PHYSICAL && block.getType() == Material.SOIL) {
            event.setCancelled(true);
            return;
        }
		
		if(block.getType() == Material.TRAP_DOOR || block.getType() == Material.ITEM_FRAME || 
				block.getType() == Material.LEVER || block.getType() == Material.STONE_BUTTON || block.getType() == Material.WOOD_BUTTON || 
				block.getType() == Material.STONE_PLATE || block.getType() == Material.WOOD_PLATE || block.getType() == Material.FENCE_GATE || 
				block.getType() == Material.WOOD_DOOR || block.getType() == Material.WOODEN_DOOR || block.getType() == Material.ACACIA_DOOR || 
				block.getType() == Material.BIRCH_DOOR || block.getType() == Material.DARK_OAK_DOOR || block.getType() == Material.JUNGLE_DOOR || 
				block.getType() == Material.SPRUCE_DOOR || block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST || 
				block.getType() == Material.FURNACE || block.getType() == Material.HOPPER || block.getType() == Material.ENCHANTMENT_TABLE || 
				block.getType() == Material.WORKBENCH || block.getType() == Material.BREWING_STAND || block.getType() == Material.BEACON || 
				block.getType() == Material.REDSTONE_COMPARATOR) {
			
			if(player.hasPermission("practice.bypass.restrictions")) {
				if(player.getGameMode() == GameMode.CREATIVE) {
					return;
				}
			}
			
			event.setCancelled(true);
			event.setUseInteractedBlock(Result.DENY);
			event.setUseItemInHand(Result.DENY);
		}
	}
	
	@EventHandler
	public void onBlockSpread(BlockSpreadEvent event) { event.setCancelled(true); }
	
	@EventHandler
	public void onLeavesDecay(LeavesDecayEvent event) { event.setCancelled(true); }
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) { event.setCancelled(true); }
	
	@EventHandler
	public void onBlockBurn(BlockBurnEvent event) { event.setCancelled(true); }
	
	@EventHandler
	public void onBlockDamage(BlockDamageEvent event) {
		if(event.getBlock() != null) {
			Material type = event.getBlock().getType();
			
			if(type == Material.WHEAT || type == Material.CARROT || type == Material.SEEDS || type == Material.PUMPKIN_SEEDS || type == Material.MELON_SEEDS) event.setCancelled(true);
		}
	}
}
