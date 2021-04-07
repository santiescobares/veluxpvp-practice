package club.veluxpvp.practice.utilities;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.match.MatchState;
import club.veluxpvp.practice.party.Party;
import club.veluxpvp.practice.party.pvpclass.HCFClassType;
import club.veluxpvp.practice.profile.Profile;
import club.veluxpvp.practice.tournament.Tournament;

public class Preconditions {

	public static boolean canSetClass(Player target, HCFClassType Class) {
		Party party = Practice.getInstance().getPartyManager().getPlayerParty(target);
		int totalClass = party.getTotalByClass(Class);
		
		int allowedClassAmount = 0;
		for(int i = 0; i < party.getMembers().size(); i += 5) {
			allowedClassAmount++;
		}
		
		if(totalClass >= allowedClassAmount) return false;
		
		return true;
	}
	
	public static boolean canCreateParty(Player player) {
		if(Practice.getInstance().getPartyManager().getPlayerParty(player) != null) {
			player.sendMessage(ChatUtil.TRANSLATE("&cYou are already in a party!"));
			return false;
		}
		
		if(Practice.getInstance().getMatchManager().getPlayerMatch(player) != null || Practice.getInstance().getQueueManager().getPlayer(player) != null || Practice.getInstance().getKitManager().isEditingKit(player)) {
			player.sendMessage(ChatUtil.TRANSLATE("&cYou can't do this in your current state!"));
			return false;
		}
		
		if(player.hasMetadata("StaffMode") || player.hasMetadata("Vanished")) {
			player.sendMessage(ChatUtil.TRANSLATE("&cYou can't join a party while in Staff Mode/Vanished!"));
			return false;
		}
		
		return true;
	}
	
	public static boolean canJoinParty(Player player) {
		if(Practice.getInstance().getPartyManager().getPlayerParty(player) != null) {
			player.sendMessage(ChatUtil.TRANSLATE("&cYou are already in a party!"));
			return false;
		}
		
		if(Practice.getInstance().getMatchManager().getPlayerMatch(player) != null || Practice.getInstance().getQueueManager().getPlayer(player) != null || Practice.getInstance().getKitManager().isEditingKit(player)) {
			player.sendMessage(ChatUtil.TRANSLATE("&cYou can't do this in your current state!"));
			return false;
		}
		
		if(player.hasMetadata("StaffMode") || player.hasMetadata("Vanished")) {
			player.sendMessage(ChatUtil.TRANSLATE("&cYou can't join a party while in Staff Mode/Vanished!"));
			return false;
		}
		
		return true;
	}
	
	public static boolean canJoinTournament(Player player) {
		if(Practice.getInstance().getTournamentManager().getActiveTournament().isInTournament(player)) {
			player.sendMessage(ChatUtil.TRANSLATE("&cYou are already in a tournament!"));
			return false;
		}
		
		if(Practice.getInstance().getMatchManager().getPlayerMatch(player) != null || Practice.getInstance().getQueueManager().getPlayer(player) != null || Practice.getInstance().getKitManager().isEditingKit(player)) {
			player.sendMessage(ChatUtil.TRANSLATE("&cYou can't do this in your current state!"));
			return false;
		}
		
		if(player.hasMetadata("StaffMode") || player.hasMetadata("Vanished")) {
			player.sendMessage(ChatUtil.TRANSLATE("&cYou can't join a tournament while in Staff Mode/Vanished!"));
			return false;
		}
		
		Party party = Practice.getInstance().getPartyManager().getPlayerParty(player);
		int teamSize = Practice.getInstance().getTournamentManager().getActiveTournament().getTeamSize();
		
		if(teamSize > 1) {
			if(party == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cYou can't join the tournament without a party!"));
				return false;
			}
			
			if(party.getMembers().size() != teamSize) {
				player.sendMessage(ChatUtil.TRANSLATE("&cYour party must have " + teamSize + " members to join the tournament!"));
				return false;
			}
		} else {
			if(party != null && party.getMembers().size() != teamSize) {
				player.sendMessage(ChatUtil.TRANSLATE("&cYour party must have " + teamSize + " members to join the tournament!"));
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean canSpectate(Player player, Player target) {
		Match targetMatch = Practice.getInstance().getMatchManager().getPlayerMatch(target);
		
		if(Practice.getInstance().getMatchManager().getPlayerMatch(player) != null || Practice.getInstance().getQueueManager().getPlayer(player) != null || Practice.getInstance().getKitManager().isEditingKit(player)) {
			player.sendMessage(ChatUtil.TRANSLATE("&cYou can't do this in your current state!"));
			return false;
		}
		
		if(player.hasMetadata("StaffMode") || player.hasMetadata("Vanished")) {
			player.sendMessage(ChatUtil.TRANSLATE("&cYou can't spectate a match while in Staff Mode/Vanished!"));
			return false;
		}
		
		if(targetMatch == null) {
			player.sendMessage(ChatUtil.TRANSLATE("&c" + target.getName() + " is not in a match!"));
			return false;
		}
		
		if(targetMatch.getState() == MatchState.ENDING) {
			player.sendMessage(ChatUtil.TRANSLATE("&cThe match is no longer avaiable for spectate!"));
			return false;
		}
		
		Party party = Practice.getInstance().getPartyManager().getPlayerParty(player);
		Party targetParty = Practice.getInstance().getPartyManager().getPlayerParty(target);
		
		if(party != null) {
			Tournament tour = Practice.getInstance().getTournamentManager().getActiveTournament();
			
			if(tour != null && tour.isInTournament(party)) {
				if(targetParty == null || !tour.isInTournament(targetParty)) {
					player.sendMessage(ChatUtil.TRANSLATE("&cYou can't spectate matches that are not in your tournament!"));
					return false;
				}
			} else {
				player.sendMessage(ChatUtil.TRANSLATE("&cYou can't spectate a match while in party!"));
				return false;
			}
		}
		
		int playersAllowingSpecs = 0;
		for(UUID uuid : targetMatch.getPlayersWhoPlayedCache()) {
			Profile p = Practice.getInstance().getProfileManager().getProfile(Bukkit.getOfflinePlayer(uuid));
			
			if(p.isAllowSpectators()) playersAllowingSpecs++;
		}
		
		if(playersAllowingSpecs >= (targetMatch.getPlayersWhoPlayedCache().size() / 2)) {
			return true;
		}
		
		player.sendMessage(ChatUtil.TRANSLATE("&cThis match cannot be spectated due to many people have their spectators disabled!"));
		return false;
	}
	
	public static boolean canSendDuel(Player player, Player target) {
		Match targetMatch = Practice.getInstance().getMatchManager().getPlayerMatch(target);
		Party party = Practice.getInstance().getPartyManager().getPlayerParty(player);
		Party targetParty = Practice.getInstance().getPartyManager().getPlayerParty(target);
		
		if(Practice.getInstance().getMatchManager().getPlayerMatch(player) != null || Practice.getInstance().getQueueManager().getPlayer(player) != null || Practice.getInstance().getKitManager().isEditingKit(player)) {
			player.sendMessage(ChatUtil.TRANSLATE("&cYou can't do this in your current state!"));
			return false;
		}
		
		if(targetMatch != null) {
			player.sendMessage(ChatUtil.TRANSLATE("&c" + target.getName() + " is not in the spawn!"));
			return false;
		}
		
		if(!Practice.getInstance().getProfileManager().getProfile(player).isAllowDuels()) {
			player.sendMessage(ChatUtil.TRANSLATE("&cYou can't duel another people while your duel requests are disabled!"));
			return false;
		}
		
		if(!Practice.getInstance().getProfileManager().getProfile(target).isAllowDuels()) {
			player.sendMessage(ChatUtil.TRANSLATE("&c" + target.getName() + " is not accepting duel requests!"));
			return false;
		}
		
		if(party != null) {
			player.sendMessage(ChatUtil.TRANSLATE("&cYou can't send a 1vs1 duel request while in party!"));
			return false;
		}
		
		if(targetParty != null) {
			player.sendMessage(ChatUtil.TRANSLATE("&c" + target.getName() + " is in a party!"));
			return false;
		}
		
		if(player.hasMetadata("StaffMode") || player.hasMetadata("Vanished")) {
			player.sendMessage(ChatUtil.TRANSLATE("&cYou can't send a duel request while in Staff Mode/Vanished!"));
			return false;
		}
		
		if(target.hasMetadata("StaffMode") || player.hasMetadata("Vanished")) {
			player.sendMessage(ChatUtil.TRANSLATE("&c" + target.getName() + " is not accepting duel requests!"));
			return false;
		}
		
		return true;
	}
	
	public static boolean canAcceptDuel(Player player, Player target) {
		Match match = Practice.getInstance().getMatchManager().getPlayerMatch(player);
		Match targetMatch = Practice.getInstance().getMatchManager().getPlayerMatch(target);
		Party party = Practice.getInstance().getPartyManager().getPlayerParty(player);
		Party targetParty = Practice.getInstance().getPartyManager().getPlayerParty(target);
		
		if(match != null || Practice.getInstance().getQueueManager().getPlayer(player) != null || Practice.getInstance().getKitManager().isEditingKit(player)) {
			player.sendMessage(ChatUtil.TRANSLATE("&cYou can't do this in your current state!"));
			return false;
		}
		
		if(targetMatch != null) {
			player.sendMessage(ChatUtil.TRANSLATE("&c" + target.getName() + " is not in the spawn!"));
			return false;
		}
		
		if(!Practice.getInstance().getProfileManager().getProfile(player).isAllowDuels()) {
			player.sendMessage(ChatUtil.TRANSLATE("&cYou can't duel another people while your duel requests are disabled!"));
			return false;
		}
		
		if(!Practice.getInstance().getProfileManager().getProfile(target).isAllowDuels()) {
			player.sendMessage(ChatUtil.TRANSLATE("&c" + target.getName() + " is not accepting duel requests!"));
			return false;
		}
		
		if(party != null) {
			player.sendMessage(ChatUtil.TRANSLATE("&cYou can't accept a 1vs1 duel request while in party!"));
			return false;
		}
		
		if(targetParty != null) {
			player.sendMessage(ChatUtil.TRANSLATE("&c" + target.getName() + " is in a party!"));
			return false;
		}
		
		if(player.hasMetadata("StaffMode") || player.hasMetadata("Vanished")) {
			player.sendMessage(ChatUtil.TRANSLATE("&cYou can't send a duel request while in Staff Mode/Vanished!"));
			return false;
		}
		
		if(target.hasMetadata("StaffMode") || player.hasMetadata("Vanished")) {
			player.sendMessage(ChatUtil.TRANSLATE("&c" + target.getName() + " is not accepting duel requests!"));
			return false;
		}
		
		return true;
	}
	
	public static boolean canEnableStaffMode(Player player) {
		Match match = Practice.getInstance().getMatchManager().getPlayerMatch(player);
		Party party = Practice.getInstance().getPartyManager().getPlayerParty(player);
		
		if(match != null) return false;
		if(party != null) return false;
		if(Practice.getInstance().getQueueManager().getPlayer(player) != null) return false;
		
		return true;
	}
	
	public static boolean isHCFClassActive(Player player, HCFClassType Class) {
		ItemStack helmet = player.getInventory().getHelmet();
		ItemStack chestplate = player.getInventory().getChestplate();
		ItemStack leggings = player.getInventory().getLeggings();
		ItemStack boots = player.getInventory().getBoots();
		
		switch(Class) {
		case BARD:
			if(helmet != null && chestplate != null && leggings != null && boots != null) {
				if(helmet.getType() == Material.GOLD_HELMET && chestplate.getType() == Material.GOLD_CHESTPLATE && leggings.getType() == Material.GOLD_LEGGINGS && boots.getType() == Material.GOLD_BOOTS) {
					return true;
				}
			}
			
			return false;
		case ROGUE:
			if(helmet != null && chestplate != null && leggings != null && boots != null) {
				if(helmet.getType() == Material.CHAINMAIL_HELMET && chestplate.getType() == Material.CHAINMAIL_CHESTPLATE && leggings.getType() == Material.CHAINMAIL_LEGGINGS && boots.getType() == Material.CHAINMAIL_BOOTS) {
					return true;
				}
			}
			
			return false;
		case ARCHER:
			if(helmet != null && chestplate != null && leggings != null && boots != null) {
				if(helmet.getType() == Material.LEATHER_HELMET && chestplate.getType() == Material.LEATHER_CHESTPLATE && leggings.getType() == Material.LEATHER_LEGGINGS && boots.getType() == Material.LEATHER_BOOTS) {
					return true;
				}
			}
			
			return false;
		default:
			return true;
		}
	}
	
	public static boolean isArmorFromClass(Material material, HCFClassType activeClass) {
		switch(activeClass) {
		case BARD:
			if(material == Material.GOLD_HELMET || material == Material.GOLD_CHESTPLATE || material == Material.GOLD_LEGGINGS || material == Material.GOLD_BOOTS) return true;
			return false;
		case ROGUE:
			if(material == Material.CHAINMAIL_HELMET || material == Material.CHAINMAIL_CHESTPLATE || material == Material.CHAINMAIL_LEGGINGS || material == Material.CHAINMAIL_BOOTS) return true;
			return false;
		case ARCHER:
			if(material == Material.LEATHER_HELMET || material == Material.LEATHER_CHESTPLATE || material == Material.LEATHER_LEGGINGS || material == Material.LEATHER_BOOTS) return true;
			return false;
		case DIAMOND:
			if(material == Material.DIAMOND_HELMET || material == Material.DIAMOND_CHESTPLATE || material == Material.DIAMOND_LEGGINGS || material == Material.DIAMOND_BOOTS) return true;
			return false;
		default:
			return false;
		}
	}
	
	public static boolean isClassLimitExceded(Party party, HCFClassType Class) {
		int totalClass = party.getTotalByClass(Class);
		
		int allowedClassAmount = 0;
		for(int i = 0; i < party.getMembers().size(); i += 5) {
			allowedClassAmount++;
		}
		
		if(totalClass > allowedClassAmount) return true;
		
		return false;
	}
}
