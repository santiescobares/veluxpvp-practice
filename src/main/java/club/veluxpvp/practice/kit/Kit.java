package club.veluxpvp.practice.kit;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import club.veluxpvp.practice.utilities.ChatUtil;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Kit {

	private UUID ownerUUID;
	private String id, displayName;
	private KitType type;
	private int number;
	private boolean Default;
	private ItemStack[] contents, armorContents;
	
	public Kit(UUID ownerUUID, KitType type, boolean Default, int number) {
		this.ownerUUID = ownerUUID;
		this.id = UUID.randomUUID().toString().substring(0, 10);
		this.type = type;
		this.Default = Default;
		this.displayName = Default ? "&bDefault Kit" : "&bKit #" + number;
		this.number = number;
		this.contents = new ItemStack[0];
		this.armorContents = new ItemStack[0];
	}
	
	public void apply(Player player, boolean silent) {
		if(this.contents != null) player.getInventory().setContents(this.contents);
		if(this.armorContents != null) player.getInventory().setArmorContents(this.armorContents);
		player.updateInventory();
		
		if(silent) return;
		
		if(!this.Default) {
			player.sendMessage(ChatUtil.TRANSLATE("&fYou have equipped your \"&b" + this.displayName + "&r\" " + this.type.getLadder().name + " Kit!"));
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&fYou have equipped your &bDefault &f" + this.type.getLadder().name + " Kit!"));
		}
	}
	
	public int getSlotInMenu() {
		return number == 1 ? 2 : number == 2 ? 3 : number == 3 ? 4 : number == 4 ? 5 : 6;
	}
	
	public int getSlotInHotbar() {
		return Default ? 0 : (number == 1 ? 2 : number == 2 ? 3 : number == 3 ? 4 : number == 4 ? 5 : 6);
	}
}
