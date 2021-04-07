package club.veluxpvp.practice.match.listener;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.kit.Kit;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.match.MatchEndReason;
import club.veluxpvp.practice.match.MatchManager;
import club.veluxpvp.practice.match.MatchState;
import club.veluxpvp.practice.match.MatchTeam;
import club.veluxpvp.practice.match.PostMatchPlayer;
import club.veluxpvp.practice.match.TeamType;
import club.veluxpvp.practice.match.event.MatchResetEvent;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.PlayerUtil;

public class MatchListener implements Listener {

	private MatchManager mm;
	private Map<UUID, Location> lastLocation = new ConcurrentHashMap<>();
	
	public MatchListener() {
		this.mm = Practice.getInstance().getMatchManager();
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		final Entity killer = player.getKiller();
		Match match = mm.getPlayerMatch(player);
		
		event.getDrops().clear();
		event.setDroppedExp(0);
		
		if(match == null) {
			lastLocation.put(player.getUniqueId(), player.getLocation());
			
			if(player.isDead()) {
				player.spigot().respawn();
				player.teleport(lastLocation.get(player.getUniqueId()));
			}
			
			lastLocation.remove(player.getUniqueId());
			return;
		}
		
		if(killer != null && killer instanceof Player) {
			match.getKills().put(player.getKiller().getUniqueId(), match.getKills().getOrDefault(player.getKiller().getUniqueId(), 0) + 1);
			match.getPlayers().stream().forEach(p -> p.sendMessage(ChatUtil.TRANSLATE(PlayerUtil.colorName(player, p) + " &7was killed by " + PlayerUtil.colorName((Player) killer, p))));
		} else {
			match.getPlayers().stream().forEach(p -> p.sendMessage(ChatUtil.TRANSLATE(PlayerUtil.colorName(player, p) + " &7died.")));
		}
		
		lastLocation.put(player.getUniqueId(), player.getLocation());
		if(!match.isParty() && !match.isFfa()) event.getDrops().removeIf(d -> d.getType() == Material.POTION || d.getType() == Material.GLASS_BOTTLE || d.getType() == Material.GOLDEN_APPLE || d.getType() == Material.MUSHROOM_SOUP);
		
		if(player.isDead()) {
			player.spigot().respawn();
			player.teleport(lastLocation.get(player.getUniqueId()));
		}
		
		lastLocation.remove(player.getUniqueId());
		
		if(match.getLadder() != Ladder.BRIDGES) {
			if(match.getLadder() == Ladder.PARKOUR) {
				player.teleport(match.getArena().getCorner1());
				return;
			}
			
			match.getPlayers().stream().forEach(p -> this.sendLightningPacket(p, this.getLightningPacket(player.getLocation())));
			match.getPlayers().stream().forEach(p -> p.playSound(player.getLocation(), Sound.AMBIENCE_THUNDER, 6F, 1.0F));
			match.setAsSpectator(player);
			
			if(match.isFfa()) {
				match.tryFinishGame(MatchEndReason.ONE_PLAYER_ALIVE);
			} else {
				match.tryFinishGame(MatchEndReason.ONE_TEAM_ALIVE);
			}
		} else {
			MatchTeam playerTeam = match.getPlayerTeam(player);
			PlayerUtil.reset(player, GameMode.SURVIVAL, true);
			
			Kit choosedKit = MatchKitListener.choosedKit.get(player.getUniqueId());
			Bukkit.getScheduler().runTaskLater(Practice.getInstance(), () -> {
				if(choosedKit != null) choosedKit.apply(player, true);
			}, 2L);
			
			Bukkit.getScheduler().runTaskLater(Practice.getInstance(), () -> player.setVelocity(new Vector()), 1L);
			player.getInventory().setHeldItemSlot(0);
			player.setHealth(20);
			player.teleport((playerTeam.getType() == TeamType.TEAM_1 ? match.getArena().getCorner1() : match.getArena().getCorner2()));
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(event.getEntity() != null && event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Match match = mm.getPlayerMatch(player);
			
			if(match == null || !match.isStarted()) return;
			if(match.isSpectating(player)) return;
			if(match.getState() == MatchState.ENDING) {
				event.setCancelled(true);
				return;
			}
			
			if(event.getDamager() != null && event.getDamager() instanceof Projectile) {
				Projectile projectile = (Projectile) event.getDamager();
				
				if(projectile.getShooter() != null && projectile.getShooter() instanceof Player) {
					Player shooter = (Player) projectile.getShooter();
					Match targetMatch = mm.getPlayerMatch(shooter);
					
					if(targetMatch == null || targetMatch.isSpectating(shooter) || match != targetMatch) return;
					if(!match.isFfa() && match.getPlayerTeam(player) == match.getPlayerTeam(shooter)) {
						event.setCancelled(true);
					} else {
						if(!(projectile instanceof Arrow)) return;
						if(match.getLadder() != Ladder.BUILD_UHC && match.getLadder() != Ladder.FINAL_UHC && match.getLadder() != Ladder.ARCHER) return;
						shooter.sendMessage(ChatUtil.TRANSLATE("&b" + player.getName() + "&f's Health&7: &c" + PlayerUtil.getHealth(player) + " &c&l♥"));
					}
				}
			}
			
			if(event.getDamager() != null && event.getDamager() instanceof Player) {
				Player damager = (Player) event.getDamager();
				
				if(!match.isFfa() && match.getPlayerTeam(player) == match.getPlayerTeam(damager)) {
					event.setCancelled(true);
					return;
				}
				
				match.getTotalHits().put(damager.getUniqueId(), match.getTotalHits().getOrDefault(damager.getUniqueId(), 0) + 1);
				match.getCurrentCombo().put(damager.getUniqueId(), match.getCurrentCombo().getOrDefault(damager.getUniqueId(), 0) + 1);
				if(match.getCurrentCombo().get(damager.getUniqueId()) > match.getLongestCombo().getOrDefault(damager.getUniqueId(), 0)) match.getLongestCombo().put(damager.getUniqueId(), match.getCurrentCombo().getOrDefault(damager.getUniqueId(), 0));
			}

			match.setLastPvPLocation(player.getLocation());
			match.getCurrentCombo().put(player.getUniqueId(), 0);
			//if(match.getLadder() == Ladder.BUILD_UHC) Practice.getInstance().getNametagManager().updateHealthDisplay(player);
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.getEntity() != null && event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Match match = mm.getPlayerMatch(player);
			
			if(match == null || !match.isStarted()) return;
			if(match.isSpectating(player)) return;
			if(match.getState() == MatchState.ENDING) {
				event.setCancelled(true);
				return;
			}
			
			//if(match.getLadder() == Ladder.BUILD_UHC) Practice.getInstance().getNametagManager().updateHealthDisplay(player);
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		
		if(match == null || match.getLadder() == Ladder.BUILD_UHC || match.getLadder() == Ladder.FINAL_UHC || match.getLadder() == Ladder.BRIDGES) return;
		if(match.isSpectating(player)) return;

		event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		
		if(match == null || match.getLadder() == Ladder.BUILD_UHC || match.getLadder() == Ladder.FINAL_UHC || match.getLadder() == Ladder.BRIDGES) return;
		if(match.isSpectating(player)) return;
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		
		if(match == null || match.isSpectating(player)) return;
		
		ItemStack item = event.getItemDrop().getItemStack();
		if(item != null && item.getType().name().contains("SWORD")) {
			event.setCancelled(true);
			player.sendMessage(ChatUtil.TRANSLATE("&cYou can't drop your sword!"));
			return;
		}
		
		if(event.getItemDrop().getItemStack().getType() == Material.GLASS_BOTTLE || event.getItemDrop().getItemStack().getType() == Material.BOWL || event.getItemDrop().getItemStack().getType() == Material.MUSHROOM_SOUP) {
			event.getItemDrop().remove();
			return;
		}
		
		Bukkit.getScheduler().runTaskLater(Practice.getInstance(), () -> event.getItemDrop().remove(), 100L);
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		
		if(match == null || match.getState() != MatchState.ENDING) return;
		if(match.isSpectating(player)) return;
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		
		if(match == null) return;
		if(match.isSpectating(player)) return;
		
		if(!match.getArena().getBounds().contains(event.getTo())) {
			if(match.getLadder() == Ladder.PARKOUR) {
				player.teleport(match.getArena().getCorner1());
			} else if(match.getLadder() == Ladder.BRIDGES) {
				MatchTeam playerTeam = match.getPlayerTeam(player);
				PlayerUtil.reset(player, GameMode.SURVIVAL, true);
				
				Kit choosedKit = MatchKitListener.choosedKit.get(player.getUniqueId());
				if(choosedKit != null) {
					choosedKit.apply(player, true);
				}
				
				player.getInventory().setHeldItemSlot(0);
				player.setHealth(20);
				player.teleport((playerTeam.getType() == TeamType.TEAM_1 ? match.getArena().getCorner1() : match.getArena().getCorner2()));
				match.getPlayers().stream().forEach(p -> p.sendMessage(ChatUtil.TRANSLATE(PlayerUtil.colorName(player, p) + " &7died.")));
			} else {
				player.teleport(match.getArena().getBounds().getCenter());
			}
		}
	}
	
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPotionLaunch(ProjectileLaunchEvent event) {
        Projectile thrownEntity = event.getEntity();
        if(!(thrownEntity instanceof ThrownPotion)) return;

        ThrownPotion thrownPotion = (ThrownPotion) thrownEntity;

        ProjectileSource projectileSource = thrownPotion.getShooter();
        if(!(projectileSource instanceof Player)) return;

        Player player = (Player) projectileSource;
        Match match = Practice.getInstance().getMatchManager().getPlayerMatch(player);

        if(match == null) return;
        match.getMissedPots().put(player.getUniqueId(), match.getMissedPots().getOrDefault(player.getUniqueId(), 0) + 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSplash(PotionSplashEvent event) {
        ThrownPotion thrownPotion = event.getEntity();

        if(thrownPotion.getItem().getDurability() != 16421) return;

        ProjectileSource projectileSource = thrownPotion.getShooter();
        if(!(projectileSource instanceof Player)) return;

        Player player = (Player) projectileSource;
        Match match = Practice.getInstance().getMatchManager().getPlayerMatch(player);

        if(match == null) return;

        match.getThrownPots().put(player.getUniqueId(), match.getThrownPots().getOrDefault(player.getUniqueId(), 0) + 1);
        
        for(LivingEntity affectedEntity : event.getAffectedEntities()) {
            if(!affectedEntity.getUniqueId().equals(player.getUniqueId())) continue;

            if(event.getIntensity(affectedEntity) == 1.0D) {
                match.getMissedPots().put(player.getUniqueId(), Math.max(match.getMissedPots().getOrDefault(player.getUniqueId(), 1) - 1, 0));
            }
        }
    }
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		
		if(match == null) return;
		
		if(!match.isSpectating(player)) {
			match.getPlayers().stream().forEach(p -> p.sendMessage(ChatUtil.TRANSLATE(PlayerUtil.colorName(player, p) + " &7disconnected.")));
			match.setAsSpectator(player);
			match.getSpectators().remove(player);
			
			if(match.isFfa()) {
				match.tryFinishGame(MatchEndReason.ONE_PLAYER_ALIVE);
			} else {
				match.tryFinishGame(MatchEndReason.ONE_TEAM_ALIVE);
			}
		} else {
			match.removeSpectator(player, false);
		}
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		
		if(match == null) return;
		
		if(!match.isSpectating(player)) {
			match.getPlayers().stream().forEach(p -> p.sendMessage(ChatUtil.TRANSLATE(PlayerUtil.colorName(player, p) + " &7disconnected.")));
			match.setAsSpectator(player);
			match.getSpectators().remove(player);
			
			if(match.isFfa()) {
				match.tryFinishGame(MatchEndReason.ONE_PLAYER_ALIVE);
			} else {
				match.tryFinishGame(MatchEndReason.ONE_TEAM_ALIVE);
			}
		} else {
			match.removeSpectator(player, false);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		
		if(event.getView().getTitle().endsWith("'s Inventory")) {
			event.setCancelled(true);
		}
		
		if(event.getClickedInventory() != null && event.getClickedInventory().getTitle().endsWith("'s Inventory")) {
			if(event.getSlot() == 45 || event.getSlot() == 53) {
				ItemStack item = event.getCurrentItem();
				
				if(item != null && item.getType() == Material.LEVER) {
					OfflinePlayer viewingPlayerInventory = Bukkit.getOfflinePlayer(event.getClickedInventory().getTitle().split("\\'")[0]);
					UUID targetUUID = Practice.getInstance().getMatchManager().getLastMatch(player).getSwapInventoriesMap().get(viewingPlayerInventory.getUniqueId());
					PostMatchPlayer postMatchTarget = Practice.getInstance().getMatchManager().getLastMatch(player).getPostMatchPlayers().get(targetUUID);
					
					player.openInventory(postMatchTarget.getInventory(true));
				}
			}
		}
	}
	
	@EventHandler
	public void onMatchEnd(MatchResetEvent event) { mm.getMatches().remove(event.getMatch()); }
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		
		if(match == null) return;
		
		String[] blockedCommands = {"/fly", "/gamemode", "/gm", "/heal", "/feed", "/staffmode", "/sm", "/staff", "/mod", "/modmode", "/mm", "/vanish", "/v"};
		
		for(int i = 0; i < blockedCommands.length; i++) {
			if(event.getMessage().equalsIgnoreCase(blockedCommands[i])) {
				if(player.hasPermission("practice.bypass.restrictedcommands")) return;
				
				event.setCancelled(true);
				player.sendMessage(ChatUtil.TRANSLATE("&cYou can't perform this action while in match!"));
				return;
			}
		}
	}
	
    @EventHandler
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        Player player = event.getPlayer();

        if(!Enchantment.PROTECTION_ENVIRONMENTAL.canEnchantItem(event.getItem())) {
            return;
        }

        if(player.getLastDamageCause() != null && player.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            if(((EntityDamageByEntityEvent) player.getLastDamageCause()).getDamager() instanceof FishHook) {
                event.setCancelled(true);
            }
        }
    }
    
    private PacketContainer getLightningPacket(Location location) {
        PacketContainer lightningPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_WEATHER);

        lightningPacket.getModifier().writeDefaults();
        lightningPacket.getIntegers().write(0, 128);
        lightningPacket.getIntegers().write(4, 1);
        lightningPacket.getIntegers().write(1, (int) (location.getX() * 32.0D));
        lightningPacket.getIntegers().write(2, (int) (location.getY() * 32.0D));
        lightningPacket.getIntegers().write(3, (int) (location.getZ() * 32.0D));

        return lightningPacket;
    }

    private void sendLightningPacket(Player target, PacketContainer packet) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(target, packet);
        } catch (InvocationTargetException ignored) {}
    }
}
