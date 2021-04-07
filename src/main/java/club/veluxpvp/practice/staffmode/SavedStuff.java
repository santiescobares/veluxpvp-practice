package club.veluxpvp.practice.staffmode;

import java.util.Collection;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class SavedStuff {

	private ItemStack[] contents, armorContents;
	private Collection<PotionEffect> activeEffects;
	private GameMode gamemode;
	private double health, maxHealth;
	private int food, level;
	private float experience, exhaustion;
	private boolean flying;
	
	public SavedStuff(Player player) {
		this.contents = player.getInventory().getContents();
		this.armorContents = player.getInventory().getArmorContents();
		this.activeEffects = player.getActivePotionEffects();
		this.gamemode = player.getGameMode();
		this.health = player.getHealth();
		this.maxHealth = player.getMaxHealth();
		this.food = player.getFoodLevel();
		this.level = player.getLevel();
		this.experience = player.getExp();
		this.exhaustion = player.getExhaustion();
		this.flying = player.isFlying();
	}
	
	public void applyBack(Player player) {
		player.getInventory().setContents(this.contents);
		player.getInventory().setArmorContents(this.armorContents);
		this.activeEffects.stream().forEach(e -> player.addPotionEffect(e, true));
		player.setGameMode(this.gamemode);
		player.setHealth(this.health);
		player.setMaxHealth(this.maxHealth);
		player.setFoodLevel(this.food);
		player.setLevel(this.level);
		player.setExp(this.experience);
		player.setExhaustion(this.exhaustion);
		player.setAllowFlight(this.flying);
		player.setFlying(this.flying);
		player.updateInventory();
	}
}
