package club.veluxpvp.practice.party.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import club.veluxpvp.practice.party.Party;
import club.veluxpvp.practice.party.PartyRole;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class PartyKickCommand extends PartyCommand {

	@Command(name = "party.kick", aliases = {"p.kick", "faction.kick", "f.kick", "team.kick", "t.kick", "clan.kick"}, partyOnly = true)
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		Player player = cmd.getPlayer();
		Party party = pm.getPlayerParty(player);
		
		if(args.length >= 1) {
			Player target = Bukkit.getPlayer(args[0]);
			
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
			
			party.removeMember(target, Party.QuitReason.KICKED);
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /party kick <player>"));
		}
	}
}
