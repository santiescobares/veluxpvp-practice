package club.veluxpvp.practice.party.menu;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import com.google.common.collect.Lists;

import club.veluxpvp.practice.menu.Button;
import club.veluxpvp.practice.party.PartyMember;
import club.veluxpvp.practice.party.pvpclass.HCFClassType;
import lombok.Getter;

public class HCFRosterButton extends Button {

	@Getter private PartyMember clickedMember;
	private HCFClassType currentClass;
	
	public HCFRosterButton(PartyMember clickedMember) {
		this.clickedMember = clickedMember;
		this.currentClass = clickedMember.getHcfClass();
	}
	
	@Override
	public Material getMaterial() {
		return this.currentClass == HCFClassType.BARD ? Material.GOLD_CHESTPLATE : this.currentClass == HCFClassType.ROGUE ? Material.CHAINMAIL_CHESTPLATE : this.currentClass == HCFClassType.ARCHER ? Material.LEATHER_CHESTPLATE : Material.DIAMOND_CHESTPLATE;
	}
	
	@Override
	public String getName() {
		return ChatColor.AQUA + this.clickedMember.getPlayer().getName();
	}
	
	@Override
	public List<String> getLore() {
		List<String> lore = Lists.newArrayList();
		
		lore.add((this.currentClass == HCFClassType.DIAMOND ? "&aDiamond" : "&7Diamond"));
		lore.add((this.currentClass == HCFClassType.BARD ? "&aBard" : "&7Bard"));
		lore.add((this.currentClass == HCFClassType.ROGUE ? "&aRogue" : "&7Rogue"));
		lore.add((this.currentClass == HCFClassType.ARCHER ? "&aArcher" : "&7Archer"));
		
		return lore;
	}
}
