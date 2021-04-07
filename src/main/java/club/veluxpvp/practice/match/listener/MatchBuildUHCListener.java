package club.veluxpvp.practice.match.listener;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.match.MatchEndReason;
import club.veluxpvp.practice.match.MatchManager;
import club.veluxpvp.practice.match.event.MatchResetEvent;
import club.veluxpvp.practice.utilities.ChatUtil;

public class MatchBuildUHCListener implements Listener {

	private MatchManager mm;
	
	public MatchBuildUHCListener() {
		this.mm = Practice.getInstance().getMatchManager();
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		if(event.getEntity() != null && event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Match match = mm.getPlayerMatch(player);
			
			if(match == null || match.isSpectating(player) || match.getLadder() != Ladder.BUILD_UHC && match.getLadder() != Ladder.FINAL_UHC) return;
			
			if(event.getRegainReason() == RegainReason.SATIATED) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		
		if(match == null || match.isSpectating(player) || match.getLadder() != Ladder.BUILD_UHC && match.getLadder() != Ladder.FINAL_UHC) return;
		
		ItemStack item = event.getItem();
		
		if(item == null || item.getType() == Material.AIR) return;
		if(item.getType() == Material.GOLDEN_APPLE && !item.hasItemMeta()) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 4 * 25, 1), true);
		}
		
		if(item.getType() == Material.GOLDEN_APPLE && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 8 * 25, 1), true);
		}
	}
	
	@EventHandler
	public void onPrepareItemCraft(PrepareItemCraftEvent event) {
		for(HumanEntity he : event.getViewers()) {
			if(he instanceof Player) {
				Player player = (Player) he;
				Match match = mm.getPlayerMatch(player);
				
				if(match == null || match.isSpectating(player) || match.getLadder() != Ladder.BUILD_UHC && match.getLadder() != Ladder.FINAL_UHC) return;
				
				event.getInventory().setResult(new ItemStack(Material.AIR));
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		
		if(match == null || match.isSpectating(player) || match.getLadder() != Ladder.BUILD_UHC && match.getLadder() != Ladder.FINAL_UHC) return;
		
		final Block block = event.getBlock();
		
		if(block == null || block.getType() == Material.AIR) return;
		if(!block.hasMetadata("PlacedOnMatch")) {
			event.setCancelled(true);
			return;
		}
		
		event.setExpToDrop(0);
		Bukkit.getScheduler().runTaskLater(Practice.getInstance(), () -> block.getDrops().clear(), 100L);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		
		if(match == null || match.isSpectating(player) || match.getLadder() != Ladder.BUILD_UHC && match.getLadder() != Ladder.FINAL_UHC) return;
		
		if(event.getBlock().getLocation().getBlockY() > match.getArena().getMaxBuildHeight()) {
			event.setCancelled(true);
			player.sendMessage(ChatUtil.TRANSLATE("&cYou can't build above y=" + match.getArena().getMaxBuildHeight() + "."));
			return;
		}
		
		event.getBlock().setMetadata("PlacedOnMatch", new FixedMetadataValue(Practice.getInstance(), true));
	}
	
	@EventHandler
	public void onBlockFromTo(BlockFromToEvent event) {
		event.getToBlock().setMetadata("PlacedOnMatch", new FixedMetadataValue(Practice.getInstance(), true));
		event.getBlock().setMetadata("PlacedOnMatch", new FixedMetadataValue(Practice.getInstance(), true));
	}
	
	@EventHandler
	public void onBlockForm(BlockFormEvent event) {
		event.getBlock().setMetadata("PlacedOnMatch", new FixedMetadataValue(Practice.getInstance(), true));
	}
	
	@EventHandler
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		Match match = mm.getPlayerMatch(player);
		
		if(match == null || match.isSpectating(player) || match.getLadder() != Ladder.BUILD_UHC && match.getLadder() != Ladder.FINAL_UHC) return;
		
		if(event.getBlockClicked().getLocation().getBlockY() > match.getArena().getMaxBuildHeight()) {
			event.setCancelled(true);
			player.sendMessage(ChatUtil.TRANSLATE("&cYou can't build above y=" + match.getArena().getMaxBuildHeight() + "."));
			return;
		}
	}
	
	@EventHandler
	public void onMatchReset(MatchResetEvent event) {
		Match match = event.getMatch();
		
		if(match.getLadder() == Ladder.BUILD_UHC || match.getLadder() == Ladder.FINAL_UHC) {
			boolean delaySetInUse = false;
			List<Block> placedBlocks = match.getArena().getBounds().getBlocks().stream().filter(b -> b != null && b.hasMetadata("PlacedOnMatch") || b.getType() == Material.WATER || b.getType() == Material.STATIONARY_WATER || b.getType() == Material.LAVA || b.getType() == Material.STATIONARY_LAVA).collect(Collectors.toList());
			
			if(placedBlocks.size() > 0) {
				delaySetInUse = true;
				
				if(match.getEndReason() == MatchEndReason.CANCELLED) {
					placedBlocks.stream().forEach(b -> b.setType(Material.AIR));
					return;
				} else {
					new BukkitRunnable() {

						@Override
						public void run() {
							if(placedBlocks.size() == 0) this.cancel();
							
							try {
								Block b1 = placedBlocks.get(new Random().nextInt(placedBlocks.size()));
								Block b2 = placedBlocks.get(new Random().nextInt(placedBlocks.size()));
								Block b3 = placedBlocks.get(new Random().nextInt(placedBlocks.size()));
								Block b4 = placedBlocks.get(new Random().nextInt(placedBlocks.size()));
								
								b1.removeMetadata("PlacedOnMatch", Practice.getInstance());
								b2.removeMetadata("PlacedOnMatch", Practice.getInstance());
								b3.removeMetadata("PlacedOnMatch", Practice.getInstance());
								b4.removeMetadata("PlacedOnMatch", Practice.getInstance());
								
								b1.setType(Material.AIR);
								b2.setType(Material.AIR);
								b3.setType(Material.AIR);
								b4.setType(Material.AIR);
								
								try {
									placedBlocks.remove(b1);
									placedBlocks.remove(b2);
									placedBlocks.remove(b3);
									placedBlocks.remove(b4);
								} catch(ConcurrentModificationException ignored) {}
							} catch(IllegalArgumentException ignored) {}
						}
						
					}.runTaskTimer(Practice.getInstance(), 1L, 1L);
				}
			}
			
			if(delaySetInUse) {
				Bukkit.getScheduler().runTaskLater(Practice.getInstance(), () -> match.getArena().setInUse(false), 50L);
			} else {
				match.getArena().setInUse(false);
			}
		}
	}
}
