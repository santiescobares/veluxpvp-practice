package club.veluxpvp.practice.utilities;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.match.HealingType;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.match.PostMatchPlayer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class MatchUtil {

	public static String getTeamPlayerNames(List<Player> players, String playerColor) {
		String names = "";

		for(int i = 0; i < players.size(); i++) {
			if(i != (players.size() - 1)) {
				names += players.get(i).getName() + "&7, " + playerColor;
			} else {
				names += players.get(i).getName();
			}
		}

		return names;
	}
	
	public static TextComponent getPostMatchPlayerNames(Match match, List<Player> players, String playerColor) {
		TextComponent message = new TextComponent(ChatUtil.TRANSLATE(playerColor));
		
		for(int i = 0; i < players.size(); i++) {
			Player player = players.get(i);
			
			TextComponent tc = new TextComponent(ChatUtil.TRANSLATE(playerColor + player.getName()));
			
			int totalHits = match.getTotalHits().get(player.getUniqueId());
			int longestCombo = match.getLongestCombo().get(player.getUniqueId());
			
			if(match.getHealingType() == HealingType.HEALTH_POTION) {
				int potsLeft = PlayerUtil.getHealingLeft(match.getPostMatchPlayers().getOrDefault(player.getUniqueId(), new PostMatchPlayer(player, match.getHealingType(), 0 ,0)).getContents(), HealingType.HEALTH_POTION);
				double missedPotsDouble = match.getMissedPots().getOrDefault(player.getUniqueId(), 0);
				
				tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatUtil.TRANSLATE("&b" + player.getName() + "'s Statistics\n\n&7* &fTotal Hits&7: &b" + totalHits + "\n&7* &fLongest Combo&7: &b" + longestCombo + "\n\n&bHealing:\n&7* &fPots Left&7: &b" + potsLeft + "\n&7* &fMissed Pots&7: &b" + ((int) missedPotsDouble) + "\n\n&aClick to view inventory")).create()));
			} else {
				if(match.getHealingType() == HealingType.NONE) {
					tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatUtil.TRANSLATE("&b" + player.getName() + "'s Statistics\n\n&7* &fTotal Hits&7: &b" + totalHits + "\n&7* &fLongest Combo&7: &b" + longestCombo + "\n\n&aClick to view inventory")).create()));
				} else {
					int healingLeft = PlayerUtil.getHealingLeft(match.getPostMatchPlayers().getOrDefault(player.getUniqueId(), new PostMatchPlayer(player, match.getHealingType(), 0 ,0)).getContents(), match.getHealingType());
					String healingString = match.getHealingType() == HealingType.GOLDEN_APPLE ? "Golden Apples" : match.getHealingType() == HealingType.GAPPLE ? "GApples" : "Soups";
					
					tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatUtil.TRANSLATE("&b" + player.getName() + "'s Statistics\n\n&7* &fTotal Hits&7: &b" + totalHits + "\n&7* &fLongest Combo&7: &b" + longestCombo + "\n\n&bHealing:\n&7* &f" + healingString + " Left&7: &b" + healingLeft + "\n\n&aClick to view inventory")).create()));
				}
			}
			
			tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/_ " + player.getName()));
			
			message.addExtra(tc);
		
			if(i != (players.size() - 1)) {
				message.addExtra(new TextComponent(ChatUtil.TRANSLATE("&7, " + playerColor)));
			}
		}
		
		return message;
	}
	
	public static TextComponent getSpectatorsNames(Match match) {
		TextComponent message = new TextComponent(ChatUtil.TRANSLATE("&f"));
		List<Player> spectators = match.getPlayers().stream().filter(p -> match.getSpectators().contains(p) && match.getPlayerTeam(p) == null).collect(Collectors.toList());
		
		for(int i = 0; i < spectators.size(); i++) {
			Player player = spectators.get(i);
			TextComponent tc = new TextComponent(ChatUtil.TRANSLATE((i != (spectators.size() - 1) ? player.getName() + "&7, &f" : player.getName())));
			
			if(i == 2 && spectators.size() > 3) {
				tc.addExtra(new TextComponent(ChatUtil.TRANSLATE("&f(and +" + (spectators.size() - 3) + ")")));
				message.addExtra(tc);
				break;
			}
			
			message.addExtra(tc);
		}
		
		String component = "&bSpectators\n\n";
		
		for(int x = 0; x < spectators.size(); x++) {
			if(x != (spectators.size() - 1)) {
				component += "&7" + spectators.get(x).getName() + "\n";
			} else {
				component += "&7" + spectators.get(x).getName();
			}
		}
		
		message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatUtil.TRANSLATE(component)).create()));
		
		return message;
	}
}
