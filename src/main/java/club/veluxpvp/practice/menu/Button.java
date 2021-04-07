package club.veluxpvp.practice.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

import club.veluxpvp.practice.utilities.ChatUtil;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Button {

	private ItemStack item;
	private Material material;
	private int amount;
	private byte dataValue;
	private String name, skullOwner;
	private List<String> lore;
	private List<PotionEffect> potionEffects;
	
	public Button() {
		this.item = null;
		this.material = null;
		this.amount = 1;
		this.dataValue = 0;
		this.name = null;
		this.skullOwner = null;
		this.lore = new ArrayList<>();
		this.potionEffects = new ArrayList<>();
	}
	
	public ItemStack build() {
		if(getItem() != null) return getItem(); 
		
		ItemStack item = new ItemStack(getMaterial(), getAmount(), getDataValue());
		
		if(getSkullOwner() != null) {
			SkullMeta meta = (SkullMeta) item.getItemMeta();
			
			meta.setOwner(getSkullOwner());
			
			if(getName() != null) {
				meta.setDisplayName(ChatUtil.TRANSLATE(getName()));
			}
			
			if(getLore().size() > 0) {
				List<String> lore = new ArrayList<>();
				
				for(String l : getLore()) {
					lore.add(ChatUtil.TRANSLATE(l));
				}
				
				meta.setLore(lore);
			}

			item.setItemMeta(meta);
		} else if(getPotionEffects().size() > 0) {
			PotionMeta meta = (PotionMeta) item.getItemMeta();
			
			for(PotionEffect e : getPotionEffects()) {
				meta.addCustomEffect(e, true);
			}
			
			if(getName() != null) {
				meta.setDisplayName(ChatUtil.TRANSLATE(getName()));
			}
			
			if(getLore().size() > 0) {
				List<String> lore = new ArrayList<>();
				
				for(String l : getLore()) {
					lore.add(ChatUtil.TRANSLATE(l));
				}
				
				meta.setLore(lore);
			}

			item.setItemMeta(meta);
		} else {
			ItemMeta meta = item.getItemMeta();
			
			if(getName() != null) {
				meta.setDisplayName(ChatUtil.TRANSLATE(getName()));
			}
			
			if(getLore().size() > 0) {
				List<String> lore = new ArrayList<>();
				
				for(String l : getLore()) {
					lore.add(ChatUtil.TRANSLATE(l));
				}
				
				meta.setLore(lore);
			}

			item.setItemMeta(meta);
			
		}

		return item;
	}
}
