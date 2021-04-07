package club.veluxpvp.practice.duel;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class DuelManager {

	public static Map<UUID, Duel> makingDuel = Maps.newConcurrentMap();
	public static Set<Duel> duels = Sets.newHashSet();
	
	public static boolean hasSentDuel(Player sender, Player target) {
		for(Duel d : duels) {
			if(d.getSender() == sender && d.getTarget() == target && !d.isExpired()) return true;
		}
		
		return false;
	}
	
	public static Duel getMakingDuel(Player player) {
		if(makingDuel.containsKey(player.getUniqueId())) return makingDuel.get(player.getUniqueId());
		
		return null;
	}
	
	public static void sendDuel(Player player) {
		Duel duel = getMakingDuel(player);
		
		if(duel == null) return;
		
		duel.send();
		duels.add(duel);
		makingDuel.remove(player.getUniqueId());
	}
}
