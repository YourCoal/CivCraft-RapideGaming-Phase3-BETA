package com.civcraft.main;

import java.io.IOException;
import java.sql.SQLException;

import moblib.moblib.MobLib;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import pvptimer.PvPListener;
import pvptimer.PvPTimer;

import com.civcraft.anticheat.ACManager;
import com.civcraft.command.AcceptCommand;
import com.civcraft.command.BuildCommand;
import com.civcraft.command.DenyCommand;
import com.civcraft.command.EconCommand;
import com.civcraft.command.HereCommand;
import com.civcraft.command.KillCommand;
import com.civcraft.command.PayCommand;
import com.civcraft.command.SelectCommand;
import com.civcraft.command.admin.AdminCommand;
import com.civcraft.command.camp.CampCommand;
import com.civcraft.command.civ.CivChatCommand;
import com.civcraft.command.civ.CivCommand;
import com.civcraft.command.debug.DebugCommand;
import com.civcraft.command.market.MarketCommand;
import com.civcraft.command.plot.PlotCommand;
import com.civcraft.command.resident.ResidentCommand;
import com.civcraft.command.town.TownChatCommand;
import com.civcraft.command.town.TownCommand;
import com.civcraft.config.CivSettings;
import com.civcraft.database.SQL;
import com.civcraft.database.SQLUpdate;
import com.civcraft.endgame.EndConditionNotificationTask;
import com.civcraft.event.EventTimerTask;
import com.civcraft.exception.CivException;
import com.civcraft.exception.InvalidConfiguration;
import com.civcraft.fishing.FishingListener;
import com.civcraft.listener.BlockListener;
import com.civcraft.listener.BonusGoodieManager;
import com.civcraft.listener.ChatListener;
import com.civcraft.listener.CustomItemManager;
import com.civcraft.listener.DebugListener;
import com.civcraft.listener.DisableXPListener;
import com.civcraft.listener.HeroChatListener;
import com.civcraft.listener.MarkerPlacementManager;
import com.civcraft.listener.PlayerListener;
import com.civcraft.lorestorage.LoreCraftableMaterialListener;
import com.civcraft.lorestorage.LoreGuiItemListener;
import com.civcraft.mobs.MobSpawner;
import com.civcraft.mobs.listeners.MobListener;
import com.civcraft.mobs.timers.MobSpawnerTimer;
import com.civcraft.nocheat.NoCheatPlusSurvialFlyHandler;
import com.civcraft.populators.TradeGoodPopulator;
import com.civcraft.randomevents.RandomEventSweeper;
import com.civcraft.sessiondb.SessionDBAsyncTimer;
import com.civcraft.siege.bronze.BronzeCannonListener;
import com.civcraft.siege.iron.IronCannonListener;
import com.civcraft.siege.steel.SteelCannonListener;
import com.civcraft.siege.titanium.TitaniumCannonListener;
import com.civcraft.structure.Farm;
import com.civcraft.structure.farm.FarmGrowthSyncTask;
import com.civcraft.structure.farm.FarmPreCachePopulateTimer;
import com.civcraft.structurevalidation.StructureValidationChecker;
import com.civcraft.structurevalidation.StructureValidationPunisher;
import com.civcraft.threading.TaskMaster;
import com.civcraft.threading.sync.SyncBuildUpdateTask;
import com.civcraft.threading.sync.SyncGetChestInventory;
import com.civcraft.threading.sync.SyncGrowTask;
import com.civcraft.threading.sync.SyncLoadChunk;
import com.civcraft.threading.sync.SyncUpdateChunks;
import com.civcraft.threading.sync.SyncUpdateInventory;
import com.civcraft.threading.tasks.ArrowProjectileTask;
import com.civcraft.threading.tasks.ProjectileComponentTimer;
import com.civcraft.threading.tasks.ScoutTowerTask;
import com.civcraft.threading.timers.AnnouncementTimer;
import com.civcraft.threading.timers.BeakerTimer;
import com.civcraft.threading.timers.ChangeGovernmentTimer;
import com.civcraft.threading.timers.PlayerLocationCacheUpdate;
import com.civcraft.threading.timers.PlayerProximityComponentTimer;
import com.civcraft.threading.timers.RandomStructureTimer;
import com.civcraft.threading.timers.ReduceExposureTimer;
import com.civcraft.threading.timers.RegenTimer;
import com.civcraft.threading.timers.UnitTrainTimer;
import com.civcraft.threading.timers.UpdateEventTimer;
import com.civcraft.threading.timers.WindmillTimer;
import com.civcraft.util.BukkitObjects;
import com.civcraft.util.ChunkCoord;
import com.civcraft.util.TimeTools;
import com.civcraft.war.WarListener;
import com.global.scores.CalculateScoreTimer;
import com.sls.SLSManager;

public final class CivCraft extends JavaPlugin {
	
	private boolean isError = false;
	private static JavaPlugin plugin;
	
	private void startTimers() {
		TaskMaster.asyncTask("SQLUpdate", new SQLUpdate(), 0);
		
		TaskMaster.syncTimer(SyncBuildUpdateTask.class.getName(), new SyncBuildUpdateTask(), 0, 1);
		TaskMaster.syncTimer(SyncUpdateChunks.class.getName(), new SyncUpdateChunks(), 0, TimeTools.toTicks(1));
		TaskMaster.syncTimer(SyncLoadChunk.class.getName(), new SyncLoadChunk(), 0, 1);
		TaskMaster.syncTimer(SyncGetChestInventory.class.getName(), new SyncGetChestInventory(), 0, 1);
		TaskMaster.syncTimer(SyncUpdateInventory.class.getName(), new SyncUpdateInventory(), 0, 1);
		TaskMaster.syncTimer(SyncGrowTask.class.getName(), new SyncGrowTask(), 0, 1);
		TaskMaster.syncTimer(PlayerLocationCacheUpdate.class.getName(), new PlayerLocationCacheUpdate(), 0, 10);
		
		TaskMaster.asyncTimer("RandomEventSweeper", new RandomEventSweeper(), 0, TimeTools.toTicks(10));
		TaskMaster.asyncTimer("UpdateEventTimer", new UpdateEventTimer(), TimeTools.toTicks(1));
		TaskMaster.asyncTimer("RegenTimer", new RegenTimer(), TimeTools.toTicks(5));
		TaskMaster.asyncTimer("BeakerTimer", new BeakerTimer(60), TimeTools.toTicks(60));
		TaskMaster.syncTimer("UnitTrainTimer", new UnitTrainTimer(), TimeTools.toTicks(1));
		TaskMaster.asyncTimer("ReduceExposureTimer", new ReduceExposureTimer(), 0, TimeTools.toTicks(5));
		try {
			double arrow_firerate = CivSettings.getDouble(CivSettings.warConfig, "arrow_tower.fire_rate");
			TaskMaster.syncTimer("arrowTower", new ProjectileComponentTimer(), (int)(arrow_firerate*20));	
			TaskMaster.asyncTimer("ScoutTowerTask", new ScoutTowerTask(), TimeTools.toTicks(1));
		} catch (InvalidConfiguration e) {
			e.printStackTrace();
			return;
		}
		TaskMaster.syncTimer("arrowhomingtask", new ArrowProjectileTask(), 5);
		TaskMaster.syncTimer("FarmCropCache", new FarmPreCachePopulateTimer(), TimeTools.toTicks(30));
		TaskMaster.asyncTimer("FarmGrowthTimer", new FarmGrowthSyncTask(), TimeTools.toTicks(Farm.GROW_RATE));
		TaskMaster.asyncTimer("announcer", new AnnouncementTimer("tips.txt"), 0, TimeTools.toTicks(30*30));
		TaskMaster.asyncTimer("ChangeGovernmentTimer", new ChangeGovernmentTimer(), TimeTools.toTicks(59));
		TaskMaster.asyncTimer("CalculateScoreTimer", new CalculateScoreTimer(), 0, TimeTools.toTicks(10));
		TaskMaster.asyncTimer(PlayerProximityComponentTimer.class.getName(), new PlayerProximityComponentTimer(), 
				TimeTools.toTicks(1));
		TaskMaster.asyncTimer(EventTimerTask.class.getName(), new EventTimerTask(), TimeTools.toTicks(5));
		
//		if (PlatinumManager.isEnabled()) {
//			TaskMaster.asyncTimer(PlatinumManager.class.getName(), new PlatinumManager(), TimeTools.toTicks(5));
//		}
		
		TaskMaster.asyncTimer("RandomStructureTimer", new RandomStructureTimer(), TimeTools.toTicks(15));
		TaskMaster.syncTimer("WindmillTimer", new WindmillTimer(), TimeTools.toTicks(15));
		TaskMaster.asyncTimer("EndGameNotification", new EndConditionNotificationTask(), TimeTools.toTicks(3600));
		TaskMaster.asyncTask(new StructureValidationChecker(), TimeTools.toTicks(120));
		TaskMaster.asyncTimer("StructureValidationPunisher", new StructureValidationPunisher(), TimeTools.toTicks(3600));
		TaskMaster.asyncTimer("SessionDBAsyncTimer", new SessionDBAsyncTimer(), 10);
		
		TaskMaster.asyncTimer("pvptimer", new PvPTimer(), TimeTools.toTicks(30));
		TaskMaster.syncTimer("MobSpawner", new MobSpawnerTimer(), TimeTools.toTicks(15));
	}
	
	private void registerEvents() {
		final PluginManager pluginManager = getServer().getPluginManager();
		pluginManager.registerEvents(new BlockListener(), this);
		pluginManager.registerEvents(new ChatListener(), this);
		pluginManager.registerEvents(new BonusGoodieManager(), this);
		pluginManager.registerEvents(new MarkerPlacementManager(), this);
		pluginManager.registerEvents(new CustomItemManager(), this);
		pluginManager.registerEvents(new PlayerListener(), this);		
		pluginManager.registerEvents(new DebugListener(), this);
		pluginManager.registerEvents(new LoreCraftableMaterialListener(), this);
		pluginManager.registerEvents(new LoreGuiItemListener(), this);
		pluginManager.registerEvents(new DisableXPListener(), this);
		pluginManager.registerEvents(new MobListener(), this);
		pluginManager.registerEvents(new IronCannonListener(), this);
		pluginManager.registerEvents(new BronzeCannonListener(), this);
		pluginManager.registerEvents(new SteelCannonListener(), this);
		pluginManager.registerEvents(new TitaniumCannonListener(), this);
		pluginManager.registerEvents(new WarListener(), this);
		pluginManager.registerEvents(new FishingListener(), this);	
		pluginManager.registerEvents(new PvPListener(), this);
		if (hasPlugin("HeroChat")) {
			pluginManager.registerEvents(new HeroChatListener(), this);
		}
	}
	
	private void registerNPCHooks() {
		NoCheatPlusSurvialFlyHandler.init();
	}
	
	@Override
	public void onEnable() {
		setPlugin(this);
		this.saveDefaultConfig();
		CivLog.init(this);
		BukkitObjects.initialize(this);
		BukkitObjects.getWorlds().get(0).getPopulators().add(new TradeGoodPopulator());
		try {
			CivSettings.init(this);
			SQL.initialize();
			SQL.initCivObjectTables();
			ChunkCoord.buildWorldList();
			CivGlobal.loadGlobals();
			ACManager.init();
			try {
				SLSManager.init();
			} catch (CivException e1) {
				e1.printStackTrace();
			} catch (InvalidConfiguration e1) {
				e1.printStackTrace();
			}
		} catch (InvalidConfiguration | SQLException | IOException | InvalidConfigurationException | CivException | ClassNotFoundException e) {
			e.printStackTrace();
			setError(true);
			return;
		}
		
		// Init commands
		getCommand("town").setExecutor(new TownCommand());
		getCommand("resident").setExecutor(new ResidentCommand());
		getCommand("dbg").setExecutor(new DebugCommand());
		getCommand("plot").setExecutor(new PlotCommand());
		getCommand("accept").setExecutor(new AcceptCommand());
		getCommand("deny").setExecutor(new DenyCommand());
		getCommand("civ").setExecutor(new CivCommand());
		getCommand("tc").setExecutor(new TownChatCommand());
		getCommand("cc").setExecutor(new CivChatCommand());
		getCommand("ad").setExecutor(new AdminCommand());
		getCommand("econ").setExecutor(new EconCommand());
		getCommand("pay").setExecutor(new PayCommand());
		getCommand("build").setExecutor(new BuildCommand());
		getCommand("market").setExecutor(new MarketCommand());
		getCommand("select").setExecutor(new SelectCommand());
		getCommand("here").setExecutor(new HereCommand());
		getCommand("camp").setExecutor(new CampCommand());
		getCommand("kill").setExecutor(new KillCommand());
		registerEvents();
		
		if (hasPlugin("NoCheatPlus")) {
			registerNPCHooks();
		} else {
			CivLog.warning("NoCheatPlus not found, not registering NCP hooks. This is fine if you're not using NCP.");
		}
		MobLib.registerAllEntities();
		startTimers();
		MobSpawner.register();
	}
	
	public boolean hasPlugin(String name) {
		Plugin p;
		p = getServer().getPluginManager().getPlugin(name);
		return (p != null);
	}
	
	@Override
	public void onDisable() {
		MobSpawner.despawnAll();
	}
	
	public boolean isError() {
		return isError;
	}
	
	public void setError(boolean isError) {
		this.isError = isError;
	}
	
	public static JavaPlugin getPlugin() {
		return plugin;
	}
	
	public static void setPlugin(JavaPlugin plugin) {
		CivCraft.plugin = plugin;
	}
}
