package club.veluxpvp.practice;

import java.util.ConcurrentModificationException;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Lists;

import club.veluxpvp.practice.arena.ArenaManager;
import club.veluxpvp.practice.arena.Ladder;
import club.veluxpvp.practice.arena.command.*;
import club.veluxpvp.practice.arena.listener.ArenaBoundsListener;
import club.veluxpvp.practice.arena.listener.ArenaCheckpointsListener;
import club.veluxpvp.practice.arena.listener.ArenaClaimListener;
import club.veluxpvp.practice.bukkitevent.ArmorListener;
import club.veluxpvp.practice.command.*;
import club.veluxpvp.practice.command.toggle.*;
import club.veluxpvp.practice.config.ConfigurationManager;
import club.veluxpvp.practice.database.MySQLManager;
import club.veluxpvp.practice.duel.command.AcceptCommand;
import club.veluxpvp.practice.duel.command.DuelCommand;
import club.veluxpvp.practice.duel.listener.DuelMenuListener;
import club.veluxpvp.practice.elo.command.EloCommand;
import club.veluxpvp.practice.elo.command.ResetEloCommand;
import club.veluxpvp.practice.elo.command.SetEloCommand;
import club.veluxpvp.practice.event.PracticeEventManager;
import club.veluxpvp.practice.event.listener.SumoEventListener;
import club.veluxpvp.practice.kit.KitManager;
import club.veluxpvp.practice.kit.command.KitClearKitsCommand;
import club.veluxpvp.practice.kit.command.KitClearPlayerKitsCommand;
import club.veluxpvp.practice.kit.command.KitCommand;
import club.veluxpvp.practice.kit.command.KitLoadCommand;
import club.veluxpvp.practice.kit.command.KitSetDefaultInvCommand;
import club.veluxpvp.practice.kit.listener.KitEditorListener;
import club.veluxpvp.practice.kit.menu.listener.EditKitsMenuListener;
import club.veluxpvp.practice.kit.menu.listener.KitEditorMenuListener;
import club.veluxpvp.practice.listener.GeneralListener;
import club.veluxpvp.practice.match.Match;
import club.veluxpvp.practice.match.MatchEndReason;
import club.veluxpvp.practice.match.MatchManager;
import club.veluxpvp.practice.match.listener.*;
import club.veluxpvp.practice.menu.MenuListener;
import club.veluxpvp.practice.nametag.NametagManager;
import club.veluxpvp.practice.party.PartyManager;
import club.veluxpvp.practice.party.command.*;
import club.veluxpvp.practice.party.listener.HCFRosterMenuListener;
import club.veluxpvp.practice.party.listener.OtherPartiesMenuListener;
import club.veluxpvp.practice.party.listener.PartyListener;
import club.veluxpvp.practice.party.listener.SelectLadderMenuListener;
import club.veluxpvp.practice.party.listener.StartFightMenuListener;
import club.veluxpvp.practice.party.pvpclass.HCFClassListener;
import club.veluxpvp.practice.party.pvpclass.HCFClassManager;
import club.veluxpvp.practice.profile.ProfileManager;
import club.veluxpvp.practice.queue.QueueManager;
import club.veluxpvp.practice.queue.listener.QueueListener;
import club.veluxpvp.practice.queue.listener.RankedQueueMenuListener;
import club.veluxpvp.practice.queue.listener.UnrankedQueueMenuListener;
import club.veluxpvp.practice.scoreboard.ScoreboardManager;
import club.veluxpvp.practice.scoreboard.provider.Assemble;
import club.veluxpvp.practice.setting.command.SettingsCommand;
import club.veluxpvp.practice.setting.listener.SettingsMenuListener;
import club.veluxpvp.practice.staffmode.StaffModeManager;
import club.veluxpvp.practice.staffmode.command.FreezeCommand;
import club.veluxpvp.practice.staffmode.command.StaffModeCommand;
import club.veluxpvp.practice.staffmode.command.VanishCommand;
import club.veluxpvp.practice.staffmode.listener.StaffModeListener;
import club.veluxpvp.practice.tablist.TablistManager;
import club.veluxpvp.practice.tablist.provider.EightTab;
import club.veluxpvp.practice.tournament.TournamentManager;
import club.veluxpvp.practice.tournament.command.*;
import club.veluxpvp.practice.tournament.listener.TournamentListener;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.commandframework.CommandFramework;
import lombok.Getter;

@Getter
public class Practice extends JavaPlugin {

	@Getter private static Practice instance;
	private int announcementID = 0;
	
	private CommandFramework commandFramework;
	
	private ConfigurationManager configurationManager;
	private MySQLManager mySQLManager;
	private ArenaManager arenaManager;
	private MatchManager matchManager;
	private ProfileManager profileManager;
	private KitManager kitManager;
	private QueueManager queueManager;
	private PartyManager partyManager;
	private TournamentManager tournamentManager;
	private PracticeEventManager eventManager;
	private NametagManager nametagManager;
	private StaffModeManager staffModeManager;
	private HCFClassManager hcfClassManager;
	private Assemble scoreboardManager;
	private EightTab tablistManager;
	
	@Override
	public void onEnable() {
		instance = this;

		for(World w : Bukkit.getWorlds()) {
			w.getEntities().stream().filter(e -> !(e instanceof Player)).forEach(e -> e.remove());
			w.setTime(6000);
			w.setDifficulty(Difficulty.NORMAL);
			w.setGameRuleValue("doDaylightCycle", "false");
			w.setGameRuleValue("doMobSpawning", "false");
		}
		
		Bukkit.getConsoleSender().sendMessage(ChatUtil.TRANSLATE("&9================================"));
		Bukkit.getConsoleSender().sendMessage(ChatUtil.TRANSLATE("&bPractice &f- &av" + this.getDescription().getVersion()));
		Bukkit.getConsoleSender().sendMessage(" ");
		Bukkit.getConsoleSender().sendMessage(ChatUtil.TRANSLATE("&aConnecting to the database..."));
		
		registerManagers();
		registerListeners();
		registerCommands();
		
		Bukkit.getConsoleSender().sendMessage(ChatUtil.TRANSLATE("&7* &a" + this.arenaManager.getArenas().size() + " &farenas loaded!"));
		Bukkit.getConsoleSender().sendMessage(ChatUtil.TRANSLATE("&7* &a" + this.profileManager.getProfiles().size() + " &fprofiles loaded!"));
		Bukkit.getConsoleSender().sendMessage(ChatUtil.TRANSLATE("&9================================"));
	
		runOnEnableTasks();
	}
	
	@Override
	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage(ChatUtil.TRANSLATE("&9================================"));
		Bukkit.getConsoleSender().sendMessage(ChatUtil.TRANSLATE("&bPractice &f- &av" + this.getDescription().getVersion()));
		Bukkit.getConsoleSender().sendMessage(" ");
		
		this.profileManager.saveProfiles();
		Bukkit.getConsoleSender().sendMessage(ChatUtil.TRANSLATE("&7* &a" + this.profileManager.getProfiles().size() + " &fprofiles saved!"));
		this.arenaManager.saveArenas();
		Bukkit.getConsoleSender().sendMessage(ChatUtil.TRANSLATE("&7* &a" + this.arenaManager.getArenas().size() + " &farenas saved!"));
		this.kitManager.saveKits();
		Bukkit.getConsoleSender().sendMessage(ChatUtil.TRANSLATE("&7* &a" + this.kitManager.getKits().size() + " &fkits saved!"));
		
		try {
			this.matchManager.getMatches().stream().forEach(m -> m.finish(MatchEndReason.CANCELLED));
		} catch(ConcurrentModificationException ignored) {}
		
		this.matchManager.saveRankedMatchesLogs();
		Bukkit.getConsoleSender().sendMessage(ChatUtil.TRANSLATE("&7* &a" + this.matchManager.getRankedMatchesLogs().size() + " &franked matches logs saved!"));
		this.scoreboardManager.cleanup();
		this.staffModeManager.onDisable();
		this.tablistManager.onDisable();
		
		Bukkit.getConsoleSender().sendMessage(ChatUtil.TRANSLATE("&9================================"));
	}
	
	// Managers
	private void registerManagers() {
		this.configurationManager = new ConfigurationManager();
		this.mySQLManager = new MySQLManager();
		this.arenaManager = new ArenaManager();
		this.matchManager = new MatchManager();
		this.profileManager = new ProfileManager();
		this.kitManager = new KitManager();
		this.queueManager = new QueueManager();
		this.partyManager = new PartyManager();
		this.tournamentManager = new TournamentManager();
		this.eventManager = new PracticeEventManager();
		this.nametagManager = new NametagManager();
		this.staffModeManager = new StaffModeManager();
		this.hcfClassManager = new HCFClassManager();
		this.scoreboardManager = new Assemble(this, new ScoreboardManager());
		this.tablistManager = new EightTab(this, new TablistManager());
		
		Bukkit.getConsoleSender().sendMessage(ChatUtil.TRANSLATE("&7* &fManagers registered!"));
	}
	
	// Listeners
	private void registerListeners() {
		new GeneralListener();
		new ArmorListener(Lists.newArrayList());

		new ArenaBoundsListener();
		new ArenaCheckpointsListener();
		new ArenaClaimListener();
		
		new PartyListener();
		new StartFightMenuListener();
		new SelectLadderMenuListener();
		new HCFRosterMenuListener();
		new OtherPartiesMenuListener();
		
		new QueueListener();
		new UnrankedQueueMenuListener();
		new RankedQueueMenuListener();
		
		new MatchListener();
		new MatchStartingListener();
		new MatchEnderPearlListener();
		new MatchSpectateListener();
		new MatchSumoListener();
		new MatchBuildUHCListener();
		new MatchParkourListener();
		new MatchBridgesListener();
		new MatchSoupListener();
		new MatchHCTListener();
		new MatchRankedListener();
		new MatchFinishListener();
		new MatchKitListener();
		new MatchSpectateMenuListener();
		
		new HCFClassListener();
		
		new MenuListener();
		
		new KitEditorListener();
		new KitEditorMenuListener();
		new EditKitsMenuListener();

		new TournamentListener();
		
		new SumoEventListener();
		
		new SettingsMenuListener();
		
		new DuelMenuListener();
		
		new StaffModeListener();
		
		Bukkit.getConsoleSender().sendMessage(ChatUtil.TRANSLATE("&7* &fListeners registered!"));
	}
	
	// Commands
	private void registerCommands() {
		this.commandFramework = new CommandFramework(this);

		new SetSpawnCommand();
		new SpawnCommand();
		new SetKitEditorSpawnCommand();
		new KitEditorSpawnCommand();
		new _Command();
		new SpectateCommand();
		new LeaderboardCommand();
		new PlayTimeCommand();
		new PingCommand();
		new CollidesCommand();
		new HitDelayCommand();
		
		new StaffModeCommand();
		new VanishCommand();
		new FreezeCommand();
		
		new DuelCommand();
		new AcceptCommand();
		
		new SettingsCommand();
		
		new EloCommand();
		new SetEloCommand();
		new ResetEloCommand();

		new KitCommand();
		new KitClearKitsCommand();
		new KitClearPlayerKitsCommand();
		new KitSetDefaultInvCommand();
		new KitLoadCommand();
		
		new ArenaCommand();
		new ArenaCreateCommand();
		new ArenaDeleteCommand();
		new ArenaCorner1Command();
		new ArenaCorner2Command();
		new ArenaSpectatorsSpawnCommand();
		new ArenaEventsSpawnCommand();
		new ArenaIconCommand();
		new ArenaEnableCommand();
		new ArenaDisableCommand();
		new ArenaBoundsCommand();
		new ArenaRankedCommand();
		new ArenaPartiesCommand();
		new ArenaTournamentsCommand();
		new ArenaAddLadderCommand();
		new ArenaRemoveLadderCommand();
		new ArenaCheckpointsCommand();
		new ArenaClaimCommand();
		new ArenaEnderpearlsCommand();
		new ArenaMaxBuildHeightCommand();
		new ArenaInfoCommand();
		new ArenaListCommand();
		new ArenaSaveAllCommand();
		
		new PartyCommand();
		new PartyCreateCommand();
		new PartyLeaveCommand();
		new PartyDisbandCommand();
		new PartyJoinCommand();
		new PartyInfoCommand();
		new PartyInviteCommand();
		new PartyKickCommand();
		new PartyOpenCommand();
		new PartyCloseCommand();
		new PartyLeaderCommand();
		new PartySlotsCommand();
		new PartyClassCommand();
		new PartyAcceptCommand();
		
		new TournamentCommand();
		new TournamentStartCommand();
		new TournamentForceStartCommand();
		new TournamentJoinCommand();
		new TournamentLeaveCommand();
		new TournamentCancelCommand();
		new TournamentStatusCommand();
		
		new ToggleScoreboardCommand();
		new ToggleSpectatorsCommand();
		new ToggleDuelsCommand();
		new ToggleTournamentMessagesCommand();
		new TogglePingOnScoreboardCommand();
		new ToggleSimilarPingCommand();
		
		Bukkit.getConsoleSender().sendMessage(ChatUtil.TRANSLATE("&7* &fCommands registered!"));
	}
	
	// Tasks
	private void runOnEnableTasks() {
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
			for(Player p : Bukkit.getOnlinePlayers()) {
				Match match = this.matchManager.getPlayerMatch(p);
				
				if(match == null || match.isSpectating(p) || match.getLadder() == Ladder.SUMO || match.getLadder() == 
						Ladder.ARCHER || match.getLadder() == Ladder.SOUP || match.getLadder() == Ladder.COMBO_FLY || 
						match.getLadder() == Ladder.GAPPLE || match.getLadder() == Ladder.BRIDGES || match.getLadder() == 
						Ladder.PARKOUR || match.getLadder() == Ladder.HG) {
					p.setExhaustion(-10);
				}
			}
		}, 5 * 20L, 5 * 20L);
		
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
			long startedAt = System.currentTimeMillis();
			long endedAt;
			
			this.profileManager.saveAsync();
			this.kitManager.saveAsync();
			this.matchManager.saveAsync();
			
			endedAt = System.currentTimeMillis();
			System.out.println("Profiles, Ranked matches logs & kits saved in " + (endedAt - startedAt) + "ms.");
		}, (10 * 60) * 20L, (10 * 60) * 20L);
		
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
			if(this.announcementID == 4) this.announcementID = 0;
			
			Bukkit.broadcastMessage(" ");
			switch(this.announcementID) {
			case 0:
				Bukkit.broadcastMessage(ChatUtil.TRANSLATE("&bJoin our Discord for get notified about server updates, giveaways and more!"));
				Bukkit.broadcastMessage(ChatUtil.TRANSLATE("&f&ndiscord.veluxpvp.club"));
				break;
			case 1:
				Bukkit.broadcastMessage(ChatUtil.TRANSLATE("&bDon't like the knockback? Are potions too slow? Just disconnect."));
				break;
			case 2:
				Bukkit.broadcastMessage(ChatUtil.TRANSLATE("&bFollow us on Twitter to stay up to date about the server, enter giveaways and more!"));
				Bukkit.broadcastMessage(ChatUtil.TRANSLATE("&f&ntwitter.com/VeluxPvPNet"));
				break;
			case 3:
				Bukkit.broadcastMessage(ChatUtil.TRANSLATE("&bDid you see a cheater? Do you need help? Use &f/report &bor &f/request &bfor help."));
				break;
			}
			Bukkit.broadcastMessage(" ");
			
			this.announcementID++;
		}, 300 * 20L, 300 * 20L);
	}
}
