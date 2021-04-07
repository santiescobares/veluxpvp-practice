package club.veluxpvp.practice.kit.menu;

import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;

import com.google.common.collect.Maps;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.kit.Kit;
import club.veluxpvp.practice.kit.KitType;
import club.veluxpvp.practice.menu.Button;
import club.veluxpvp.practice.menu.Menu;
import lombok.Getter;

public class EditKitsMenu extends Menu {

	private Player player;
	@Getter private KitType kitType;
	
	public EditKitsMenu(Player viewer, KitType kitType) {
		super(viewer);
		this.player = viewer;
		this.kitType = kitType;
	}

	@Override
	public String getTitle() {
		return "Your " + kitType.name + " Kits";
	}
	
	@Override
	public int getSize() {
		return 36;
	}
	
	@Override
	public boolean isFillEmptySpaces() {
		return true;
	}
	
	@Override
	public Map<Integer, Button> getButtons() {
		Map<Integer, Button> buttons = Maps.newConcurrentMap();
		Set<Kit> kits = Practice.getInstance().getKitManager().getKitsOfType(this.player, this.kitType);
		
		for(Kit k : kits) {
			buttons.put(k.getSlotInMenu(), new RenameKitButton());
			buttons.put(k.getSlotInMenu() + 9, new SaveKitButton());
			buttons.put(k.getSlotInMenu() + 18, new DeleteKitButton());
		}
		
		buttons.putIfAbsent(2, new CreateKitButton());
		buttons.putIfAbsent(3, new CreateKitButton());
		buttons.putIfAbsent(4, new CreateKitButton());
		buttons.putIfAbsent(5, new CreateKitButton());
		buttons.putIfAbsent(6, new CreateKitButton());
		
		return buttons;
	}
}
