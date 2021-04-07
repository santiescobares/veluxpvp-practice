package club.veluxpvp.practice.party.menu;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import club.veluxpvp.practice.menu.Button;
import club.veluxpvp.practice.party.Party;
import club.veluxpvp.practice.party.PartyMember;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class OtherPartyButton extends Button {

	@Getter private Party party;
	
	@Override
	public Material getMaterial() {
		return Material.SKULL_ITEM;
	}
	
	@Override
	public byte getDataValue() {
		return (byte) 3;
	}
	
	@Override
	public String getName() {
		return ChatColor.AQUA + this.party.getLeader().getPlayer().getName() + "'s Party " + ChatColor.GRAY + "(" + this.party.getMembers().size() + "/" + this.party.getSlots() + ")";
	}
	
	@Override
	public List<String> getLore() {
		List<String> lore = Lists.newArrayList();
		
		for(Player p : this.party.getMembers().stream().map(PartyMember::getPlayer).collect(Collectors.toList())) {
			if(p == this.party.getLeader()) continue;
			
			lore.add("&7* &f" + p.getName());
		}
		
		lore.add(" ");
		lore.add("&7* &aClick to duel!");
		
		return lore;
	}
}
