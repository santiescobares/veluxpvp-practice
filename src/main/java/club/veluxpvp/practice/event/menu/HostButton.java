package club.veluxpvp.practice.event.menu;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import com.google.common.collect.Lists;

import club.veluxpvp.practice.event.EventType;
import club.veluxpvp.practice.menu.Button;
import lombok.Getter;
import lombok.Setter;

public class HostButton extends Button {

	@Getter private EventType eventType;
	@Getter @Setter private boolean twoVStwo;
	
	public HostButton(EventType eventType) {
		this.eventType = eventType;
		this.twoVStwo = false;
	}
	
	@Override
	public String getName() {
		return ChatColor.AQUA + this.eventType.name + " Event";
	}
	
	@Override
	public Material getMaterial() {
		switch(this.eventType) {
		case SUMO:
			return Material.LEASH;
		default:
			return Material.DIAMOND_SWORD;
		}
	}
	
	@Override
	public List<String> getLore() {
		switch(this.eventType) {
		case SUMO:
			return Arrays.asList(
					"&7Try to beat all your opponents on",
					"&7a platform for win! But, be carefully,",
					"&7if you fell into the water you lose.",
					" ",
					"&7* &fType&7: &b" + (this.twoVStwo ? "2vs2" : "1vs1") + " &7(Right click to change)",
					" ",
					"&7* &aClick to host!"
					);
		default:
			return Lists.newArrayList();
		}
	}
}
