package club.veluxpvp.practice.party;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.utilities.ChatUtil;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class PartyManager {

	@Getter private Set<Party> parties;
	public static Map<UUID, UUID> partyInvite = Maps.newConcurrentMap();
	public static Map<Party, PartyDuel> makingPartyDuel = Maps.newConcurrentMap();
	public static Set<PartyDuel> partyDuels = Sets.newHashSet();
	
	public PartyManager() {
		this.parties = new HashSet<>();
	}
	
	public boolean isPartyInTournament(Party party) {
		return Practice.getInstance().getTournamentManager().getActiveTournament() != null && Practice.getInstance().getTournamentManager().getActiveTournament().isInTournament(party);
	}
	
	public Party getPlayerParty(Player player) {
		for(Party p : this.parties) {
			PartyMember pm = p.getMember(player);
			
			if(pm == null) continue;
			
			return p;
		}
		
		return null;
	}
	
	public static boolean hasSentInvite(Player player, Player invited) {
		return partyInvite.containsKey(player.getUniqueId()) && partyInvite.containsValue(invited.getUniqueId()) && 
				Practice.getInstance().getPartyManager().getPlayerParty(player) != null && Practice.getInstance().getPartyManager().getPlayerParty(player).getLastInviteSent().get(invited.getUniqueId()) > System.currentTimeMillis();
	}
	
	public static void sendInvite(Player player, Player target) {
		partyInvite.put(player.getUniqueId(), target.getUniqueId());
		
		Practice.getInstance().getPartyManager().getPlayerParty(player).getLastInviteSent().put(target.getUniqueId(), System.currentTimeMillis() + (30 * 1000));
		
		TextComponent tc = new TextComponent(ChatUtil.TRANSLATE("&b" + player.getName() + " &7has invited you to their party! Type &b/party join " + player.getName() + " &7or "));
		TextComponent clickHere = new TextComponent(ChatUtil.TRANSLATE("&aClick here"));
		clickHere.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatUtil.TRANSLATE("&7* &fClick to &ajoin &fthe party!")).create()));
		clickHere.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party join " + player.getName()));
		tc.addExtra(clickHere);
		tc.addExtra(new TextComponent(ChatUtil.TRANSLATE(" &7to join.")));
		
		target.spigot().sendMessage(tc);
		Practice.getInstance().getPartyManager().getPlayerParty(player).getMembers().stream().forEach(m -> m.getPlayer().sendMessage(ChatUtil.TRANSLATE("&b" + target.getName() + " &7has been invited to the party!")));
	}
	
	public static void acceptInvite(Player player, Player invitedParty) {
		partyInvite.remove(invitedParty.getUniqueId(), player.getUniqueId());
		Practice.getInstance().getPartyManager().getPlayerParty(invitedParty).getLastInviteSent().remove(player.getUniqueId());
	}
	
	public static void cancelInvite(Player player) {
		Player target = null;
		
		for(Map.Entry<UUID, UUID> inviteEntry : partyInvite.entrySet()) {
			if(inviteEntry.getValue().equals(player.getUniqueId())) {
				target = Bukkit.getPlayer(inviteEntry.getKey());
				break;
			}
		}
		
		partyInvite.remove(target.getUniqueId(), player.getUniqueId());
		Practice.getInstance().getPartyManager().getPlayerParty(target).getLastInviteSent().remove(player.getUniqueId());
	}
	
	public static boolean hasSentPartyDuel(Party sender, Party target) {
		for(PartyDuel pd : partyDuels) {
			if(pd.getSender().equals(sender) && pd.getTarget().equals(target) && !pd.isExpired()) return true;
		}
		
		return false;
	}
	
	public static PartyDuel getMakingDuel(Party party) {
		if(makingPartyDuel.containsKey(party)) return makingPartyDuel.get(party);
		
		return null;
	}
	
	public static void sendPartyDuel(Party sender) {
		PartyDuel pd = getMakingDuel(sender);
		
		if(pd == null) return;
		
		pd.send();
		partyDuels.add(pd);
		makingPartyDuel.remove(sender);
	}
	
	public static void remove(Party party, Party target) {
		PartyDuel pd = getMakingDuel(party);
		
		if(pd != null && pd.getTarget() == target) {
			partyDuels.remove(pd);
		}
		
		makingPartyDuel.remove(party, target);
	}
}
