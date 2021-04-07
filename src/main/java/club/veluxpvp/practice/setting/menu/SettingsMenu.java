package club.veluxpvp.practice.setting.menu;

import java.util.Map;

import org.bukkit.entity.Player;

import com.google.common.collect.Maps;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.menu.Button;
import club.veluxpvp.practice.menu.Menu;
import club.veluxpvp.practice.profile.Profile;
import club.veluxpvp.practice.setting.SettingType;

public class SettingsMenu extends Menu {

	private Player player;
	
	public SettingsMenu(Player viewer) {
		super(viewer);
		this.player = viewer;
	}

	@Override
	public String getTitle() {
		return "Your Settings";
	}
	
	@Override
	public Map<Integer, Button> getButtons() {
		Map<Integer, Button> buttons = Maps.newConcurrentMap();
		SettingType[] type = SettingType.values();
		Profile p = Practice.getInstance().getProfileManager().getProfile(this.player);
		
		int slot = 0;
		for(int i = 0; i < type.length; i++) {
			SettingType setting = type[i];
			boolean enabled = false;
			
			switch(setting) {
			case SCOREBOARD:
				enabled = p.isScoreboard();
				break;
			case ALLOW_SPECTATORS:
				enabled = p.isAllowSpectators();
				break;
			case ALLOW_DUELS:
				enabled = p.isAllowDuels();
				break;
			case TOURNAMENT_MESSAGES:
				enabled = p.isTournamentMessages();
				break;
			case PING_ON_SCOREBOARD:
				enabled = p.isPingOnScoreboard();
				break;
			case RANKED_SIMILAR_PING:
				enabled = p.isRankedSimilarPing();
				break;
			}
			
			buttons.put(slot++, new SettingButton(setting, enabled));
		}
		
		return buttons;
	}
}
