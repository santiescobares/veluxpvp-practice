package club.veluxpvp.practice.arena.command;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import club.veluxpvp.practice.arena.Arena;
import club.veluxpvp.practice.arena.Claim;
import club.veluxpvp.practice.arena.listener.ArenaClaimListener;
import club.veluxpvp.practice.match.TeamType;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.ItemBuilder;
import club.veluxpvp.practice.utilities.PlayerUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class ArenaClaimCommand extends ArenaCommand {

	@Command(name = "arena.claim")
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		Player player = cmd.getPlayer();
		
		if(args.length >= 2) {
			Arena arena = am.getByName(args[0]);
			TeamType teamType = TeamType.getByName(args[1]);
			
			if(arena == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cArena \"" + args[0] + "\" not found!"));
				return;
			}
			
			if(arena.isEnabled()) {
				player.sendMessage(ChatUtil.TRANSLATE("&cYou can't modify an arena while is enabled!"));
				return;
			}
			
			if(teamType == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cTeam \"" + args[1] + "\" not found!"));
				return;
			}
			
			// Claimed
			if(ArenaClaimListener.makingClaim.containsKey(player.getUniqueId())) {
				Arena a = ArenaClaimListener.arenaClaimMap.get(player.getUniqueId());
				final Claim claim = ArenaClaimListener.makingClaim.get(player.getUniqueId());
				TeamType team = ArenaClaimListener.teamClaimMap.get(player.getUniqueId());
				
				if(!a.getName().equalsIgnoreCase(arena.getName())) {
					player.sendMessage(ChatUtil.TRANSLATE("&cYou are already making a different arena claim!"));
					return;
				}
				
				if(claim.getCorner1() == null || claim.getCorner2() == null) {
					player.sendMessage(ChatUtil.TRANSLATE("&cPlease finish the claim selection!"));
					return;
				}
				
				if(team == TeamType.TEAM_1) {
					arena.setBridgesRedClaim(new Claim(team, claim.getCorner1(), claim.getCorner2()));
				} else {
					arena.setBridgesBlueClaim(new Claim(team, claim.getCorner1(), claim.getCorner2()));
				}
				
				player.getInventory().remove(Material.DIAMOND_HOE);
				player.sendMessage(ChatUtil.TRANSLATE((team == TeamType.TEAM_1 ? "&cRed" : "&9Blue") + " team's claim of arena &b" + arena.getName() + " &aset&f!"));
				
				ArenaClaimListener.arenaClaimMap.remove(player.getUniqueId());
				ArenaClaimListener.makingClaim.remove(player.getUniqueId());
				ArenaClaimListener.teamClaimMap.remove(player.getUniqueId());
				return;
			}
			
			// Didn't claim
			if(!giveClaimWand(player)) {
				player.sendMessage(ChatUtil.TRANSLATE("&cPlease make space in your inventory!"));
				return;
			}
			
			Claim claim = new Claim(teamType, null, null);
			
			ArenaClaimListener.arenaClaimMap.put(player.getUniqueId(), arena);
			ArenaClaimListener.makingClaim.put(player.getUniqueId(), claim);
			ArenaClaimListener.teamClaimMap.put(player.getUniqueId(), teamType);
			
			player.sendMessage(ChatUtil.TRANSLATE("&fSelect both bounds for the claim and then execute again &b/arena claim " + arena.getName() + " " + args[1] + "&f."));
			return;
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /arena claim <arenaName> <red|blue>"));
		}
	}
	
	private boolean giveClaimWand(Player player) {
		ItemStack claimWand = new ItemBuilder().of(Material.DIAMOND_HOE).name("&bClaim Wand").lore(Arrays.asList(
				"&7Left click to select the &bfirst &7position.",
				"&7Right click to select the &bsecond &7position."
				)).build();
		
		if(!PlayerUtil.hasEmptySlots(player, 1)) return false;
		
		player.getInventory().addItem(claimWand);
		player.updateInventory();
		
		return true;
	}
}
