package club.veluxpvp.practice.party.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import club.veluxpvp.practice.party.Party;
import club.veluxpvp.practice.party.PartyRole;
import club.veluxpvp.practice.party.pvpclass.HCFClassType;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.Preconditions;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class PartyClassCommand extends PartyCommand {

	@Command(name = "party.class", aliases = {"p.class", "faction.class", "f.class", "team.class", "t.class", "clan.class"}, partyOnly = true)
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		Player player = cmd.getPlayer();
		Party party = pm.getPlayerParty(player);
		
		if(args.length >= 2) {
			Player target = Bukkit.getPlayer(args[0]);
			HCFClassType Class = HCFClassType.getByName(args[1]);
			
			if(party.getMember(player).getRole() != PartyRole.LEADER) {
				player.sendMessage(ChatUtil.TRANSLATE("&cYou must be the party leader!"));
				return;
			}
			
			if(target == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cPlayer \"" + args[0] + "\" not found!"));
				return;
			}
			
			if(party.getMember(target) == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&c" + target.getName() + " is not in your party!"));
				return;
			}
			
			if(Class == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cHCF Class \"" + args[1] + "\" not found!"));
				return;
			}
			
			if(!Preconditions.canSetClass(target, Class)) {
				player.sendMessage(ChatUtil.TRANSLATE("&cYou can't set this class to " + target.getName() + " because your party has reached the class limit!"));
				return;
			}
			
			party.getMember(player).setHcfClass(Class);
			party.getMembers().stream().forEach(m -> m.getPlayer().sendMessage(ChatUtil.TRANSLATE("&b" + target.getName() + "&7's HCF class has been set to " + Class.getColor() + Class.name + "&7.")));
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /party class <player> <class>"));
		}
	}
}
