package club.veluxpvp.practice.match.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.elo.EloCalculator;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.match.MatchEndReason;
import club.veluxpvp.practice.match.MatchTeam;
import club.veluxpvp.practice.match.TeamType;
import club.veluxpvp.practice.match.event.MatchEndEvent;
import club.veluxpvp.practice.profile.Profile;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.MatchUtil;
import club.veluxpvp.practice.utilities.TimeUtil;
import net.md_5.bungee.api.chat.TextComponent;

public class MatchFinishListener implements Listener {

	public MatchFinishListener() {
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onMatchEnd(MatchEndEvent event) {
		Match match = event.getMatch();
		
		if(match.getEndReason() == MatchEndReason.CANCELLED) return;
		
		if(match.getEndReason() == MatchEndReason.ONE_TEAM_ALIVE || match.getEndReason() == MatchEndReason.BRIDGES_TEAM_WINS) {
			MatchTeam winnerTeam = null;
			MatchTeam loserTeam = null;
			
			if(match.getEndReason() == MatchEndReason.ONE_TEAM_ALIVE) {
				winnerTeam = match.getLastAliveTeam();
				loserTeam = winnerTeam.getType() == TeamType.TEAM_1 ? match.getTeam2() : match.getTeam1();
			} else {
				winnerTeam = MatchBridgesListener.teamScore.get(match.getTeam1()) == 3 ? match.getTeam1() : match.getTeam2();
				loserTeam = winnerTeam.getType() == TeamType.TEAM_1 ? match.getTeam2() : match.getTeam1();
			}
			
			if(winnerTeam.getPlayers().size() == 1 && loserTeam.getPlayers().size() == 1) {
				match.getSwapInventoriesMap().put(winnerTeam.getFirstPlayer().getUniqueId(), loserTeam.getFirstPlayer().getUniqueId());
				match.getSwapInventoriesMap().put(loserTeam.getFirstPlayer().getUniqueId(), winnerTeam.getFirstPlayer().getUniqueId());
			
				Profile winnerProfile = Practice.getInstance().getProfileManager().getProfile(winnerTeam.getFirstPlayer());
				Profile loserProfile = Practice.getInstance().getProfileManager().getProfile(loserTeam.getFirstPlayer());
				
				// Ranked Match Log
				if(match.isRanked()) {
					// Updating elo
					final int winnerElo = winnerProfile.getElo(match.getLadder());
					final int loserElo = loserProfile.getElo(match.getLadder());
					final int eloUpdate = EloCalculator.calculate(winnerElo, loserElo);
					
					winnerProfile.addElo(match.getLadder(), eloUpdate);
					winnerProfile.setRankedWins(winnerProfile.getRankedWins() + 1);
					loserProfile.removeElo(match.getLadder(), eloUpdate);
					loserProfile.setRankedLoses(loserProfile.getRankedLoses() + 1);
					
					match.setWinnerElo(winnerElo);
					match.setLoserElo(loserElo);
					match.setEloUpdate(eloUpdate);
				} else {
					if(!match.isFfa() && !match.isParty() && Practice.getInstance().getPartyManager().getPlayerParty(winnerTeam.getFirstPlayer()) == null && Practice.getInstance().getPartyManager().getPlayerParty(loserTeam.getFirstPlayer()) == null) {
						winnerProfile.setUnrankedWins(winnerProfile.getUnrankedWins() + 1);
						loserProfile.setUnrankedLoses(loserProfile.getUnrankedLoses() + 1);
					}
				}
			}
			
			List<Player> spectators = match.getPlayers().stream().filter(p -> match.getSpectators().contains(p) && match.getPlayerTeam(p) == null).collect(Collectors.toList());
			
			for(Player p : match.getPlayers()) {
				p.sendMessage(ChatUtil.LINE());
				p.sendMessage(ChatUtil.TRANSLATE("&b&lMatch Ended &7(Hover names for info)"));
				p.sendMessage(" ");
				
				TextComponent winners = new TextComponent(ChatUtil.TRANSLATE(" &7* &a" + (winnerTeam.getPlayers().size() > 1 ? "Winners" : "Winner") + "&7: &f"));
				winners.addExtra(MatchUtil.getPostMatchPlayerNames(match, winnerTeam.getPlayers(), "&f"));
				
				p.spigot().sendMessage(winners);
				
				TextComponent losers = new TextComponent(ChatUtil.TRANSLATE(" &7* &c" + (loserTeam.getPlayers().size() > 1 ? "Losers" : "Loser") + "&7: &f"));
				losers.addExtra(MatchUtil.getPostMatchPlayerNames(match, loserTeam.getPlayers(), "&f"));
				
				p.spigot().sendMessage(losers);
				
				p.sendMessage(" ");
				p.sendMessage(ChatUtil.TRANSLATE(" &7* &bDuration&7: &f" + TimeUtil.getFormattedDuration(match.getDuration(), true)));
				
				if(match.isRanked()) {
					p.sendMessage(ChatUtil.TRANSLATE(" &7* &bElo Updates&7: &a" + winnerTeam.getFirstPlayer().getName() + " +" + match.getEloUpdate() + " (" + (match.getWinnerElo() + match.getEloUpdate()) + ") &7- &c" + loserTeam.getFirstPlayer().getName() + " -" + match.getEloUpdate() + " (" + (match.getLoserElo() - match.getEloUpdate()) + ")"));
				}
				
				if(spectators.size() > 0) {
					TextComponent tc = new TextComponent(ChatUtil.TRANSLATE(" &7* &bSpectators (" + spectators.size() + ")&7: &f"));
					tc.addExtra(MatchUtil.getSpectatorsNames(match));
					
					p.spigot().sendMessage(tc);
				}
				
				p.sendMessage(ChatUtil.LINE());
			}
		}
		
		if(match.getEndReason() == MatchEndReason.ONE_PLAYER_ALIVE) {
			Player alivePlayer = match.getLastAlivePlayer();
			List<Player> alivePlayerAsList = new ArrayList<>();
			List<Player> otherPlayers = new ArrayList<>();

			alivePlayerAsList.add(alivePlayer);
			
			for(UUID uuid : match.getPlayersWhoPlayedCache()) {
				Player player = Bukkit.getPlayer(uuid);
				
				if(player != null) otherPlayers.add(player);
			}
			
			otherPlayers.remove(alivePlayer);
			
			List<Player> spectators = match.getPlayers().stream().filter(p -> match.getSpectators().contains(p) && match.getPlayerTeam(p) == null).collect(Collectors.toList());
			
			for(Player p : match.getPlayers()) {
				p.sendMessage(ChatUtil.LINE());
				p.sendMessage(ChatUtil.TRANSLATE("&b&lMatch Ended &7(Hover names for info)"));
				p.sendMessage(" ");
				
				TextComponent winner = new TextComponent(ChatUtil.TRANSLATE(" &7* &aWinner&7: &f"));
				winner.addExtra(MatchUtil.getPostMatchPlayerNames(match, alivePlayerAsList, "&f"));
				
				p.spigot().sendMessage(winner);
				
				TextComponent losers = new TextComponent(ChatUtil.TRANSLATE(" &7* &c" + (otherPlayers.size() > 1 ? "Losers" : "Loser") + "&7: &f"));
				losers.addExtra(MatchUtil.getPostMatchPlayerNames(match, otherPlayers, "&f"));
				
				p.spigot().sendMessage(losers);
				
				p.sendMessage(" ");
				p.sendMessage(ChatUtil.TRANSLATE(" &7* &bDuration&7: &f" + TimeUtil.getFormattedDuration(match.getDuration(), true)));
				
				if(spectators.size() > 0) {
					TextComponent tc = new TextComponent(ChatUtil.TRANSLATE(" &7* &bSpectators (" + spectators.size() + ")&7: &f"));
					tc.addExtra(MatchUtil.getSpectatorsNames(match));
					
					p.spigot().sendMessage(tc);
				}
				
				p.sendMessage(ChatUtil.LINE());
			}
		}
		
		if(match.getEndReason() == MatchEndReason.PARKOUR_FINISHED) {
			if(!match.isFfa()) {
				MatchTeam winnerTeam = MatchParkourListener.winnerTeam.get(match);
				MatchTeam loserTeam = winnerTeam.getType() == TeamType.TEAM_1 ? match.getTeam2() : match.getTeam1();
				
				if(winnerTeam.getPlayers().size() == 1 && loserTeam.getPlayers().size() == 1) {
					match.getSwapInventoriesMap().put(winnerTeam.getFirstPlayer().getUniqueId(), loserTeam.getFirstPlayer().getUniqueId());
					match.getSwapInventoriesMap().put(loserTeam.getFirstPlayer().getUniqueId(), winnerTeam.getFirstPlayer().getUniqueId());
				}
				
				List<Player> spectators = match.getPlayers().stream().filter(p -> match.getSpectators().contains(p) && match.getPlayerTeam(p) == null).collect(Collectors.toList());
				
				for(Player p : match.getPlayers()) {
					p.sendMessage(ChatUtil.LINE());
					p.sendMessage(ChatUtil.TRANSLATE("&b&lMatch Ended &7(Hover names for info)"));
					p.sendMessage(" ");
					
					TextComponent winners = new TextComponent(ChatUtil.TRANSLATE(" &7* &a" + (winnerTeam.getPlayers().size() > 1 ? "Winners" : "Winner") + "&7: &f"));
					winners.addExtra(MatchUtil.getPostMatchPlayerNames(match, winnerTeam.getPlayers(), "&f"));
					
					p.spigot().sendMessage(winners);
					
					TextComponent losers = new TextComponent(ChatUtil.TRANSLATE(" &7* &c" + (loserTeam.getPlayers().size() > 1 ? "Losers" : "Loser") + "&7: &f"));
					losers.addExtra(MatchUtil.getPostMatchPlayerNames(match, loserTeam.getPlayers(), "&f"));
					
					p.spigot().sendMessage(losers);
					
					p.sendMessage(" ");
					p.sendMessage(ChatUtil.TRANSLATE(" &7* &bDuration&7: &f" + TimeUtil.getFormattedDuration(match.getDuration(), true)));
					
					if(match.isRanked()) {
						p.sendMessage(ChatUtil.TRANSLATE(" &7* &bElo Updates&7: &a" + winnerTeam.getFirstPlayer().getName() + " +" + match.getEloUpdate() + " (" + (match.getWinnerElo() + match.getEloUpdate()) + ") &7- &c" + loserTeam.getFirstPlayer().getName() + " -" + match.getEloUpdate() + " (" + (match.getLoserElo() - match.getEloUpdate()) + ")"));
					}
					
					if(spectators.size() > 0) {
						TextComponent tc = new TextComponent(ChatUtil.TRANSLATE(" &7* &bSpectators (" + spectators.size() + ")&7: &f"));
						tc.addExtra(MatchUtil.getSpectatorsNames(match));
						
						p.spigot().sendMessage(tc);
					}
					
					p.sendMessage(ChatUtil.LINE());
				}
			} else {
				Player alivePlayer = Bukkit.getPlayer(MatchParkourListener.winnerPlayer.get(match));
				List<Player> alivePlayerAsList = new ArrayList<>();
				List<Player> otherPlayers = new ArrayList<>();
				
				alivePlayerAsList.add(alivePlayer);
				
				for(UUID uuid : match.getPlayersWhoPlayedCache()) {
					Player player = Bukkit.getPlayer(uuid);
					
					if(player != null) otherPlayers.add(player);
				}
				
				otherPlayers.remove(alivePlayer);
				
				List<Player> spectators = match.getPlayers().stream().filter(p -> match.getSpectators().contains(p) && match.getPlayerTeam(p) == null).collect(Collectors.toList());
				
				for(Player p : match.getPlayers()) {
					p.sendMessage(ChatUtil.LINE());
					p.sendMessage(ChatUtil.TRANSLATE("&b&lMatch Ended &7(Hover names for info)"));
					p.sendMessage(" ");
					
					TextComponent winner = new TextComponent(ChatUtil.TRANSLATE(" &7* &aWinner&7: &f"));
					winner.addExtra(MatchUtil.getPostMatchPlayerNames(match, alivePlayerAsList, "&f"));
					
					p.spigot().sendMessage(winner);
					
					TextComponent losers = new TextComponent(ChatUtil.TRANSLATE(" &7* &c" + (otherPlayers.size() > 1 ? "Losers" : "Loser") + "&7: &f"));
					losers.addExtra(MatchUtil.getPostMatchPlayerNames(match, otherPlayers, "&f"));
					
					p.spigot().sendMessage(losers);
					
					p.sendMessage(" ");
					p.sendMessage(ChatUtil.TRANSLATE(" &7* &bDuration&7: &f" + TimeUtil.getFormattedDuration(match.getDuration(), true)));
					
					if(spectators.size() > 0) {
						TextComponent tc = new TextComponent(ChatUtil.TRANSLATE(" &7* &bSpectators (" + spectators.size() + ")&7: &f"));
						tc.addExtra(MatchUtil.getSpectatorsNames(match));
						
						p.spigot().sendMessage(tc);
					}
					
					p.sendMessage(ChatUtil.LINE());
				}
			}
		}
	}
}
