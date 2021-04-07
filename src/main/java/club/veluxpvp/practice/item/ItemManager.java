package club.veluxpvp.practice.item;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.party.Party;
import club.veluxpvp.practice.party.PartyRole;
import club.veluxpvp.practice.utilities.ItemBuilder;

public class ItemManager {
	
	// Lobby Items
	public static ItemStack getLobbyUnranked() {
		return new ItemBuilder().of(Material.IRON_SWORD).name("&aUnranked Queue").build();
	}
	
	public static ItemStack getLobbyRanked() {
		return new ItemBuilder().of(Material.DIAMOND_SWORD).name("&9Ranked Queue").build();
	}
	
	public static ItemStack getLobbyParty() {
		return new ItemBuilder().of(Material.NETHER_STAR).name("&bCreate a Party").build();
	}
	
	public static ItemStack getLobbySpectateMenu() {
		return new ItemBuilder().of(Material.LEVER).name("&cSpectate Menu").build();
	}
	
	public static ItemStack getLobbyStatistics(Player player) {
		return new ItemBuilder().of(Material.SKULL_ITEM).dataValue((byte) 3).skull(player.getName()).name("&eYour Statistics").build();
	}
	
	public static ItemStack getLobbyKitEditor() {
		return new ItemBuilder().of(Material.BOOK).name("&5Kit Editor").build();
	}
	
	public static void loadLobbyItems(Player player) {
		player.getInventory().setItem(0, getLobbyUnranked());
		player.getInventory().setItem(1, getLobbyRanked());
		player.getInventory().setItem(4, getLobbyParty());
		player.getInventory().setItem(6, getLobbySpectateMenu());
		player.getInventory().setItem(7, getLobbyStatistics(player));
		player.getInventory().setItem(8, getLobbyKitEditor());
		player.updateInventory();
	}
	
	// Queue Items
	public static ItemStack getQueueLeaveQueue() {
		return new ItemBuilder().of(Material.INK_SACK).dataValue((byte) 1).name("&cLeave Queue").build();
	}
	
	public static void loadQueueItems(Player player) {
		player.getInventory().setItem(4, getQueueLeaveQueue());
		player.updateInventory();
	}
	
	// Party Items
	public static ItemStack getPartyInfo() {
		return new ItemBuilder().of(Material.PAPER).name("&bParty Info").build();
	}
	
	public static ItemStack getPartyStartFight() {
		return new ItemBuilder().of(Material.IRON_AXE).name("&aStart Party Fight").build();
	}
	
	public static ItemStack getPartyHCFRoster() {
		return new ItemBuilder().of(Material.ITEM_FRAME).name("&6HCF Roster").build();
	}
	
	public static ItemStack getPartyOtherParties() {
		return new ItemBuilder().of(Material.DIAMOND).name("&eOther Parties").build();
	}
	
	public static ItemStack getPartyKitEditor() {
		return new ItemBuilder().of(Material.BOOK).name("&5Kit Editor").build();
	}
	
	public static ItemStack getPartyLeaveParty() {
		return new ItemBuilder().of(Material.INK_SACK).dataValue((byte) 1).name("&cLeave Party").build();
	}
	
	public static void loadPartyItems(Player player) {
		Party party = Practice.getInstance().getPartyManager().getPlayerParty(player);
		boolean isLeader = party.getMember(player).getRole() == PartyRole.LEADER;
		
		player.getInventory().setItem(0, getPartyInfo());
		
		if(isLeader) {
			player.getInventory().setItem(1, getPartyStartFight());
		}
		
		player.getInventory().setItem(5, getPartyHCFRoster());
		player.getInventory().setItem(6, getPartyOtherParties());
		player.getInventory().setItem(7, getPartyKitEditor());
		player.getInventory().setItem(8, getPartyLeaveParty());
		player.updateInventory();
	}
	
	// Spectator Items
	public static ItemStack getSpectatorHideSpectators() {
		return new ItemBuilder().of(Material.INK_SACK).dataValue((byte) 10).name("&eHide Spectators").build();
	}
	
	public static ItemStack getSpectatorShowSpectators() {
		return new ItemBuilder().of(Material.INK_SACK).dataValue((byte) 8).name("&eShow Spectators").build();
	}
	
	public static ItemStack getSpectatorLastPvP() {
		return new ItemBuilder().of(Material.EMERALD).name("&aTeleport to Last PvP").build();
	}
	
	public static ItemStack getSpectatorBackToSpawn() {
		return new ItemBuilder().of(Material.INK_SACK).dataValue((byte) 1).name("&cBack to Spawn").build();
	}
	
	public static void loadSpectatorItems(Player player) {
		player.getInventory().setItem(0, getSpectatorLastPvP());
		player.getInventory().setItem(1, getSpectatorHideSpectators());
		player.getInventory().setItem(8, getSpectatorBackToSpawn());
		player.updateInventory();
	}
	
	// Staff Mode Items
	public static ItemStack getSMTeleporter() {
		return new ItemBuilder().of(Material.COMPASS).name("&eTeleporter").build();
	}
	
	public static ItemStack getSMInventoryInspector() {
		return new ItemBuilder().of(Material.BOOK).name("&aInventory Inspector").build();
	}
	
	public static ItemStack getSMFreeze() {
		return new ItemBuilder().of(Material.ICE).name("&bFreeze").build();
	}
	
	public static ItemStack getSMVanishOn() {
		return new ItemBuilder().of(Material.INK_SACK).dataValue((byte) 10).name("&9Vanish: &aEnabled").build();
	}
	
	public static ItemStack getSMVanishOff() {
		return new ItemBuilder().of(Material.INK_SACK).dataValue((byte) 8).name("&9Vanish: &cDisabled").build();
	}
	
	public static ItemStack getSMRandomTeleporter() {
		return new ItemBuilder().of(Material.RECORD_3).name("&cRandom Teleporter").build();
	}
	
	public static void loadStaffModeItems(Player player) {
		player.getInventory().setItem(0, getSMTeleporter());
		player.getInventory().setItem(1, getSMInventoryInspector());
		player.getInventory().setItem(2, getSMFreeze());
		
		if(Practice.getInstance().getStaffModeManager().isVanished(player)) {
			player.getInventory().setItem(7, getSMVanishOn());
		} else {
			player.getInventory().setItem(7, getSMVanishOff());
		}
		
		player.getInventory().setItem(8, getSMRandomTeleporter());
		player.updateInventory();
	}
}
