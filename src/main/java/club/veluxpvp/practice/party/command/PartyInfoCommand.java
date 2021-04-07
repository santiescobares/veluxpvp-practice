package club.veluxpvp.practice.party.command;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import club.veluxpvp.practice.party.Party;
import club.veluxpvp.practice.party.PartyMember;
import club.veluxpvp.practice.party.PartyRole;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class PartyInfoCommand extends PartyCommand {

	@Command(name = "party.info", aliases = {"p.info", "faction.info", "f.info", "team.info", "t.info", "clan.info", 
			"party.show", "p.show", "faction.show", "f.show", "team.show", "t.show", "clan.show", "party.who", "p.who", 
			"faction.who", "f.who", "team.who", "t.who", "clan.who", "party.i", "p.i", "faction.i", "f.i", "team.i", "t.i", 
			"clan.i", "party.information", "p.information", "faction.information", "f.information", "team.information", 
			"t.information", "clan.information"})
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		Player player = cmd.getPlayer();
		
		if(args.length >= 1) {
			Player target = Bukkit.getPlayer(args[0]);
			
			if(target == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cPlayer \"" + args[0] + "\" not found!"));
				return;
			}
			
			Party targetParty = pm.getPlayerParty(target);
			
			if(targetParty == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&c" + target.getName() + " is not in a party!"));
				return;
			}
			
			sendPartyInfo(player, targetParty);
		} else {
			Party party = pm.getPlayerParty(player);
			
			if(party == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /party info <player>"));
			} else {
				sendPartyInfo(player, party);
			}
		}
	}
	
	public static void sendPartyInfo(Player player, Party party) {
		player.sendMessage(ChatUtil.LINE());
		player.sendMessage(ChatUtil.TRANSLATE("&b&l" + party.getLeader().getPlayer().getName() + "'s Party Information &7(" + party.getMembers().size() + "/" + party.getSlots() + ")"));
		player.sendMessage(ChatUtil.LINE());
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &fLeader&7: &b" + party.getLeader().getPlayer().getName()));
		
		List<PartyMember> members = party.getMembers().stream().filter(m -> m.getRole() == PartyRole.MEMBER).collect(Collectors.toList());
		String membersNames = "";
		
		for(int i = 0; i < members.size(); i++) {
			membersNames += members.get(i).getPlayer().getName() + (i != (members.size() - 1) ? " " : "");
		}
		
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &fMembers (" + members.size() + ")&7: &b" + (members.size() > 0 ? membersNames : "Nobody")));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &fState&7: " + (party.isOpen() ? "&aOpen" : "&cClosed")));
		player.sendMessage(ChatUtil.LINE());
	}
}
