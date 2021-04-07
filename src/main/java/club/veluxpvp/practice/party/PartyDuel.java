package club.veluxpvp.practice.party;

import club.veluxpvp.practice.arena.Arena;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.utilities.ChatUtil;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

@Getter @Setter
public class PartyDuel {

	private Party sender, target;
	private Ladder ladder;
	private Arena arena;
	private long expiresAt;
	
	public PartyDuel(Party sender, Party target) {
		this.sender = sender;
		this.target = target;
		this.ladder = null;
		this.arena = null;
		this.expiresAt = System.currentTimeMillis();
	}
	
	public void send() {
		TextComponent message = new TextComponent(ChatUtil.TRANSLATE("&b" + sender.getLeader().getPlayer().getName() + "'s Party (" + sender.getMembers().size() + ") &7has sent you a &b" + ladder.name + " &7duel request. Type &b/party accept " + sender.getLeader().getPlayer().getName() + " &7or "));
		TextComponent clickHere = new TextComponent(ChatUtil.TRANSLATE("&aClick here"));
		clickHere.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatUtil.TRANSLATE("&7* &fClick to &aaccept &fthe duel!")).create()));
		clickHere.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + sender.getLeader().getPlayer().getName()));
		message.addExtra(clickHere);
		message.addExtra(new TextComponent(ChatUtil.TRANSLATE(" &7to accept!")));
		
		target.getMembers().stream().forEach(m -> m.getPlayer().spigot().sendMessage(message));
		sender.getMembers().stream().forEach(m -> m.getPlayer().sendMessage(ChatUtil.TRANSLATE("&7Your party has sent a &b" + ladder.name + " &7duel request to &b" + target.getLeader().getPlayer().getName() + " Party (" + target.getMembers().size() + ")&7.")));
	
		this.expiresAt = System.currentTimeMillis() + (30 * 1000);
	}
	
	public boolean isExpired() {
		return System.currentTimeMillis() >= this.expiresAt;
	}
}
