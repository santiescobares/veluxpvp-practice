package club.veluxpvp.practice.event;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.Lists;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Arena;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.PlayerUtil;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

@Getter @Setter
public class PracticeEvent {

	protected EventType type;
	protected Arena arena;
	protected List<Player> aliveParticipants, spectators;
	protected int slots;
	protected EventState state;
	protected long startedAt, endedAt;
	
	protected BukkitTask task;
	protected List<UUID> playersWhoPlayedCache;
	protected int TOTAL_PARTICIPANTS;
	
	protected final int MIN_PLAYERS = 4, MAX_PLAYERS = 100;
	
	public PracticeEvent(EventType type, Arena arena, int slots) {
		this.type = type;
		this.arena = arena;
		this.slots = slots;
		this.aliveParticipants = Lists.newArrayList();
		this.spectators = Lists.newArrayList();
		this.playersWhoPlayedCache = Lists.newArrayList();
		this.state = EventState.WAITING;
		this.startedAt = System.currentTimeMillis();
		this.endedAt = System.currentTimeMillis();
		
		this.TOTAL_PARTICIPANTS = 0;
		
		broadcastJoinMessage();
		startAnnounce();
	}
	
	private void broadcastJoinMessage() {
		TextComponent message = new TextComponent(ChatUtil.TRANSLATE("&7A &b" + this.type.name + " Event &7has started! Type &b/event join &7or "));
		TextComponent clickHere = new TextComponent(ChatUtil.TRANSLATE("&aClick here"));
		clickHere.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatUtil.TRANSLATE("&7* &fClick to &ajoin &fthe event!")).create()));
		clickHere.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/event join"));
		message.addExtra(clickHere);
		message.addExtra(new TextComponent(ChatUtil.TRANSLATE(" &7to join! &f(" + this.aliveParticipants.size() + "/" + this.slots + ")")));
		
		Bukkit.getOnlinePlayers().stream().forEach(p -> p.sendMessage(" "));
		Bukkit.getOnlinePlayers().stream().forEach(p -> p.spigot().sendMessage(message));
		Bukkit.getOnlinePlayers().stream().forEach(p -> p.sendMessage(" "));
	}
	
	private void startAnnounce() {
		this.task = new BukkitRunnable() {

			@Override
			public void run() {
				if(state != EventState.WAITING) {
					this.cancel();
					return;
				}
				
				broadcastJoinMessage();
			}
			
		}.runTaskTimerAsynchronously(Practice.getInstance(), 45 * 20L, 45 * 20L);
	}
	
	public void addPlayer(Player player) {
		if(this.aliveParticipants.size() >= this.MAX_PLAYERS) {
			player.sendMessage(ChatUtil.TRANSLATE("&cThe event is full!"));
			return;
		}
		
		this.aliveParticipants.add(player);
		
		PlayerUtil.reset(player, GameMode.SURVIVAL, true);
		player.teleport(this.arena.getEventsSpawn());
		PlayerUtil.updateVisibility(player);
		Practice.getInstance().getNametagManager().updateNametag(player);
		
		this.messageAll("&b" + player.getName() + " &7has joined the event! &f(" + this.aliveParticipants.size() + "/" + this.slots + ")");
	}
	
	public void removePlayer(Player player) {
		this.aliveParticipants.remove(player);
		this.spectators.remove(player);
		
		PlayerUtil.reset(player, GameMode.SURVIVAL, true);
		PlayerUtil.sendToSpawn(player);
		PlayerUtil.updateVisibility(player);
		Practice.getInstance().getNametagManager().updateNametag(player);
		
		if(this.state == EventState.WAITING) this.messageAll("&b" + player.getName() + " &7has left the event! &f(" + this.aliveParticipants.size() + "/" + this.slots + ")");
	}
	
	public void addSpectator(Player player) {
		this.spectators.add(player);
		
		PlayerUtil.reset(player, GameMode.SURVIVAL, true);
		player.teleport(this.arena.getSpectatorsSpawn());
		PlayerUtil.updateVisibility(player);
		Practice.getInstance().getNametagManager().updateNametag(player);
	}
	
	public void setAsSpectator(Player player) {
		this.spectators.add(player);
		this.aliveParticipants.remove(player);
		
		PlayerUtil.reset(player, GameMode.SURVIVAL, true);
		player.teleport(this.arena.getSpectatorsSpawn());
		PlayerUtil.updateVisibility(player);
		Practice.getInstance().getNametagManager().updateNametag(player);
	}
	
	public void messageAll(String message) {
		this.getPlayers().stream().forEach(p -> p.sendMessage(ChatUtil.TRANSLATE(message)));
	}
	
	public List<Player> getPlayers() {
		List<Player> players = Lists.newArrayList(this.aliveParticipants);
		this.spectators.stream().forEach(p -> players.add(p));
		return players;
	}
	
	public boolean isParticipating(Player player) {
		return this.aliveParticipants.contains(player);
	}
	
	public boolean isSpectating(Player player) {
		return this.spectators.contains(player);
	}
	
	public void tryStart() {
		if(this.state == EventState.STARTING) return;
		
		if(this.aliveParticipants.size() >= this.MIN_PLAYERS) {
			startCountdown();
		}
	}
	
	public void tryStopCountdown() {
		if(this.state != EventState.STARTING) return;
		
		if(this.aliveParticipants.size() < this.MIN_PLAYERS) {
			stopCountdown();
		}
	}
	
	public void stopCountdown() {
		this.state = EventState.WAITING;
		this.task.cancel();
		startAnnounce();
	}
	
	public void startCountdown() {}
	
	public void start() {}
	
	public void finish(boolean cancelled) {}
}
