package club.veluxpvp.practice.match;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import com.google.common.collect.Lists;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.utilities.ItemBuilder;
import club.veluxpvp.practice.utilities.PlayerUtil;
import club.veluxpvp.practice.utilities.TimeUtil;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PostMatchPlayer {

	private UUID playerUUID;
	private ItemStack[] contents, armorContents;
	private HealingType healingType;
	private double health;
	private int food, healingLeft;
	private int thrownPots, missedPots;
	private Collection<PotionEffect> activeEffects;
	
	public PostMatchPlayer(Player player, HealingType healingType, int thrownPots, int missedPots) {
		this.playerUUID = player.getUniqueId();
		this.contents = player.getInventory().getContents();
		this.armorContents = player.getInventory().getArmorContents();
		this.healingType = healingType;
		this.health = player.getHealth();
		this.food = player.getFoodLevel();
		this.thrownPots = thrownPots;
		this.missedPots = missedPots;
		this.healingLeft = PlayerUtil.getHealingLeft(contents, healingType);
		this.activeEffects = player.getActivePotionEffects();
	}
	
	public Inventory getInventory(boolean swapInventories) {
		Inventory inventory = Bukkit.createInventory(null, 54, Bukkit.getOfflinePlayer(this.playerUUID).getName() + "'s Inventory");
		
		inventory.setContents(this.contents);
		
		for(int i = 0; i < this.armorContents.length; i++) {
			if(armorContents[i] == null) continue;
			
			if(armorContents[i].getType().name().contains("HELMET")) {
				inventory.setItem(36, armorContents[i]);
			}
			
			if(armorContents[i].getType().name().contains("CHESTPLATE")) {
				inventory.setItem(37, armorContents[i]);
			}
			
			if(armorContents[i].getType().name().contains("LEGGINGS")) {
				inventory.setItem(38, armorContents[i]);
			}
			
			if(armorContents[i].getType().name().contains("BOOTS")) {
				inventory.setItem(39, armorContents[i]);
			}
		}

		ItemStack health = new ItemBuilder().of(Material.SPECKLED_MELON).amount((int) this.health).name("&bHealth: &a" + (int) this.health + "/20").build();
		ItemStack food = new ItemBuilder().of(Material.COOKED_BEEF).amount(this.food).name("&bFood: &a" + this.food + "/20").build();
		
		List<String> effectsLore = new ArrayList<>();
		for(PotionEffect e : this.activeEffects) {
			String effect = "";
			
			if(e.getType().getName().contains("SPEED")) effect = "&fSpeed";
			if(e.getType().getName().contains("FIRE_RESISTANCE")) effect = "&fFire Resistance";
			if(e.getType().getName().contains("REGENERATION")) effect = "&fRegeneration";
			if(e.getType().getName().contains("POISON")) effect = "&fPoison";
			if(e.getType().getName().contains("SLOW")) effect = "&fSlowness";
			if(e.getType().getName().contains("DAMAGE_RESISTANCE")) effect = "&fResistance";
			if(e.getType().getName().contains("INCREASE_DAMAGE")) effect = "&fStrength";
			
			if(e.getAmplifier() > 0) {
				effect += " " + (e.getAmplifier() + 1);
			}
			
			effect += "&7: &b" + TimeUtil.getFormattedDuration((e.getDuration() / 20), true);
			effectsLore.add(effect);
		}
		
		ItemStack effects = new ItemBuilder().of(Material.POTION).name("&bPotion Effects").lore(effectsLore).build();
		ItemStack healingLeft = this.healingType.getItem();
		
		inventory.setItem(48, health);
		inventory.setItem(49, food);
		inventory.setItem(50, effects);
		if(healingLeft != null) {
			String healingItem = this.healingType == HealingType.HEALTH_POTION ? "Potions" : this.healingType == HealingType.GOLDEN_APPLE ? "Golden Apples" : this.healingType == HealingType.GAPPLE ? "GApples" : this.healingType == HealingType.SOUP ? "Soups" : "";
			
			healingLeft.setAmount(this.healingLeft);
			ItemMeta meta = healingLeft.getItemMeta();
			meta.setDisplayName(ChatColor.AQUA + "" + this.healingLeft + " " + healingItem);
			
			List<String> lore = Lists.newArrayList();
			
			lore.add(ChatColor.GRAY + "* " + ChatColor.WHITE + Bukkit.getOfflinePlayer(this.playerUUID).getName() + " had " + ChatColor.AQUA + this.healingLeft + ChatColor.WHITE + " " + healingItem.toLowerCase() + " lefts.");
			if(healingType == HealingType.HEALTH_POTION) {
				lore.add(ChatColor.GRAY + "* " + ChatColor.WHITE + Bukkit.getOfflinePlayer(this.playerUUID).getName() + " missed " + ChatColor.AQUA + this.missedPots + ChatColor.WHITE + " potions.");
			}
			
			meta.setLore(lore);
			healingLeft.setItemMeta(meta);
			inventory.setItem(51, healingLeft);
		}
		
		if(swapInventories) {
			String otherPlayerName = Bukkit.getOfflinePlayer(Practice.getInstance().getMatchManager().getLastMatch(Bukkit.getOfflinePlayer(this.playerUUID)).getSwapInventoriesMap().get(this.playerUUID)).getName();
			ItemStack swapInventory = new ItemBuilder().of(Material.LEVER).name("&b" + otherPlayerName + "'s Inventory").lore(Arrays.asList("&7* &fClick to swap to " + otherPlayerName + "'s inventory!")).build();
		
			inventory.setItem(45, swapInventory);
			inventory.setItem(53, swapInventory);
		}
		
		return inventory;
	}
}
