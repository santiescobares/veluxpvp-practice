package club.veluxpvp.practice.tablist;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import club.veluxpvp.core.CoreAPI;
import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.party.Party;
import club.veluxpvp.practice.party.PartyMember;
import club.veluxpvp.practice.party.PartyRole;
import club.veluxpvp.practice.profile.Profile;
import club.veluxpvp.practice.tablist.provider.Skin;
import club.veluxpvp.practice.tablist.provider.TabEntry;
import club.veluxpvp.practice.utilities.PlayerUtil;

public final class LobbyTablist {
	
	public static final List<TabEntry> getDefaultLobbyLines(Player player) {
		List<TabEntry> lines = Lists.newArrayList();
		Profile p = Practice.getInstance().getProfileManager().getProfile(player);
		
		// Left
		lines.add(new TabEntry(0, 1, "&7Online: " + Bukkit.getOnlinePlayers().size()));
		
		// Middle
		lines.add(new TabEntry(1, 0, "&b&lVelux &7┃ &fPractice"));
		lines.add(new TabEntry(1, 1, "&7Your Connnection", PlayerUtil.getPing(player), Skin.getPlayer(player)));
		
		// Right
		lines.add(new TabEntry(2, 1, "&7Fighting: " + Practice.getInstance().getMatchManager().getFighting()));
		
		// Far Right
		lines.add(new TabEntry(3, 4, "&b&lYour Rankings"));
		lines.add(new TabEntry(3, 5, "No Debuff&7: &b" + p.getNoDebuffElo()));
		lines.add(new TabEntry(3, 6, "Debuff&7: &b" + p.getDebuffElo()));
		lines.add(new TabEntry(3, 7, "Build UHC&7: &b" + p.getBuildUHCElo()));
		lines.add(new TabEntry(3, 8, "HG&7: &b" + p.getHgElo()));
		lines.add(new TabEntry(3, 9, "GApple&7: &b" + p.getGappleElo()));
		lines.add(new TabEntry(3, 10, "Sumo&7: &b" + p.getSumoElo()));
		lines.add(new TabEntry(3, 11, "Soup&7: &b" + p.getSoupElo()));
		lines.add(new TabEntry(3, 12, "Archer&7: &b" + p.getArcherElo()));
		lines.add(new TabEntry(3, 13, "Parkour&7: &b" + p.getParkourElo()));
		lines.add(new TabEntry(3, 14, "Bridges&7: &b" + p.getBridgesElo()));
		
		// Players
		List<Player> onlinePlayers = Bukkit.getOnlinePlayers().stream()
				.sorted((p1, p2) -> {
					int priority1 = CoreAPI.getRankPriority(CoreAPI.getPlayerRank(p1.getUniqueId()));
					int priority2 = CoreAPI.getRankPriority(CoreAPI.getPlayerRank(p2.getUniqueId()));
					
					return priority2 - priority1;
				})
				.collect(Collectors.toList());
		
		int playerSize = 0;
		int column = 0;
		int raw = 3;
		
		for(Player pl : onlinePlayers) {
			playerSize++;
			if(playerSize > 48) break;
			
			lines.add(new TabEntry(column++, raw, CoreAPI.getColor(pl.getUniqueId()) + pl.getName(), PlayerUtil.getPing(pl), Skin.getPlayer(pl)));
			
			if(column == 3) {
				column = 0;
				raw++;
			}
		}
		
		return lines;
	}
	
	public static final List<TabEntry> getPartyLobbyLines(Player player, Party party) {
		List<TabEntry> lines = Lists.newArrayList();
		Profile p = Practice.getInstance().getProfileManager().getProfile(player);
		
		// Left
		lines.add(new TabEntry(0, 1, "&7Online: " + Bukkit.getOnlinePlayers().size()));
		
		// Middle
		lines.add(new TabEntry(1, 0, "&b&lVelux &7┃ &fPractice"));
		lines.add(new TabEntry(1, 1, "&7Your Connnection", PlayerUtil.getPing(player), Skin.getPlayer(player)));
		lines.add(new TabEntry(1, 3, "&b&lYour Party &7(" + party.getMembers().size() + "/" + party.getSlots() + ")"));
		
		// Right
		lines.add(new TabEntry(2, 1, "&7Fighting: " + Practice.getInstance().getMatchManager().getFighting()));
		
		// Far Right
		lines.add(new TabEntry(3, 4, "&b&lYour Rankings"));
		lines.add(new TabEntry(3, 5, "No Debuff&7: &b" + p.getNoDebuffElo()));
		lines.add(new TabEntry(3, 6, "Debuff&7: &b" + p.getDebuffElo()));
		lines.add(new TabEntry(3, 7, "Build UHC&7: &b" + p.getBuildUHCElo()));
		lines.add(new TabEntry(3, 8, "HG&7: &b" + p.getHgElo()));
		lines.add(new TabEntry(3, 9, "GApple&7: &b" + p.getGappleElo()));
		lines.add(new TabEntry(3, 10, "Sumo&7: &b" + p.getSumoElo()));
		lines.add(new TabEntry(3, 11, "Soup&7: &b" + p.getSoupElo()));
		lines.add(new TabEntry(3, 12, "Archer&7: &b" + p.getArcherElo()));
		lines.add(new TabEntry(3, 13, "Parkour&7: &b" + p.getParkourElo()));
		lines.add(new TabEntry(3, 14, "Bridges&7: &b" + p.getBridgesElo()));
		
		// Members
		int membersSize = 0;
		int column = 1;
		int raw = 4;
		
		Player leader = party.getLeader().getPlayer();
		lines.add(new TabEntry(0, 4, ChatColor.GREEN + leader.getName() + "*", PlayerUtil.getPing(leader), Skin.getPlayer(leader)));
		
		List<PartyMember> members = party.getMembers().stream().filter(m -> m.getRole() == PartyRole.MEMBER).collect(Collectors.toList());
		
		for(PartyMember pm : members) {
			Player pl = pm.getPlayer();
			
			membersSize++;
			if(membersSize > 45) break;
			
			lines.add(new TabEntry(column++, raw, ChatColor.GREEN + pl.getName(), PlayerUtil.getPing(pl), Skin.getPlayer(pl)));
			
			if(column == 3) {
				column = 0;
				raw++;
			}
		}
		
		return lines;
	}
}
