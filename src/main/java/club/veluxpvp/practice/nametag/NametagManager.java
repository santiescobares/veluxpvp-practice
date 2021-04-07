package club.veluxpvp.practice.nametag;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.nametagedit.plugin.NametagEdit;

import club.veluxpvp.core.utilities.ChatUtil;
import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.match.MatchTeam;
import club.veluxpvp.practice.match.listener.MatchHCTListener;
import club.veluxpvp.practice.profile.Profile;

public class NametagManager {

	public void updateNametag(Player player) {
		Match match = Practice.getInstance().getMatchManager().getPlayerMatch(player);
		
		updateLunarNametag(player);
		
		if(match == null || match.isSpectating(player)) {
			NametagEdit.getApi().setPrefix(player, ChatColor.GRAY.toString());
			return;
		}
		
		if(MatchHCTListener.isArcherMarked(player)) {
			NametagEdit.getApi().setPrefix(player, ChatColor.YELLOW.toString());
			return;
		}
		
		if(match.isParty() || match.getLadder() == Ladder.BRIDGES) {
			MatchTeam playerTeam = match.getPlayerTeam(player);
			
			if(playerTeam != null) {
				NametagEdit.getApi().setPrefix(player, playerTeam == match.getTeam1() ? ChatColor.RED.toString() : ChatColor.BLUE.toString());
				return;
			}
		} else {
			NametagEdit.getApi().setPrefix(player, ChatColor.RED.toString());
		}
	}
	
	private void updateLunarNametag(Player player) {
		Match match = Practice.getInstance().getMatchManager().getPlayerMatch(player);
		Profile p = Practice.getInstance().getProfileManager().getProfile(player);
		
		if(match == null || match.isSpectating(player)) {
			updateLunarToAll(player, Arrays.asList("&7" + player.getName()));
			return;
		}
		
		if(MatchHCTListener.isArcherMarked(player)) {
			if(!match.isRanked()) {
				updateLunarToAll(player, Arrays.asList("&e" + player.getName()));
			} else {
				updateLunarToAll(player, Arrays.asList("ELO&7: &b" + p.getElo(match.getLadder()), "&e" + player.getName()));
			}
			
			return;
		}
		
		if(match.isParty() || match.getLadder() == Ladder.BRIDGES) {
			MatchTeam playerTeam = match.getPlayerTeam(player);
			
			if(playerTeam != null) {
				ChatColor color = playerTeam == match.getTeam1() ? ChatColor.RED : ChatColor.BLUE;
				
				if(!match.isRanked()) {
					updateLunarToAll(player, Arrays.asList(color.toString()));
				} else {
					updateLunarToAll(player, Arrays.asList("ELO&7: &b" + p.getElo(match.getLadder()), color.toString()));
				}
			}
		} else {
			if(!match.isRanked()) {
				updateLunarToAll(player, Arrays.asList("&c" + player.getName()));
			} else {
				updateLunarToAll(player, Arrays.asList("ELO&7: &b" + p.getElo(match.getLadder()), "&c" + player.getName()));
			}
		}
	}
	
	private void updateLunarToAll(Player player, List<String> tags) {
		for(Player otherPlayer : Bukkit.getOnlinePlayers()) {
			LunarClientAPI.getInstance().overrideNametag(player, ChatUtil.TRANSLATE(tags), otherPlayer);
		}
	}
	
	/*
	public void updateNametag(Player player) {
		Match match = Practice.getInstance().getMatchManager().getPlayerMatch(player);

		((CraftPlayer) player).getHandle().playerConnection.sendPacket(this.getClearTagPacket(player));
		
		for(Player otherPlayer : Bukkit.getOnlinePlayers()) {
			Match otherMatch = Practice.getInstance().getMatchManager().getPlayerMatch(otherPlayer);

			if(otherPlayer == player) {
				((CraftPlayer) otherPlayer).getHandle().playerConnection.sendPacket(this.getTagPacket(player, TagColor.TEAMMATE));
				continue;
			}
			
			if(match == null || otherMatch == null || match != otherMatch) {
				((CraftPlayer) otherPlayer).getHandle().playerConnection.sendPacket(this.getTagPacket(player, TagColor.DEFAULT));
				continue;
			}
			
			if(match.getLadder() == Ladder.HCT_NO_DEBUFF || match.getLadder() == Ladder.HCT_DEBUFF) {
				if(MatchHCTListener.isArcherMarked(player)) {
					((CraftPlayer) otherPlayer).getHandle().playerConnection.sendPacket(this.getTagPacket(player, TagColor.ARCHER_MARKED));
					continue;
				}
			}
			
			if(!match.isSpectating(player) && match.isSpectating(otherPlayer) && match.getPlayerTeam(player) != null) {
				if(match.isFfa()) {
					((CraftPlayer) otherPlayer).getHandle().playerConnection.sendPacket(this.getTagPacket(player, TagColor.TEAM_1));
					continue;
				} else {
					((CraftPlayer) otherPlayer).getHandle().playerConnection.sendPacket(this.getTagPacket(player, (match.getPlayerTeam(player).getType() == TeamType.TEAM_1 ? TagColor.TEAM_1 : TagColor.TEAM_2)));
					continue;
				}
			}
			
			if(!match.isSpectating(player) && match.isSpectating(otherPlayer) && match.getPlayerTeam(otherPlayer) == null) {
				((CraftPlayer) otherPlayer).getHandle().playerConnection.sendPacket(this.getTagPacket(player, TagColor.DEFAULT));
				continue;
			}
			
			if(match.isFfa()) {
				((CraftPlayer) otherPlayer).getHandle().playerConnection.sendPacket(this.getTagPacket(player, TagColor.ENEMY));
				continue;
			}
			
			if(match.getPlayerTeam(player) == match.getPlayerTeam(otherPlayer)) {
				((CraftPlayer) otherPlayer).getHandle().playerConnection.sendPacket(this.getTagPacket(player, TagColor.TEAMMATE));
				continue;
			} else {
				((CraftPlayer) otherPlayer).getHandle().playerConnection.sendPacket(this.getTagPacket(player, TagColor.ENEMY));
				continue;
			}
		}
	}
	
	public void updateHealthDisplay(Player player) {
		Match match = Practice.getInstance().getMatchManager().getPlayerMatch(player);
		
		if(match != null && match.getLadder() == Ladder.BUILD_UHC) {
			if(match.isSpectating(player)) {
				Objective obj = player.getScoreboard().getObjective("HealthDisplay");
				if(obj != null) obj.unregister();
				return;
			}
			
			Objective obj = player.getScoreboard().getObjective("HealthDisplay");
			if(obj != null) {
				obj.setDisplayName(ChatUtil.TRANSLATE("&f" + PlayerUtil.getHealth(player) + " &c&l♥"));
			} else {
				obj = player.getScoreboard().registerNewObjective("HealthDisplay", "dummy");
				obj.setDisplaySlot(DisplaySlot.BELOW_NAME);
				obj.setDisplayName(ChatUtil.TRANSLATE("&f" + PlayerUtil.getHealth(player) + " &c&l♥"));
			}
		} else {
			Objective obj = player.getScoreboard().getObjective("HealthDisplay");
			if(obj != null) obj.unregister();
			return;
		}
	}
	
	public PacketPlayOutScoreboardTeam getTagPacket(Player player, TagColor color) {
		PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
		
		try {
			Field scoreField = packet.getClass().getDeclaredField("a");
			scoreField.setAccessible(true);
			scoreField.set(packet, player.getUniqueId().toString().substring(0, 16));
			scoreField.setAccessible(false);
			
			Field visibilityField = packet.getClass().getDeclaredField("e");
			visibilityField.setAccessible(true);
			visibilityField.set(packet, ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS.e);
			visibilityField.setAccessible(false);
			
			Field playersField = packet.getClass().getDeclaredField("g");
			playersField.setAccessible(true);
			playersField.set(packet, Arrays.asList(new String[] { player.getName() }));
			playersField.setAccessible(false);
			
			Field prefixField = packet.getClass().getDeclaredField("c");
			prefixField.setAccessible(true);
			prefixField.set(packet, color.getColor() + "");
			prefixField.setAccessible(false);
			
			Field paramIntField = packet.getClass().getDeclaredField("h");
			paramIntField.setAccessible(true);
			paramIntField.set(packet, 0);
			paramIntField.setAccessible(false);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return packet;
	}
	
	public PacketPlayOutScoreboardTeam getClearTagPacket(Player player) {
		PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
		
		try {
			Field scoreField = packet.getClass().getDeclaredField("a");
			scoreField.setAccessible(true);
			scoreField.set(packet, player.getUniqueId().toString().substring(0, 16));
			scoreField.setAccessible(false);
			
			Field visibilityField = packet.getClass().getDeclaredField("e");
			visibilityField.setAccessible(true);
			visibilityField.set(packet, ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS.e);
			visibilityField.setAccessible(false);
			
			Field playersField = packet.getClass().getDeclaredField("g");
			playersField.setAccessible(true);
			playersField.set(packet, Arrays.asList(new String[] {}));
			playersField.setAccessible(false);
			
			Field prefixField = packet.getClass().getDeclaredField("c");
			prefixField.setAccessible(true);
			prefixField.set(packet, "");
			prefixField.setAccessible(false);
			
			Field paramIntField = packet.getClass().getDeclaredField("h");
			paramIntField.setAccessible(true);
			paramIntField.set(packet, 1);
			paramIntField.setAccessible(false);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return packet;
	}
	
	public enum TagColor {
		DEFAULT,
		TEAMMATE,
		ENEMY,
		ARCHER_MARKED,
		TEAM_1,
		TEAM_2;
		
		public ChatColor getColor() {
			switch(this) {
			case DEFAULT:
				return ChatColor.GRAY;
			case TEAMMATE:
				return ChatColor.GREEN;
			case ENEMY:
				return ChatColor.RED;
			case ARCHER_MARKED:
				return ChatColor.YELLOW;
			case TEAM_1:
				return ChatColor.RED;
			default:
				return ChatColor.BLUE;
			}
		}
	}
	*/
}
