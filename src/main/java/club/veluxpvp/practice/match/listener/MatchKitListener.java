package club.veluxpvp.practice.match.listener;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Maps;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.kit.Kit;
import club.veluxpvp.practice.kit.KitManager;
import club.veluxpvp.practice.kit.KitType;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.match.event.MatchPreStartEvent;
import club.veluxpvp.practice.match.event.MatchResetEvent;
import club.veluxpvp.practice.party.PartyMember;
import club.veluxpvp.practice.party.pvpclass.HCFClassType;
import club.veluxpvp.practice.utilities.ItemBuilder;

public class MatchKitListener implements Listener {
	
	private Map<UUID, Map<Integer, Kit>> kitSlotMap;
	public static Map<UUID, Kit> choosedKit = Maps.newHashMap();
	
	public MatchKitListener() {
		this.kitSlotMap = Maps.newConcurrentMap();
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler
	public void onMatchPreStart(MatchPreStartEvent event) {
		Match match = event.getMatch();
		
		Bukkit.getScheduler().runTaskLater(Practice.getInstance(), () -> {
			for(Player p : match.getAlivePlayers()) {
				loadKits(p, match);
			}
		}, 10L);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Match match = Practice.getInstance().getMatchManager().getPlayerMatch(player);
		
		if(match == null || match.isSpectating(player)) return;
		
		if(event.getAction().name().startsWith("RIGHT_CLICK")) {
			ItemStack item = event.getItem();
			
			if(item == null || item.getType() != Material.ENCHANTED_BOOK) return;
			
			Kit clickedKit = this.getClickedKit(player, match, player.getInventory().getHeldItemSlot());
			
			if(clickedKit != null) {
				clickedKit.apply(player, false);
				choosedKit.put(player.getUniqueId(), clickedKit);
			}
		}
	}
	
	@EventHandler
	public void onMatchReset(MatchResetEvent event) {
		for(Player p : event.getMatch().getPlayers()) {
			choosedKit.remove(p.getUniqueId());
		}
	}
	
	public Kit getClickedKit(Player player, Match match, int slot) {
		if(slot == 0) {
			return Practice.getInstance().getKitManager().getDefaultKit(this.getKitType(player, match));
		} else {
			Map<Integer, Kit> kitSlotMap = this.kitSlotMap.getOrDefault(player.getUniqueId(), Maps.newConcurrentMap());
			
			return kitSlotMap.get(slot);
		}
	}
	
	public void loadKits(Player player, Match match) {
		KitManager km = Practice.getInstance().getKitManager();
		KitType kitType = this.getKitType(player, match);
		Kit defaultKit = km.getDefaultKit(kitType);
		
		if(defaultKit != null) {
			player.getInventory().setItem(0, new ItemBuilder().of(Material.ENCHANTED_BOOK).name(defaultKit.getDisplayName()).build());
		}
		
		Set<Kit> kits = km.getKitsOfType(player, kitType);
		
		if(kits.size() > 0) player.getInventory().setHeldItemSlot(2);
		
		int slot = 2;
		Map<Integer, Kit> kitSlotMap = Maps.newConcurrentMap();
		
		for(Kit k : kits) {
			player.getInventory().setItem(slot, new ItemBuilder().of(Material.ENCHANTED_BOOK).name(k.getDisplayName()).build());
			kitSlotMap.put(slot, k);
			slot++;
		}
		
		this.kitSlotMap.put(player.getUniqueId(), kitSlotMap);
		
		player.updateInventory();
	}
	
	private KitType getKitType(Player player, Match match) {
		KitType kitType = null;
		
		if(match.getLadder() == Ladder.BRIDGES) {
			kitType = match.getPlayerTeam(player) == match.getTeam1() ? KitType.BRIDGES_RED : KitType.BRIDGES_BLUE;
		} else if(match.getLadder() == Ladder.HCT_NO_DEBUFF || match.getLadder() == Ladder.HCT_DEBUFF) {
			PartyMember pm = Practice.getInstance().getPartyManager().getPlayerParty(player).getMember(player);
			
			if(pm == null) {
				kitType = match.getLadder() == Ladder.HCT_NO_DEBUFF ? KitType.HCT_DIAMOND_NO_DEBUFF : KitType.HCT_DIAMOND_DEBUFF;
			} else {
				if(pm.getHcfClass() == HCFClassType.DIAMOND) kitType = match.getLadder() == Ladder.HCT_NO_DEBUFF ? KitType.HCT_DIAMOND_NO_DEBUFF : KitType.HCT_DIAMOND_DEBUFF;
				if(pm.getHcfClass() == HCFClassType.BARD) kitType = KitType.HCT_BARD;
				if(pm.getHcfClass() == HCFClassType.ROGUE) kitType = KitType.HCT_ROGUE;
				if(pm.getHcfClass() == HCFClassType.ARCHER) kitType = KitType.HCT_ARCHER;
			}
		} else {
			kitType = KitType.getKitLadder(match.getLadder());
		}
		
		return kitType;
	}
}
