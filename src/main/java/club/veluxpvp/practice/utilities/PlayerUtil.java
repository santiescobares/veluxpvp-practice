package club.veluxpvp.practice.utilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.event.PracticeEvent;
import club.veluxpvp.practice.match.HealingType;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.party.Party;
import club.veluxpvp.practice.profile.Profile;
import club.veluxpvp.practice.scoreboard.provider.AssembleBoard;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import pt.foxspigot.jar.knockback.KnockbackModule;
import pt.foxspigot.jar.knockback.KnockbackProfile;

public class PlayerUtil {

	public static void reset(Player player, GameMode gamemode, boolean disableFly) {
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.getActivePotionEffects().stream().forEach(e -> player.removePotionEffect(e.getType()));
		player.setGameMode(gamemode);
		player.setMaxHealth(20);
		player.setHealth(20);
		player.setFoodLevel(20);
		player.setExp(0);
		player.setLevel(0);
		player.setExhaustion(-10);
		player.setFireTicks(0);
		
		if(disableFly) {
			player.setFlying(false);
			player.setAllowFlight(false);
		}

		player.updateInventory();
	}
	
	public static void resetInventory(Player player) {
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.updateInventory();
	}
	
	public static String getName(Player player) {
		return ChatColor.stripColor(ChatUtil.TRANSLATE(player.getName()));
	}
	
	public static boolean sendToSpawn(Player player) {
		Location spawn = null;
		
		if(Practice.getInstance().getConfig().contains("SPAWN")) spawn = Serializer.deserializeLocation(Practice.getInstance().getConfig().getString("SPAWN"));
		
		if(spawn == null) {
			return false;
		}
		
		player.teleport(spawn);
		player.getInventory().setHeldItemSlot(0);
		return true;
	}
	
	public static boolean sendToKitEditorSpawn(Player player) {
		Location spawn = null;
		
		if(Practice.getInstance().getConfig().contains("KIT_EDITOR_SPAWN")) spawn = Serializer.deserializeLocation(Practice.getInstance().getConfig().getString("KIT_EDITOR_SPAWN"));
		
		if(spawn == null) {
			return false;
		}
		
		player.teleport(spawn);
		player.getInventory().setHeldItemSlot(0);
		return true;
	}
	
	public static boolean hasEmptySlots(Player player, int slots) {
		int emptySlots = 0;
		
		for(int i = 0; i < player.getInventory().getSize(); i++) {
			if(player.getInventory().getItem(i) == null) {
				emptySlots++;
			}
		}
		
		if(emptySlots >= slots) {
			return true;
		}
		
		return false;
	}

	public static int getPing(Player player) {
		CraftPlayer cp = (CraftPlayer) player;
		return cp.getHandle().ping;
	}
	
	public static String getDistance(Player player, Player target) {
		Location playerLoc = player.getLocation();
		Location targetLoc = target.getLocation();

		if(!playerLoc.getWorld().getName().equals(targetLoc.getWorld().getName())) return "";
		
		String distance = "";
		int distanceDiff = 0;
		int playerX = playerLoc.getBlockX();
		int targetX = targetLoc.getBlockX();
		
		if(playerX >= targetX) {
			distanceDiff += playerX - targetX;
		} else {
			distanceDiff += targetX - playerX;
		}
		
		int playerZ = playerLoc.getBlockZ();
		int targetZ = targetLoc.getBlockZ();
		
		if(playerZ >= targetZ) {
			distanceDiff += playerZ - targetZ;
		} else {
			distanceDiff += targetZ - playerZ;
		}
		
		distance += distanceDiff;
		return distance;
	}
	
	public static void updateScoreboard(Player player) {
		Profile profile = Practice.getInstance().getProfileManager().getProfile(player);
		
		if(profile == null) return;
		if(profile.isScoreboard()) {
			Practice.getInstance().getScoreboardManager().getBoards().put(player.getUniqueId(), new AssembleBoard(player, Practice.getInstance().getScoreboardManager()));
		} else {
			Practice.getInstance().getScoreboardManager().getBoards().remove(player.getUniqueId());
			Scoreboard mainScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
			
			if(mainScoreboard != null) player.setScoreboard(mainScoreboard);
		}
	}
	
	public static void updateKnockback(Player player) {
		Match match = Practice.getInstance().getMatchManager().getPlayerMatch(player);
		EntityPlayer p = ((CraftPlayer) player).getHandle();
		
		if(match == null || match.isSpectating(player)) {
			p.setKnockback(KnockbackModule.getByName("default"));
		} else {
			KnockbackProfile kb = null;
			
			switch(match.getLadder()) {
			case NO_DEBUFF:
				kb = KnockbackModule.getByName("no_debuff");
				break;
			case DEBUFF:
				kb = KnockbackModule.getByName("no_debuff");
				break;
			case BUILD_UHC:
				kb = KnockbackModule.getByName("build_uhc");
				break;
			case FINAL_UHC:
				kb = KnockbackModule.getByName("build_uhc");
				break;
			case GAPPLE:
				kb = KnockbackModule.getByName("gapple");
				break;
			case COMBO_FLY:
				kb = KnockbackModule.getByName("combo_fly");
				break;
			case SUMO:
				kb = KnockbackModule.getByName("sumo");
				break;
			case SOUP:
				kb = KnockbackModule.getByName("soup");
				break;
			case ARCHER:
				kb = KnockbackModule.getByName("archer");
				break;
			case HCF:
				kb = KnockbackModule.getByName("hcf");
				break;
			case BRIDGES:
				kb = KnockbackModule.getByName("bridges");
				break;
			case HCT_NO_DEBUFF:
				kb = KnockbackModule.getByName("hcf");
				break;
			case HCT_DEBUFF:
				kb = KnockbackModule.getByName("hcf");
				break;
			default:
				kb = KnockbackModule.getByName("default");
			}
			
			if(kb == null) kb = KnockbackModule.getByName("default");
			
			p.setKnockback(kb);
		}
	}
	
	public static String getDirection(Player player) {
		String dir = "";
		double rotation = (player.getLocation().getYaw() - 90.0F) % 360.0F;
		
        if (rotation < 0) {
            rotation += 360.0;
        }
        
        if (0 <= rotation && rotation < 22.5) {
            dir = "N";
        }
        
        if (22.5 <= rotation && rotation < 67.5) {
            dir = "NE";
        }
        
        if (67.5 <= rotation && rotation < 112.5) {
            dir = "E";
        }
        
        if (112.5 <= rotation && rotation < 157.5) {
            dir = "SE";
        }
        
        if (157.5 <= rotation && rotation < 202.5) {
        	dir = "S";
        }
        
        if (202.5 <= rotation && rotation < 247.5) {
            dir = "SW";
        }
        
        if (247.5 <= rotation && rotation < 292.5) {
            dir = "W";
        }
        
        if (292.5 <= rotation && rotation < 337.5) {
            dir = "NW";
        }
        
        if (337.5 <= rotation && rotation <= 360) {
            dir = "N";
        }
		
		return dir;
	}
	
	public static int getHealingLeft(ItemStack[] contents, HealingType type) {
		int healingLeft = 0;
		
		for(int i = 0; i < contents.length; i++) {
			ItemStack item = contents[i];
			
			if(item == null || item.getType() == Material.AIR) continue;
			
			if(type == HealingType.HEALTH_POTION) {
				if(item.equals(new ItemStack(Material.POTION, item.getAmount(), (short) 16421))) {
					healingLeft += item.getAmount();
				}
			}
			
			if(type == HealingType.GOLDEN_APPLE) {
				if(item.equals(new ItemStack(Material.GOLDEN_APPLE, item.getAmount(), (short) 0))) {
					healingLeft += item.getAmount();
				}
			}
			
			if(type == HealingType.GAPPLE) {
				if(item.equals(new ItemStack(Material.GOLDEN_APPLE, item.getAmount(), (short) 1))) {
					healingLeft += item.getAmount();
				}
			}
			
			if(type == HealingType.SOUP) {
				if(item.equals(new ItemStack(Material.MUSHROOM_SOUP, item.getAmount(), (short) 0))) {
					healingLeft += item.getAmount();
				}
			}
		}
		
		return healingLeft;
	}
	
	public static double getHealth(Player player) {
		double health = 0.0D;
		double ph = player.getHealth();
		
		if(ph == 20.0D) health = 10.0D;
		if(ph < 20.0D && ph >= 19.0D) health = 9.5D;
		if(ph < 19.0D && ph >= 18.0D) health = 9.0D;
		if(ph < 18.0D && ph >= 17.0D) health = 8.5D;
		if(ph < 17.0D && ph >= 16.0D) health = 8.0D;
		if(ph < 16.0D && ph >= 15.0D) health = 7.5D;
		if(ph < 15.0D && ph >= 14.0D) health = 7.0D;
		if(ph < 14.0D && ph >= 13.0D) health = 6.5D;
		if(ph < 13.0D && ph >= 12.0D) health = 6.0D;
		if(ph < 12.0D && ph >= 11.0D) health = 5.5D;
		if(ph < 11.0D && ph >= 10.0D) health = 5.0D;
		if(ph < 10.0D && ph >= 9.0D) health = 4.5D;
		if(ph < 9.0D && ph >= 8.0D) health = 4.0D;
		if(ph < 8.0D && ph >= 7.0D) health = 3.5D;
		if(ph < 7.0D && ph >= 6.0D) health = 3.0D;
		if(ph < 6.0D && ph >= 5.0D) health = 2.5D;
		if(ph < 5.0D && ph >= 4.0D) health = 2.0D;
		if(ph < 4.0D && ph >= 3.0D) health = 1.5D;
		if(ph < 3.0D && ph >= 2.0D) health = 1.0D;
		if(ph < 2.0D && ph > 0.0D) health = 0.5D;
		if(ph == 0.0D) health = 0.0D;
		
		return health;
	}
	
	public static String colorName(Player player, Player others) {
		Match match = Practice.getInstance().getMatchManager().getPlayerMatch(player);
		Match otherMatch = Practice.getInstance().getMatchManager().getPlayerMatch(others);
		
		if(match == null || otherMatch == null) return ChatColor.AQUA + player.getName();
		
		if(match.getLadder() == Ladder.BRIDGES) {
			return match.getPlayerTeam(player) == match.getTeam1() ? ChatColor.RED + player.getName() : ChatColor.BLUE + player.getName();
		}
		
		if(match.isSpectating(others) && match.getPlayerTeam(others) == null) {
			return match.getPlayerTeam(player) != null && match.getPlayerTeam(player) == match.getTeam1() ? ChatColor.RED + player.getName() : ChatColor.BLUE + player.getName();
		}
		
		if(!match.isFfa() && match.getPlayerTeam(player) == match.getPlayerTeam(others)) {
			return ChatColor.GREEN + player.getName();
		}
		
		return ChatColor.RED + player.getName();
	}
	
    public static void updateVisibilityFlicker(Player target) {
        for(Player otherPlayers : Bukkit.getOnlinePlayers()) {
            target.hidePlayer(otherPlayers);
            otherPlayers.hidePlayer(target);
        }

        Bukkit.getScheduler().runTaskLater(Practice.getInstance(), () -> updateVisibility(target), 10L);
    }
	
	public static void updateVisibility(Player player) {
		((CraftPlayer) player).getHandle().collidesWithEntities = collideWithEntities(player);
		
		for(Player otherPlayers : Bukkit.getOnlinePlayers()) {	
			if(shouldSee(player, otherPlayers)) {
				player.showPlayer(otherPlayers);
			} else {
				player.hidePlayer(otherPlayers);
			}
			
			if(shouldSee(otherPlayers, player)) {
				otherPlayers.showPlayer(player);
			} else {
				otherPlayers.hidePlayer(player);
			}
			
			((CraftPlayer) otherPlayers).getHandle().collidesWithEntities = collideWithEntities(otherPlayers);
		}
	}
	
	public static boolean shouldSee(Player player, Player target) {
		Match playerMatch = Practice.getInstance().getMatchManager().getPlayerMatch(player);
		Match targetMatch = Practice.getInstance().getMatchManager().getPlayerMatch(target);
		
		if(target.hasMetadata("Vanished")) return false;
		if(player.hasMetadata("StaffMode") && !target.hasMetadata("Vanished")) return true;
		
		if(playerMatch == null && targetMatch == null) {
			Party playerParty = Practice.getInstance().getPartyManager().getPlayerParty(player);
			Party targetParty = Practice.getInstance().getPartyManager().getPlayerParty(target);
			
			if(playerParty == null || targetParty == null || playerParty != targetParty) return false;
			if(playerParty == targetParty) return true;
		}
		
		if(Practice.getInstance().getEventManager().isOnEvent(player) && Practice.getInstance().getEventManager().isOnEvent(target)) {
			PracticeEvent activeEvent = Practice.getInstance().getEventManager().getActiveEvent();
			
			if(activeEvent.isSpectating(player) && !activeEvent.isSpectating(target)) return true;
			if(!activeEvent.isSpectating(player) && activeEvent.isSpectating(target)) return false;
			
			return true;
		}
		
		if(playerMatch == null || targetMatch == null || playerMatch != targetMatch) return false;
		
		if(playerMatch.isSpectating(player) && !playerMatch.isSpectating(target)) return true;
		if(!playerMatch.isSpectating(player) && playerMatch.isSpectating(target)) return false;
		
		if(playerMatch.isSpectating(player) && playerMatch.isSpectating(target)) {
			return playerMatch.getShowingSpectators().getOrDefault(player.getUniqueId(), true);
		}
		
		return true;
	}
	
	private static boolean collideWithEntities(Player player) {
		Match match = Practice.getInstance().getMatchManager().getPlayerMatch(player);
		if(match != null && match.isSpectating(player)) return false;
		return true;
	}
	
	public static boolean isInLobby(Player player) {
		return Practice.getInstance().getMatchManager().getPlayerMatch(player) == null;
	}
}
