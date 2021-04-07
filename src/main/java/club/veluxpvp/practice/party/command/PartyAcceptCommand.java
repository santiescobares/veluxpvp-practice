package club.veluxpvp.practice.party.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.party.Party;
import club.veluxpvp.practice.party.PartyDuel;
import club.veluxpvp.practice.party.PartyManager;
import club.veluxpvp.practice.party.PartyMember;
import club.veluxpvp.practice.party.PartyRole;
import club.veluxpvp.practice.party.pvpclass.HCFClassType;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.Preconditions;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class PartyAcceptCommand extends PartyCommand {

	@Command(name = "party.accept", aliases = {"p.accept", "faction.accept", "f.accept", "team.accept", "t.accept", "clan.accept"}, partyOnly = true)
	public void execute(CommandArgs cmd) {
		String[] args = cmd.getArgs();
		Player player = cmd.getPlayer();
		Party party = pm.getPlayerParty(player);
		
		if(party.getMember(player).getRole() != PartyRole.LEADER) {
			player.sendMessage(ChatUtil.TRANSLATE("&cYou must be the party leader!"));
			return;
		}
		
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
			
			if(Practice.getInstance().getTournamentManager().getActiveTournament() != null && Practice.getInstance().getTournamentManager().getActiveTournament().isInTournament(targetParty)) {
				player.sendMessage(ChatUtil.TRANSLATE("&c" + targetParty.getLeader().getPlayer().getName() + "'s party is in a tournament!"));
				return;
			}
			
			if(!PartyManager.hasSentPartyDuel(targetParty, party)) {
				player.sendMessage(ChatUtil.TRANSLATE("&cYour party didn't receive a duel request of " + targetParty.getLeader().getPlayer().getName() + "'s party!"));
				return;
			}
			
			PartyDuel pd = PartyManager.partyDuels.stream().filter(p -> p.getSender().equals(targetParty) && p.getTarget().equals(party) && !p.isExpired()).findFirst().orElse(null);
			
			if(pd == null) {
				player.sendMessage(ChatUtil.TRANSLATE("&cYour party didn't receive a duel request of " + targetParty.getLeader().getPlayer().getName() + "'s party!"));
				return;
			}
			
			if(pd.getLadder() == Ladder.HCT_NO_DEBUFF || pd.getLadder() == Ladder.HCT_DEBUFF) {
				if(party.getMembers().size() < 3) {
					player.sendMessage(ChatUtil.TRANSLATE("&cYour party requires at least 3 members to accept a HCF TeamFight match!"));
					return;
				}
				
				HCFClassType[] limitedClasses = {HCFClassType.BARD, HCFClassType.ROGUE, HCFClassType.ARCHER};
				
				for(int i = 0; i < limitedClasses.length; i++) {
					if(Preconditions.isClassLimitExceded(party, limitedClasses[i])) {
						player.sendMessage(ChatUtil.TRANSLATE("&cYour party has reached the limit of " + limitedClasses[i].name + " HCF classes! Please reorganize your roster."));
						return;
					}
				}
			}
			
			Match match = new Match(pd.getArena(), pd.getLadder(), false);
			
			party.getMembers().stream().map(PartyMember::getPlayer).forEach(p -> match.getAlivePlayers().add(p));
			party.getMembers().stream().map(PartyMember::getPlayer).forEach(p -> match.getTeam1().getPlayers().add(p));
			targetParty.getMembers().stream().map(PartyMember::getPlayer).forEach(p -> match.getAlivePlayers().add(p));
			targetParty.getMembers().stream().map(PartyMember::getPlayer).forEach(p -> match.getTeam2().getPlayers().add(p));
			
			match.startCountdown();
			PartyManager.remove(party, targetParty);
		} else {
			player.sendMessage(ChatUtil.TRANSLATE("&cUsage: /party accept <player>"));
		}
	}
}
