package club.veluxpvp.practice.party.command;

import org.bukkit.entity.Player;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.party.PartyManager;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.Command;
import club.veluxpvp.practice.utilities.commandframework.CommandArgs;

public class PartyCommand {

	protected PartyManager pm;
	
	public PartyCommand() {
		this.pm = Practice.getInstance().getPartyManager();
		Practice.getInstance().getCommandFramework().registerCommands(this);
	}
	
	@Command(name = "party", aliases = {"p", "faction", "f", "team", "t", "clan"}, playersOnly = true)
	public void execute(CommandArgs cmd) {
		sendHelpMessage(cmd.getPlayer());
	}
	
	private void sendHelpMessage(Player player) {
		player.sendMessage(ChatUtil.LINE());
		player.sendMessage(ChatUtil.TRANSLATE("&b&lParty Commands"));
		player.sendMessage(ChatUtil.LINE());
		player.sendMessage(ChatUtil.TRANSLATE("&bMember Commands:"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/party create &7- &bCreate a party"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/party join <player> &7- &bJoin in a party"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/party info <player> &7- &bShow info about a party"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/party leave &7- &bLeave your party"));
		player.sendMessage(ChatUtil.TRANSLATE("&bLeader Commands:"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/party disband &7- &bDisband your party"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/party invite <player> &7- &bInvite a player to your party"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/party accept <player> &7- &bAccept a party's duel invite"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/party kick <player> &7- &bKick a player from your party"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/party <open|close> &7- &bMake your party public/private"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/party leader <player> &7- &bSet a new party leader"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/party slots <slots> &7- &bSet the party max slots"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &f/party class <player> <class> &7- &bSet a player HCF class"));
		player.sendMessage(" ");
		player.sendMessage(ChatUtil.TRANSLATE("&b&oTip&7&o: Prefix your message with &b&o@ &7&oto chat with your party mates."));
		player.sendMessage(ChatUtil.LINE());
	}
}
