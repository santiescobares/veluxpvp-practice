package club.veluxpvp.practice.elo.menu;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import club.veluxpvp.practice.menu.Button;
import club.veluxpvp.practice.profile.Profile;
import club.veluxpvp.practice.utilities.ChatUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class StatisticsButton extends Button {

	@Getter private Profile targetProfile;
	
	@Override
	public Material getMaterial() {
		return Material.SKULL_ITEM;
	}
	
	@Override
	public byte getDataValue() {
		return (byte) 3;
	}
	
	@Override
	public String getSkullOwner() {
		return Bukkit.getOfflinePlayer(this.targetProfile.getUuid()).getName();
	}
	
	@Override
	public String getName() {
		return ChatColor.AQUA + Bukkit.getOfflinePlayer(this.targetProfile.getUuid()).getName() + "'s Statistics";
	}
	
	@Override
	public List<String> getLore() {
		return Arrays.asList(
				ChatUtil.INV_LINE(),
				"&bUnranked",
				"&7* &fWins&7: &b" + this.targetProfile.getUnrankedWins(),
				"&7* &fLoses&7: &b" + this.targetProfile.getUnrankedLoses(),
				"&7* &fWLR&7: &b" + this.targetProfile.getUnrankedWLR(),
				" ",
				"&bRanked",
				"&7* &fWins&7: &b" + this.targetProfile.getRankedWins(),
				"&7* &fLoses&7: &b" + this.targetProfile.getRankedLoses(),
				"&7* &fWLR&7: &b" + this.targetProfile.getRankedWLR(),
				" ",
				"&bGlobal Elo",
				"&7* &f" + this.targetProfile.getGlobalElo(),
				ChatUtil.INV_LINE()
				);
	}
}
