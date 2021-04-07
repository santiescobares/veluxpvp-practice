package club.veluxpvp.practice.arena.command;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import club.veluxpvp.practice.arena.Arena;
import club.veluxpvp.practice.arena.listener.ArenaBoundsListener;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.Cuboid;
import club.veluxpvp.practice.utilities.ItemBuilder;
import club.veluxpvp.practice.utilities.PlayerUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class ArenaBoundsCommand extends ArenaCommand {
	
	@Command(name = "arena.bounds")
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		Player player = cmd.getPlayer();
		
		if(args.length >= 1) {
			Arena arena = am.getByName(args[0]);
			
			if(arena == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cArena \"" + args[0] + "\" not found!"));
				return;
			}
			
			if(arena.isEnabled()) {
				player.sendMessage(ChatUtil.TRANSLATE("&cYou can't modify an arena while is enabled!"));
				return;
			}
			
			// Claimed
			if(ArenaBoundsListener.makingBounds.containsKey(player.getUniqueId())) {
				final Cuboid bounds = ArenaBoundsListener.makingBounds.get(player.getUniqueId());
				Arena a = ArenaBoundsListener.arenaMap.get(player.getUniqueId());
				
				if(!a.getName().equalsIgnoreCase(arena.getName())) {
					player.sendMessage(ChatUtil.TRANSLATE("&cYou are already claiming a different arena bounds!"));
					return;
				}
				
				if(bounds.getLocation1() == null || bounds.getLocation2() == null) {
					player.sendMessage(ChatUtil.TRANSLATE("&cPlease finish the bounds selection!"));
					return;
				}
				
				arena.setBounds(new Cuboid(bounds.getLocation1(), bounds.getLocation2()));
				
				player.getInventory().remove(Material.DIAMOND_HOE);
				player.sendMessage(ChatUtil.TRANSLATE("&fBounds of arena &b" + arena.getName() + " &aset&f!"));
				
				ArenaBoundsListener.makingBounds.remove(player.getUniqueId());
				ArenaBoundsListener.arenaMap.remove(player.getUniqueId());
				return;
			}
			
			// Didn't claim
			if(!giveClaimWand(player)) {
				player.sendMessage(ChatUtil.TRANSLATE("&cPlease make space in your inventory!"));
				return;
			}
			
			Cuboid bounds = new Cuboid(null, null);
			ArenaBoundsListener.makingBounds.put(player.getUniqueId(), bounds);
			ArenaBoundsListener.arenaMap.put(player.getUniqueId(), arena);
			
			player.sendMessage(ChatUtil.TRANSLATE("&fSelect both bounds for the arena and then execute again &b/arena bounds " + arena.getName() + "&f."));
			return;
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /arena bounds <arenaName>"));
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
