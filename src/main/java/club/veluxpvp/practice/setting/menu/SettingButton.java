package club.veluxpvp.practice.setting.menu;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import com.google.common.collect.Lists;

import club.veluxpvp.practice.menu.Button;
import club.veluxpvp.practice.setting.SettingType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class SettingButton extends Button {

	@Getter private SettingType type;
	private boolean enabled;
	
	@Override
	public Material getMaterial() {
		return this.type.getMaterial();
	}
	
	@Override
	public String getName() {
		return ChatColor.AQUA + this.type.displayName;
	}
	
	@Override
	public List<String> getLore() {
		List<String> lore = Lists.newArrayList();
		
		switch(this.type) {
		case SCOREBOARD:
			lore.add("&7Would you like to show the scoreboard");
			lore.add("&7in the right of your screen?");
			break;
		case ALLOW_SPECTATORS:
			lore.add("&7Would you like to allow that any");
			lore.add("&7people can spectate your matches?");
			break;
		case ALLOW_DUELS:
			lore.add("&7Would you like to allow that any");
			lore.add("&7people can send you a duel request?");
			break;
		case TOURNAMENT_MESSAGES:
			lore.add("&7Would you like to show join and");
			lore.add("&7elimination tournament messages?");
			break;
		case PING_ON_SCOREBOARD:
			lore.add("&7Would you like to show your and");
			lore.add("&7opponent's ping in the scoreboard?");
			break;
		case RANKED_SIMILAR_PING:
			lore.add("&7Would you like to look for people");
			lore.add("&7with a similar ping to you in the");
			lore.add("&7ranked matchmaking? &b(Exclusive)");
			break;
		}
		
		lore.add(" ");
		lore.add("&7* " + (enabled ? "&aYes" : "&cNo"));
		
		return lore;
	}
}
