package club.veluxpvp.practice.queue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Arena;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.item.ItemManager;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.profile.Profile;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.PlayerUtil;
import lombok.Getter;

public class QueueManager {

	@Getter private List<QueuedPlayer> queuedPlayers;
	private Map<UUID, UUID> foundMatch;
	private boolean checkStarted;
	
	public QueueManager() {
		this.checkStarted = false;
		this.queuedPlayers = new ArrayList<>();
		this.foundMatch = new ConcurrentHashMap<>();
	}
	
	public void addPlayer(Player player, Ladder ladder, boolean ranked) {
		boolean similarPing = Practice.getInstance().getProfileManager().getProfile(player).isRankedSimilarPing();
		QueuedPlayer qp = new QueuedPlayer(player, ladder, ranked, similarPing);
		
		this.queuedPlayers.add(qp);
		
		PlayerUtil.reset(player, player.getGameMode(), false);
		ItemManager.loadQueueItems(player);
		
		player.sendMessage(ChatUtil.TRANSLATE("&aYou are now queued for &b" + (ranked ? "Ranked" : "Unranked") + " " + ladder.name + "&a."));
		
		if(!checkStarted) startCheck();
	}
	
	public void startCheck() {
		this.checkStarted = true;
		new BukkitRunnable() {

			@Override
			public void run() {
				if(!checkStarted || queuedPlayers.size() == 0) {
					this.cancel();
					checkStarted = false;
					return;
				}
				
				for(QueuedPlayer queuedPlayer : queuedPlayers) {
					for(Map.Entry<UUID, UUID> matchFoundEntry : foundMatch.entrySet()) {
						
						if(Bukkit.getPlayer(matchFoundEntry.getValue()) == null) {
							QueuedPlayer matchFoundPlayer = getPlayer(Bukkit.getPlayer(matchFoundEntry.getKey()));

							if(queuedPlayer == matchFoundPlayer) continue;
							if(queuedPlayer.getLadder() != matchFoundPlayer.getLadder()) continue;
							if(queuedPlayer.isRanked() != matchFoundPlayer.isRanked()) continue;
							
							if(queuedPlayer.isRanked() && matchFoundPlayer.isRanked()) {
								Profile p1 = Practice.getInstance().getProfileManager().getProfile(queuedPlayer.getPlayer());
								Profile p2 = Practice.getInstance().getProfileManager().getProfile(matchFoundPlayer.getPlayer());
							
								if(!queuedPlayer.isInsideEloRange(p2.getElo(queuedPlayer.getLadder())) || !matchFoundPlayer.isInsideEloRange(p1.getElo(matchFoundPlayer.getLadder()))) continue;
								if(p1.isRankedSimilarPing() || p2.isRankedSimilarPing()) {
									int playerPing = PlayerUtil.getPing(queuedPlayer.getPlayer());
									int opponentPing = PlayerUtil.getPing(matchFoundPlayer.getPlayer());
									
									if(opponentPing > (playerPing + 15) || opponentPing < (playerPing - 15)) {
										continue;
									}
								}
							}
							
							foundMatch.put(matchFoundPlayer.getPlayer().getUniqueId(), queuedPlayer.getPlayer().getUniqueId());
						}
					}
					
					if(!foundMatch.containsKey(queuedPlayer.getPlayer().getUniqueId()) && !foundMatch.containsValue(queuedPlayer.getPlayer().getUniqueId())) {
						foundMatch.put(queuedPlayer.getPlayer().getUniqueId(), UUID.randomUUID());
						continue;
					}
				}
				
				for(Map.Entry<UUID, UUID> foundMatchEntry : foundMatch.entrySet()) {
					if(Bukkit.getPlayer(foundMatchEntry.getKey()) != null && Bukkit.getPlayer(foundMatchEntry.getValue()) != null) {
						QueuedPlayer qp1 = getPlayer(Bukkit.getPlayer(foundMatchEntry.getKey()));
						QueuedPlayer qp2 = getPlayer(Bukkit.getPlayer(foundMatchEntry.getValue()));
						
						if(qp1 == null || qp2 == null) continue;
						
						Arena arena = Practice.getInstance().getArenaManager().getRandomArena(qp1.getLadder(), qp1.isRanked());

						if(arena == null) {
							removePlayer(qp1.getPlayer(), false, false);
							removePlayer(qp2.getPlayer(), false, false);
							
							qp1.getPlayer().sendMessage(ChatUtil.TRANSLATE("&cThere are no avaiable arenas for " + qp1.getLadder().name + " ladder!"));
							qp2.getPlayer().sendMessage(ChatUtil.TRANSLATE("&cThere are no avaiable arenas for " + qp2.getLadder().name + " ladder!"));
							
							continue;
						}
						
						Match match = new Match(arena, qp1.getLadder(), qp1.isRanked());
						
						match.getAlivePlayers().add(qp1.getPlayer());
						match.getAlivePlayers().add(qp2.getPlayer());
						
						qp1.getPlayer().sendMessage(" ");
						qp1.getPlayer().sendMessage(ChatUtil.TRANSLATE("&b&lMatch Found"));
						
						qp2.getPlayer().sendMessage(" ");
						qp2.getPlayer().sendMessage(ChatUtil.TRANSLATE("&b&lMatch Found"));

						match.startCountdown();
						
						removePlayer(qp1.getPlayer(), true, false);
						removePlayer(qp2.getPlayer(), true, false);
					}
				}
			}
			
		}.runTaskTimer(Practice.getInstance(), 0L, 10L);
	}
	
	public void removePlayer(Player player, boolean matchFound, boolean quit) {
		QueuedPlayer qp = this.getPlayer(player);
		
		if(qp == null || !this.queuedPlayers.contains(qp)) return;
		
		this.queuedPlayers.remove(qp);
		this.foundMatch.remove(player.getUniqueId());
		
		if(quit) return;
		
		if(!matchFound) {
			PlayerUtil.reset(player, player.getGameMode(), false);
			ItemManager.loadLobbyItems(player);
			
			player.sendMessage(ChatUtil.TRANSLATE("&cYou are no longer queued for &b" + (qp.isRanked() ? "Ranked" : "Unranked") + " " + qp.getLadder().name + "&c."));
		}
	}
	
	public QueuedPlayer getPlayer(Player player) {
		return this.queuedPlayers.stream().filter(qp -> qp.getPlayer() == player).findFirst().orElse(null);
	}
	
	public int getQueuedOnLadder(Ladder ladder, boolean ranked) {
		return (int) this.queuedPlayers.stream().filter(qp -> qp.getLadder() == ladder && qp.isRanked() == ranked).count();
	}
}
