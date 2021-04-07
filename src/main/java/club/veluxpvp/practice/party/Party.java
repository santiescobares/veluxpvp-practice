package club.veluxpvp.practice.party;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.Maps;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.item.ItemManager;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.party.pvpclass.HCFClassType;
import club.veluxpvp.practice.tournament.Tournament;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.PlayerUtil;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

@Getter @Setter
public class Party {

	private List<PartyMember> members;
	private int slots;
	private boolean open;
	
	private BukkitTask announceTask;
	
	private Map<Party, Long> lastDuelSent;
	private Map<Party, Ladder> lastDuelLadderSent;
	
	private Map<UUID, Long> lastInviteSent;
	
	public Party(Player leader) {
		this.members = new ArrayList<>();
		this.slots = 20;
		this.open = false;
		this.announceTask = null;
		
		this.lastDuelSent = Maps.newConcurrentMap();
		this.lastDuelLadderSent = Maps.newConcurrentMap();
		
		this.lastInviteSent = Maps.newConcurrentMap();
		
		this.members.add(new PartyMember(leader, PartyRole.LEADER));
		leader.sendMessage(ChatUtil.TRANSLATE("&aParty created!"));
	}
	
	public enum QuitReason {
		LEFT,
		KICKED;
	}
	
	public void addMember(Player player, PartyRole role) {
		this.members.add(new PartyMember(player, role));
		this.members.stream().forEach(m -> m.getPlayer().sendMessage(ChatUtil.TRANSLATE("&b" + player.getName() + " &7has joined the party!")));
	
		if(PlayerUtil.isInLobby(player)) {
			PlayerUtil.reset(player, player.getGameMode(), false);
			ItemManager.loadPartyItems(player);
		}
		
		PlayerUtil.updateVisibility(player);
		Practice.getInstance().getNametagManager().updateNametag(player);
		
		Match partyMatch = Practice.getInstance().getMatchManager().getPlayerMatch(this.getLeader().getPlayer());
		if(partyMatch != null) partyMatch.addSpectator(player);
	}
	
	public void removeMember(Player player, QuitReason quitReason) {
		PartyMember pm = this.getMember(player);
		boolean isLeader = false;
		
		if(pm.getRole() == PartyRole.LEADER) {
			isLeader = true;
		}
		
		this.members.remove(pm);
		this.members.stream().forEach(m -> m.getPlayer().sendMessage(ChatUtil.TRANSLATE("&b" + player.getName() + " &7has " + (quitReason == QuitReason.LEFT ? "left" : "been kicked from") + " the party!")));
		
		if(isLeader) {
			if(this.members.size() > 0) {
				PartyMember newLeader = this.members.get(new Random().nextInt(this.members.size()));
				newLeader.setRole(PartyRole.LEADER);
				
				this.members.stream().forEach(m -> m.getPlayer().sendMessage(ChatUtil.TRANSLATE("&b" + newLeader.getPlayer().getName() + " &7has been randomly chosen as the new party leader!")));
			} else {
				Bukkit.getScheduler().runTaskLater(Practice.getInstance(), () -> this.disband(), 2L);
			}
		}
		
		if(quitReason == QuitReason.LEFT) {
			player.sendMessage(ChatUtil.TRANSLATE("&cYou have left the party!"));
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cYou have been kicked from the party!"));
		}
		
		if(PlayerUtil.isInLobby(player)) {
			PlayerUtil.reset(player, player.getGameMode(), false);
			ItemManager.loadLobbyItems(player);
		}
		
		PlayerUtil.updateVisibility(player);
		Practice.getInstance().getNametagManager().updateNametag(player);
	}
	
	public PartyMember getMember(Player player) {
		return this.members.stream().filter(m -> m.getPlayer() == player).findFirst().orElse(null);
	}
	
	public PartyMember getLeader() {
		return this.members.stream().filter(m -> m.getRole() == PartyRole.LEADER).findFirst().orElse(null);
	}
	
	public void startAutoAnnounce() {
		announceJoinMessage();
		
		this.announceTask = new BukkitRunnable() {

			@Override
			public void run() {
				if(!open) {
					this.cancel();
					return;
				}
				
				announceJoinMessage();
			}
			
		}.runTaskTimerAsynchronously(Practice.getInstance(), 45 * 20L, 45 * 20L);
	}
	
	private void announceJoinMessage() {
		Player leader = getLeader().getPlayer();
		TextComponent tc = new TextComponent(ChatUtil.TRANSLATE("&b" + leader.getName() + " &7is hosting a public party! Type &b/party join " + leader.getName() + " &7or "));
		TextComponent clickHere = new TextComponent(ChatUtil.TRANSLATE("&aClick here"));
		
		clickHere.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatUtil.TRANSLATE("&7* &fClick to &ajoin &fthe party!")).create()));
		clickHere.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party join " + leader.getName()));
		
		tc.addExtra(clickHere);
		tc.addExtra(new TextComponent(ChatUtil.TRANSLATE(" &7to join.")));
		
		Bukkit.getOnlinePlayers().stream().forEach(p -> p.spigot().sendMessage(tc));
	}
	
	public void stopAutoAnnounce() {
		if(this.announceTask != null) {
			this.announceTask.cancel();
			this.announceTask = null;
		}
	}
	
	public void disband() {
		Tournament tour = Practice.getInstance().getTournamentManager().getActiveTournament();
		boolean delayDisband = false;
		
		if(tour != null && tour.isInTournament(this)) {
			tour.removeParticipant(this, tour.isStarted());
			delayDisband = true;
			
			if(tour.getParticipants().size() == 0) {
				tour.tryCancel();
			} else {
				tour.tryFinishRound();
			}
		}
		
		if(!delayDisband) {
			this.open = false;
			stopAutoAnnounce();
			
			Practice.getInstance().getPartyManager().getParties().remove(this);
			
			this.members.stream().filter(m -> PlayerUtil.isInLobby(m.getPlayer())).forEach(m -> PlayerUtil.reset(m.getPlayer(), m.getPlayer().getGameMode(), false));
			this.members.stream().filter(m -> PlayerUtil.isInLobby(m.getPlayer())).forEach(m -> ItemManager.loadLobbyItems(m.getPlayer()));
			this.members.stream().forEach(m -> PlayerUtil.updateVisibility(m.getPlayer()));
			this.members.stream().forEach(m -> Practice.getInstance().getNametagManager().updateNametag(m.getPlayer()));
			this.members.stream().forEach(m -> m.getPlayer().sendMessage(ChatUtil.TRANSLATE("&cYou party has been disbanded!")));
			this.members.clear();
		} else {
			Bukkit.getScheduler().runTaskLater(Practice.getInstance(), () -> {
				this.open = false;
				stopAutoAnnounce();
				
				Practice.getInstance().getPartyManager().getParties().remove(this);
				
				this.members.stream().filter(m -> PlayerUtil.isInLobby(m.getPlayer())).forEach(m -> PlayerUtil.reset(m.getPlayer(), m.getPlayer().getGameMode(), false));
				this.members.stream().filter(m -> PlayerUtil.isInLobby(m.getPlayer())).forEach(m -> ItemManager.loadLobbyItems(m.getPlayer()));
				this.members.stream().forEach(m -> PlayerUtil.updateVisibility(m.getPlayer()));
				this.members.stream().forEach(m -> Practice.getInstance().getNametagManager().updateNametag(m.getPlayer()));
				this.members.stream().forEach(m -> m.getPlayer().sendMessage(ChatUtil.TRANSLATE("&cYou party has been disbanded!")));
				this.members.clear();
			}, 3L);
		}
	}
	
	public boolean isFull() {
		return this.members.size() >= this.slots;
	}
	
	public int getTotalByClass(HCFClassType Class) {
		return (int) this.members.stream().filter(m -> m.getHcfClass() == Class).count();
	}
	
	public boolean isInLobby() {
		boolean inLobby = true;
		
		for(PartyMember pm : this.members) {
			if(Practice.getInstance().getMatchManager().getPlayerMatch(pm.getPlayer()) != null) inLobby = false;
		}
		
		return inLobby;
	}
}
