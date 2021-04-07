package club.veluxpvp.practice.match;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import club.veluxpvp.practice.tablist.provider.Skin;
import club.veluxpvp.practice.tablist.provider.TabEntry;
import club.veluxpvp.practice.utilities.PlayerUtil;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MatchTeam {

	private Match match;
	private TeamType type;
	private List<Player> players;
	private List<String> names;
	
	private int FIRST_TOTAL_MEMBERS = 0;
	
	public MatchTeam(Match match, TeamType type) {
		this.match = match;
		this.type = type;
		this.players = Lists.newArrayList();
		this.names = Lists.newArrayList();
	}
	
	public void addPlayer(Player player) {
		if(this.players.contains(player)) return;
		
		this.players.add(player);
		this.names.add(player.getName());
	}
	
	public void clear() {
		this.players.clear();
		this.names.clear();
	}
	
	public Player getFirstPlayer() {
		if(players.size() > 0) return players.get(0);	
		return null;
	}
	
	@SuppressWarnings("deprecation")
	public Set<OfflinePlayer> namesToOfflinePlayers() {
		Set<OfflinePlayer> players = Sets.newHashSet();
		
		for(String name : this.names) {
			OfflinePlayer p = Bukkit.getOfflinePlayer(name);
			players.add(p);
		}
		
		return players;
	}
	
	public void renderScoreboardNames(List<String> lines, Player player, Match match) {
		Set<OfflinePlayer> members = this.namesToOfflinePlayers();
		
		for(OfflinePlayer offP : members.stream().filter(m -> m.isOnline() && match.getAlivePlayers().contains((Player) m)).collect(Collectors.toSet())) {
			Player p = (Player) offP;
			if(p == player) continue;
			
			lines.add("&7* &f" + p.getName() + " &7(&b" + PlayerUtil.getHealth(p) + " ❤&7)");
		}
		
		for(OfflinePlayer offP : members.stream().filter(m -> !m.isOnline() || !match.getAlivePlayers().contains((Player) m)).collect(Collectors.toSet())) {
			lines.add("&7&m" + offP.getName());
		}
	}
	
	public void renderTablistNames(List<TabEntry> lines, Player player, Match match, boolean enemy, boolean spectatorPov) {
		Set<OfflinePlayer> members = this.namesToOfflinePlayers();
		ChatColor color = !spectatorPov ? (enemy ? ChatColor.RED : ChatColor.GREEN) : (enemy ? ChatColor.BLUE : ChatColor.RED);
		int column = enemy ? 2 : 0;
		int index = 4;
		boolean Continue = true;
		
		for(OfflinePlayer offP : members.stream().filter(m -> m.isOnline() && match.getAlivePlayers().contains((Player) m)).collect(Collectors.toSet())) {
			Player p = (Player) offP;
			
			lines.add(new TabEntry(column, index++, color + p.getName(), PlayerUtil.getPing(p), Skin.getPlayer(p)));
			
			if(index >= 19) {
				int membersLeft = this.players.size() - 19;
				if(membersLeft > 0) {
					lines.add(new TabEntry(2, index, color + "+" + membersLeft + " more"));
					Continue = false;
					break;
				}
			}
		}
		
		if(Continue) {
			for(OfflinePlayer offP : members.stream().filter(m -> !m.isOnline() || !match.getAlivePlayers().contains((Player) m)).collect(Collectors.toSet())) {
				lines.add(new TabEntry(column, index++, "&7&m" + offP.getName()));
				
				if(index >= 19) {
					int membersLeft = this.players.size() - 19;
					if(membersLeft > 0) {
						lines.add(new TabEntry(2, index, color + "+" + membersLeft + " more"));
						Continue = false;
						break;
					}
				}
			}
		}
	}
}
