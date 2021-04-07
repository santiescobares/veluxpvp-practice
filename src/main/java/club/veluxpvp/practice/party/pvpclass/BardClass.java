package club.veluxpvp.practice.party.pvpclass;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.utilities.ChatUtil;

public class BardClass {

	public static List<ItemStack> REQUIRED_ARMOR;
	public static List<PotionEffect> PASSIVE_EFFECTS;
	public static Map<UUID, Double> ENERGY_MAP = Maps.newConcurrentMap();
	public static Map<UUID, BukkitTask> ENERGY_TASK_MAP = Maps.newConcurrentMap();
	public static Map<UUID, Long> EFFECT_COOLDOWN_MAP = Maps.newConcurrentMap();
	private static final long EFFECT_COOLDOWN = TimeUnit.SECONDS.toMillis(8);
	
	static {
		REQUIRED_ARMOR = Arrays.asList(
				new ItemStack(Material.GOLD_HELMET),
				new ItemStack(Material.GOLD_CHESTPLATE),
				new ItemStack(Material.GOLD_LEGGINGS),
				new ItemStack(Material.GOLD_BOOTS)
				);
		PASSIVE_EFFECTS = Arrays.asList(
				new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1),
				new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0),
				new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0)
				);
	}
	
	public static double getEnergy(Player player) {
		return ENERGY_MAP.getOrDefault(player.getUniqueId(), 0.0D);
	}
	
	public static void setEnergy(Player player, double amount) {
		if(!ENERGY_MAP.containsKey(player.getUniqueId())) return;
		
		double newAmount = amount > 100 ? 100 : amount < 0 ? 0 : amount;
		ENERGY_MAP.put(player.getUniqueId(), newAmount);
	}
	
	public static boolean isOnEffectCooldown(Player player) {
		return System.currentTimeMillis() < EFFECT_COOLDOWN_MAP.getOrDefault(player.getUniqueId(), 0L);
	}
	
	public static void setOnEffectCooldown(Player player) {
		EFFECT_COOLDOWN_MAP.put(player.getUniqueId(), System.currentTimeMillis() + EFFECT_COOLDOWN);
	}
	
	public static String getEffectCooldownLeft(Player player) {
		int millisLeft = (int) (EFFECT_COOLDOWN_MAP.getOrDefault(player.getUniqueId(), System.currentTimeMillis()) - System.currentTimeMillis());
        double secondsLeft = millisLeft / 1000D;
        secondsLeft = Math.round(10D * secondsLeft) / 10D;
        
        return Double.toString(secondsLeft);
	}
	
	public static void activate(Player player) {
		PASSIVE_EFFECTS.forEach(effect -> player.addPotionEffect(effect, true));
		ENERGY_MAP.put(player.getUniqueId(), 0.0D);
		ENERGY_TASK_MAP.put(player.getUniqueId(), new BukkitRunnable() {

			@Override
			public void run() {
				double energy = ENERGY_MAP.getOrDefault(player.getUniqueId(), 0.0D);
				
				if(energy < 100.0D) {
					energy++;
					ENERGY_MAP.put(player.getUniqueId(), energy);
					
					if(energy == 10.0D || energy == 20.0D || energy == 30.0D || energy == 40.0D || energy == 50.0D || energy == 60.0D || 
							energy == 70.0D || energy == 80.0D || energy == 90.0D || energy == 100.0D) {
						player.sendMessage(ChatUtil.TRANSLATE("&bBard Energy&7: &a" + energy));
					}
				}
			}
			
		}.runTaskTimerAsynchronously(Practice.getInstance(), 20L, 20L));
		
		player.sendMessage(ChatUtil.TRANSLATE("&bBard Class &7-> &aEnabled"));
	}
	
	public static void deactivate(Player player) {
		PASSIVE_EFFECTS.forEach(effect -> player.removePotionEffect(effect.getType()));
		BukkitTask task = ENERGY_TASK_MAP.get(player.getUniqueId());
		if(task != null) task.cancel();
		ENERGY_TASK_MAP.remove(player.getUniqueId());
		ENERGY_MAP.remove(player.getUniqueId());
		EFFECT_COOLDOWN_MAP.remove(player.getUniqueId());
		
		player.sendMessage(ChatUtil.TRANSLATE("&bBard Class &7-> &cDisabled"));
	}
	
	public static boolean canActivate(Player player) {
		boolean canActivate = true;
		
		for(ItemStack requiredArmorPart : REQUIRED_ARMOR) {
			Material type = requiredArmorPart.getType();
			
			if(type.name().contains("HELMET")) {
				if(player.getInventory().getHelmet() == null || player.getInventory().getHelmet().getType() != type)
					canActivate = false;
			}
			
			if(type.name().contains("CHESTPLATE")) {
				if(player.getInventory().getChestplate() == null || player.getInventory().getChestplate().getType() != type)
					canActivate = false;
			}
			
			if(type.name().contains("LEGGINGS")) {
				if(player.getInventory().getLeggings() == null || player.getInventory().getLeggings().getType() != type)
					canActivate = false;
			}
			
			if(type.name().contains("BOOTS")) {
				if(player.getInventory().getBoots() == null || player.getInventory().getBoots().getType() != type)
					canActivate = false;
			}
		}
		
		return canActivate;
	}
	
	public static void bardEffect(Player barder, BardEffectType effectType, List<Player> toPlayers) {
		PotionEffect effect = effectType.getEffect();
		double energy = getEnergy(barder);
		
		if(isOnEffectCooldown(barder)) {
			barder.sendMessage(ChatUtil.TRANSLATE("&cYou can't bard effects for another &l" + getEffectCooldownLeft(barder) + " &cseconds!"));
			return;
		}
		
		if(energy < effectType.requiredEnergy) {
			barder.sendMessage(ChatUtil.TRANSLATE("&cThis effect requires &l" + effectType.requiredEnergy + " &cof energy and you only have &l" + energy + "&c!"));
			return;
		}
		
		List<Player> affectedPlayers = Lists.newArrayList();
		Map<Player, PotionEffect> savedEffectMap = Maps.newConcurrentMap();
		for(Player p : toPlayers) {
			if(p == barder) {
				if(!effectType.canSelfBard) continue;
			}
			
			PotionEffect activeEffect = p.getActivePotionEffects().stream()
					.filter(e -> e.getType() == effect.getType())
					.findFirst()
					.orElse(null);
			
			if(activeEffect != null) {
				if(activeEffect.getAmplifier() >= effect.getAmplifier()) continue;
			}
			
			if(activeEffect != null) savedEffectMap.put(p, activeEffect);
			p.addPotionEffect(effect, true);
			affectedPlayers.add(p);
		}
		
		barder.sendMessage(ChatUtil.TRANSLATE("You have given &b" + effectType.displayName + " " + effectType.formattedAmplifier + " &fto &a" + affectedPlayers.size() + " &fteammates!"));
		
		Bukkit.getScheduler().runTaskLater(Practice.getInstance(), () -> {
			for(Player p : affectedPlayers) {
				if(savedEffectMap.containsKey(p)) {
					if(Practice.getInstance().getMatchManager().getPlayerMatch(p) == null) {
						continue;
					}
					
					p.addPotionEffect(savedEffectMap.get(p));
					savedEffectMap.remove(p);
				}
			}
		}, effect.getDuration());
	}
	
	public static void bardHoldableEffect(Player barder, BardEffectType effectType, List<Player> toPlayers) {
		PotionEffect effect = effectType.getHoldableEffect();
		
		for(Player p : toPlayers) {
			if(p == barder) {
				if(!effectType.canSelfBard) continue;
			}
			
			PotionEffect activeEffect = p.getActivePotionEffects().stream()
					.filter(e -> e.getType() == effect.getType())
					.findFirst()
					.orElse(null);
			
			if(activeEffect != null) {
				if(activeEffect.getAmplifier() >= effect.getAmplifier()) continue;
			}

			p.addPotionEffect(effect);
		}
	}
	
	public enum BardEffectType {
		SPEED("Speed", 30.0D, "III", true),
		JUMP("Jump", 30.0D, "VII", true),
		FIRE_RESISTANCE("Fire Resistance", 30.0D, "I", true),
		REGENERATION("Regeneration", 45.0D, "III", true),
		RESISTANCE("Resistance", 45.0D, "III", true),
		STRENGTH("Strength", 45.0D, "II", false);
		
		public final String displayName;
		public final double requiredEnergy;
		public final String formattedAmplifier;
		public final boolean canSelfBard;
		
		private BardEffectType(String displayName, double requiredEnergy, String formattedAmplifier, boolean canSelfBard) {
			this.displayName = displayName;
			this.requiredEnergy = requiredEnergy;
			this.formattedAmplifier = formattedAmplifier;
			this.canSelfBard = canSelfBard;
		}
		
		public PotionEffect getEffect() {
			switch(this) {
			case SPEED:
				return new PotionEffect(PotionEffectType.SPEED, 6 * 20, 2);
			case JUMP:
				return new PotionEffect(PotionEffectType.JUMP, 6 * 20, 6);
			case FIRE_RESISTANCE:
				return new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 25 * 20, 0);
			case REGENERATION:
				return new PotionEffect(PotionEffectType.REGENERATION, 6 * 20, 2);
			case RESISTANCE:
				return new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 6 * 20, 2);
			case STRENGTH:
				return new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 6 * 20, 1);
			default:
				return null;
			}
		}
		
		public PotionEffect getHoldableEffect() {
			switch(this) {
			case SPEED:
				return new PotionEffect(PotionEffectType.SPEED, 5 * 20, 1);
			case JUMP:
				return new PotionEffect(PotionEffectType.JUMP, 5 * 20, 1);
			case FIRE_RESISTANCE:
				return new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 10 * 20, 0);
			case REGENERATION:
				return new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 0);
			case RESISTANCE:
				return new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5 * 20, 0);
			case STRENGTH:
				return new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 5 * 20, 0);
			default:
				return null;
			}
		}
		
		public static BardEffectType getByItem(ItemStack item) {
			if(item == null) return null;
			switch(item.getType()) {
			case SUGAR:
				return BardEffectType.SPEED;
			case FEATHER:
				return BardEffectType.JUMP;
			case MAGMA_CREAM:
				return BardEffectType.FIRE_RESISTANCE;
			case GHAST_TEAR:
				return BardEffectType.REGENERATION;
			case IRON_INGOT:
				return BardEffectType.RESISTANCE;
			case BLAZE_POWDER:
				return BardEffectType.STRENGTH;
			default:
				return null;
			}
		}
	}
}
