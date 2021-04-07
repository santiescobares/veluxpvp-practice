package club.veluxpvp.practice.party;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.party.pvpclass.HCFClassType;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PartyMember {

	private Player player;
	private PartyRole role;
	private HCFClassType hcfClass;
	
	public PartyMember(Player player, PartyRole role) {
		this.player = player;
		this.role = role;
		this.hcfClass = HCFClassType.DIAMOND;
	}
}
