package club.veluxpvp.practice.match.menu;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.menu.Button;
import club.veluxpvp.practice.utilities.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class SpectateMatchButton extends Button {

	@Getter private Match match;
	
	@Override
	public Material getMaterial() {
		return this.match.getLadder().getMaterial();
	}
	
	@Override
	public byte getDataValue() {
		return this.match.getLadder().getDataValue();
	}
	
	@Override
	public String getName() {
		return this.match != null ? ChatColor.AQUA + (this.match.isParty() ? this.match.getTeam1().getFIRST_TOTAL_MEMBERS() + " vs " + this.match.getTeam2().getFIRST_TOTAL_MEMBERS() : this.match.isFfa() ? "Free For All" : this.match.getTeam1().getFirstPlayer().getName() + " vs " + this.match.getTeam2().getFirstPlayer().getName()) : "";
	}
	
	@Override
	public List<String> getLore() {
		List<String> lore = Lists.newArrayList();
		
		lore.add("&7* &fLadder&7: &b" + this.match.getLadder().name);
		lore.add("&7* &fDuration&7: &b" + TimeUtil.getFormattedDuration(this.match.getDuration(), true));
		lore.add("&7* &fAlive Players&7: &b" + this.match.getAlivePlayers().size() + "/" + this.match.getPlayersWhoPlayedCache().size());
		lore.add(" ");
		lore.add("&bPlayers");
		
		if(!this.match.isFfa()) {
			for(Player p : this.match.getTeam1().getPlayers()) {
				lore.add("&7- &c" + p.getName());
			}
			
			lore.add("&7vs");
			
			for(Player p : this.match.getTeam2().getPlayers()) {
				lore.add("&7- &9" + p.getName());
			}
		}
		
		lore.add(" ");
		lore.add("&7* &aClick to spectate!");
		
		return lore;
	}
}
