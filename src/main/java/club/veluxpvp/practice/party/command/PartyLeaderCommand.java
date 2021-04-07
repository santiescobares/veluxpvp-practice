package club.veluxpvp.practice.party.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import club.veluxpvp.practice.item.ItemManager;
import club.veluxpvp.practice.party.Party;
import club.veluxpvp.practice.party.PartyRole;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.PlayerUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class PartyLeaderCommand extends PartyCommand {

	@Command(name = "party.leader", aliases = {"p.leader", "faction.leader", "f.leader", "team.leader", "t.leader", "clan.leader"}, partyOnly = true)
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
			
			if(target == player) {
				player.sendMessage(ChatUtil.TRANSLATE("&cYou are already the party leader!"));
				return;
			}
			
			if(party.getMember(target) == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&c" + target.getName() + " is not in your party!"));
				return;
			}
			
			party.getMember(player).setRole(PartyRole.MEMBER);
			party.getMember(target).setRole(PartyRole.LEADER);
			
			if(PlayerUtil.isInLobby(player)) {
				PlayerUtil.reset(player, player.getGameMode(), false);
				ItemManager.loadPartyItems(player);
			}
			
			if(PlayerUtil.isInLobby(target)) {
				PlayerUtil.reset(target, player.getGameMode(), false);
				ItemManager.loadPartyItems(target);
			}
			
			party.getMembers().stream().forEach(m -> m.getPlayer().sendMessage(ChatUtil.TRANSLATE("&b" + player.getName() + " &7has transfered the party leadership to &b" + target.getName() + "&7.")));
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /party leader <player>"));
		}
	}
}
