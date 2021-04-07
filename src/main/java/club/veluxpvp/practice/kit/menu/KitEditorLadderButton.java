package club.veluxpvp.practice.kit.menu;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import club.veluxpvp.practice.kit.KitType;
import club.veluxpvp.practice.menu.Button;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class KitEditorLadderButton extends Button {

	@Getter private KitType kitType;
	
	@Override
	public Material getMaterial() {
		return this.kitType == KitType.HCT_BARD ? Material.GOLD_CHESTPLATE : this.kitType == KitType.HCT_ROGUE ? Material.CHAINMAIL_CHESTPLATE : this.kitType == KitType.HCT_ARCHER ? Material.LEATHER_CHESTPLATE : this.kitType == KitType.HCT_DIAMOND_NO_DEBUFF || this.kitType == KitType.HCT_DIAMOND_DEBUFF ? Material.DIAMOND_CHESTPLATE : this.kitType.getLadder().getMaterial();
	}
	
	@Override
	public byte getDataValue() {
		return this.kitType.getLadder().getDataValue();
	}
	
	@Override
	public String getName() {
		return ChatColor.AQUA + this.kitType.name;
	}
	
	@Override
	public List<String> getLore() {
		return Arrays.asList(
				"&7* &fClick to edit your &b" + this.kitType.name + " &fkits!"
				);
	}
}
