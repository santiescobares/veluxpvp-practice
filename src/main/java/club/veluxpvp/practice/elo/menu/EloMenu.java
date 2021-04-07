package club.veluxpvp.practice.elo.menu;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;

import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.menu.Button;
import club.veluxpvp.practice.menu.Menu;
import club.veluxpvp.practice.profile.Profile;

public class EloMenu extends Menu {

	private Profile targetProfile;
	
	public EloMenu(Player viewer, Profile targetProfile) {
		super(viewer);
		this.targetProfile = targetProfile;
	}

	@Override
	public String getTitle() {
		return Bukkit.getOfflinePlayer(this.targetProfile.getUuid()).getName() + "'s Statistics";
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
		Ladder[] rankedLadders = Ladder.values();
		
		buttons.put(4, new StatisticsButton(this.targetProfile));
		
		int pos = 19;
		for(int i = 0; i < rankedLadders.length; i++) {
			Ladder l = rankedLadders[i];
			
			if(l == Ladder.FINAL_UHC || l == Ladder.COMBO_FLY || l == Ladder.HCF || l == Ladder.HCT_NO_DEBUFF || l == Ladder.HCT_DEBUFF) continue;
			
			if(pos == 26) {
				buttons.put(30, new EloLadderButton(this.targetProfile, l));
				pos++;
			} else if(pos == 27) {
				buttons.put(31, new EloLadderButton(this.targetProfile, l));
				pos++;
			} else if(pos == 28) {
				buttons.put(32, new EloLadderButton(this.targetProfile, l));
				pos++;
			} else {
				buttons.put(pos++, new EloLadderButton(this.targetProfile, l));
			}
		}
		
		return buttons;
	}
}
