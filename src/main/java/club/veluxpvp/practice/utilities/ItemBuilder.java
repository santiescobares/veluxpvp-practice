package club.veluxpvp.practice.utilities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

public class ItemBuilder {

	private Material material;
	private int amount;
	private byte dataValue;
	private String name;
	private String skullOwner;
	private List<PotionEffect> potionEffects;
	private List<String> lore;
	
	public ItemBuilder() {
		this.material = null;
		this.amount = 1;
		this.dataValue = 0;
		this.name = null;
		this.skullOwner = null;
		this.potionEffects = new ArrayList<>();
		this.lore = new ArrayList<>();
	}
	
	public ItemBuilder of(Material material) {
		this.material = material;
		return this;
	}
	
	public ItemBuilder amount(int amount) {
		this.amount = amount;
		return this;
	}
	
	public ItemBuilder dataValue(byte dataValue) {
		this.dataValue = dataValue;
		return this;
	}
	
	public ItemBuilder name(String name) {
		this.name = name;
		return this;
	}
	
	public ItemBuilder skull(String skullOwner) {
		this.skullOwner = skullOwner;
		return this;
	}
	
	public ItemBuilder effect(PotionEffect effect) {
		this.potionEffects.add(effect);
		return this;
	}
	
	public ItemBuilder lore(List<String> lore) {
		this.lore = lore;
		return this;
	}
	
	public ItemStack build() {
		ItemStack item = new ItemStack(this.material, this.amount, this.dataValue);
		
		if(this.skullOwner != null) {
			SkullMeta meta = (SkullMeta) item.getItemMeta();
			
			meta.setOwner(this.skullOwner);
			
			if(this.name != null) {
				meta.setDisplayName(ChatUtil.TRANSLATE(this.name));
			}
			
			if(this.lore.size() > 0) {
				List<String> lore = new ArrayList<>();
				
				for(String l : this.lore) {
					lore.add(ChatUtil.TRANSLATE(l));
				}
				
				meta.setLore(lore);
			}

			item.setItemMeta(meta);
		} else if(this.potionEffects.size() > 0) {
			PotionMeta meta = (PotionMeta) item.getItemMeta();
			
			for(PotionEffect e : this.potionEffects) {
				meta.addCustomEffect(e, true);
			}
			
			if(this.name != null) {
				meta.setDisplayName(ChatUtil.TRANSLATE(this.name));
			}
			
			if(this.lore.size() > 0) {
				List<String> lore = new ArrayList<>();
				
				for(String l : this.lore) {
					lore.add(ChatUtil.TRANSLATE(l));
				}
				
				meta.setLore(lore);
			}

			item.setItemMeta(meta);
		} else {
			ItemMeta meta = item.getItemMeta();
			
			if(this.name != null) {
				meta.setDisplayName(ChatUtil.TRANSLATE(this.name));
			}
			
			if(this.lore.size() > 0) {
				List<String> lore = new ArrayList<>();
				
				for(String l : this.lore) {
					lore.add(ChatUtil.TRANSLATE(l));
				}
				
				meta.setLore(lore);
			}

			item.setItemMeta(meta);
		}

		return item;
	}
}
