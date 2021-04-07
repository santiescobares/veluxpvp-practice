package club.veluxpvp.practice.duel;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Arena;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.profile.Profile;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.PlayerUtil;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

@Getter @Setter
public class Duel {

	private Player sender, target;
	private Ladder ladder;
	private Arena arena;
	private long expiresAt;
	
	public Duel(Player sender, Player target) {
		this.sender = sender;
		this.target = target;
		this.ladder = null;
		this.arena = null;
		this.expiresAt = System.currentTimeMillis();
	}
	
	public void send() {
		Profile p1 = Practice.getInstance().getProfileManager().getProfile(this.sender);
		Profile p2 = Practice.getInstance().getProfileManager().getProfile(this.target);
		
		if(!p1.isAllowDuels()) {
			this.sender.sendMessage(ChatUtil.TRANSLATE("&cYou can't duel another people while your duel requests are disabled!"));
			return;
		}
		
		if(!p2.isAllowDuels()) {
			this.sender.sendMessage(ChatUtil.TRANSLATE("&c" + this.target.getName() + " is not accepting duel requests!"));
			return;
		}
		
		TextComponent message = new TextComponent(ChatUtil.TRANSLATE("&b" + this.sender.getName() + " &7(&a" + PlayerUtil.getPing(this.sender) + "ms&7) &7has sent you a &b" + this.ladder.name + " &7duel request in arena &b" + this.arena.getName() + "&7! Type &b/accept " + this.sender.getName() + " &7or "));
		TextComponent clickHere = new TextComponent(ChatUtil.TRANSLATE("&aClick here"));
		clickHere.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatUtil.TRANSLATE("&7* &fClick to &aaccept &fthe duel!")).create()));
		clickHere.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/accept " + this.sender.getName()));
		message.addExtra(clickHere);
		message.addExtra(new TextComponent(ChatUtil.TRANSLATE(" &7to accept it.")));
		
		this.target.spigot().sendMessage(message);
		this.sender.sendMessage(ChatUtil.TRANSLATE("&aYou have successfully sent a " + this.ladder.name + " duel request to " + this.target.getName() + " in arena " + this.arena.getName() + "!"));
	
		this.expiresAt = System.currentTimeMillis() + (30 * 1000);
	}
	
	public boolean isExpired() {
		return System.currentTimeMillis() > this.expiresAt;
	}
}
