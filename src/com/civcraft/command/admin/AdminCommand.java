package com.civcraft.command.admin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.civcraft.command.CommandBase;
import com.civcraft.command.ReportChestsTask;
import com.civcraft.command.ReportPlayerInventoryTask;
import com.civcraft.config.CivSettings;
import com.civcraft.config.ConfigMaterial;
import com.civcraft.config.ConfigMaterialCategory;
import com.civcraft.config.ConfigUnit;
import com.civcraft.endgame.EndGameCondition;
import com.civcraft.exception.CivException;
import com.civcraft.lorestorage.LoreCraftableMaterial;
import com.civcraft.lorestorage.LoreGuiItem;
import com.civcraft.lorestorage.LoreGuiItemListener;
import com.civcraft.lorestorage.LoreMaterial;
import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivLog;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Civilization;
import com.civcraft.object.Resident;
import com.civcraft.object.Town;
import com.civcraft.sessiondb.SessionEntry;
import com.civcraft.threading.TaskMaster;
import com.civcraft.util.ChunkCoord;
import com.civcraft.util.CivColor;
import com.civcraft.util.ItemManager;

public class AdminCommand extends CommandBase {

	@Override
	public void init() {
		command = "/ad";
		displayName = "Admin";
		
		commands.put("perm", "toggles your permission overrides, if on, ignores all plot permissions.");
		commands.put("sbperm", "Allows breaking of structure blocks");
		commands.put("cbinstantbreak", "Allows instant breaking of control blocks.");

		commands.put("recover", "Manage recovery commands");
		commands.put("server", "shows the name of this server");
		commands.put("spawnunit", "[unit-id] [town] spawn the unit with this id for this town.");

		commands.put("chestreport", "[radius] check in this radius for chests");
		commands.put("playerreport", "shows all player ender chest reports.");
		
		commands.put("civ", "Admin an individual civilization");
		commands.put("town", "Admin a town.");
		commands.put("war", "Manage war settings, turn wars off and on.... etc.");
		commands.put("lag", "Manage lag on the server by disabling expensive tasks.");	
		commands.put("camp", "Shows camp management subcommands.");
		commands.put("chat", "Manage admin chat options, tc, cc, listen etc");
		commands.put("res", "Manage resident options, settown, setcamp etc");
		commands.put("build", "Manage buildings. Demolish/repair wonders etc.");
		commands.put("items", "Opens inventory which allows you to spawn in custom items.");
		commands.put("item", "Does special things to the item in your hand.");
		commands.put("timer", "Manage timers.");
		commands.put("road", "Road management commands");
		commands.put("clearendgame", "[key] [civ] - clears this end game condition for this civ.");
		commands.put("endworld", "Starts the Apocalypse.");
		commands.put("perk", "Admin perk management.");
		commands.put("mob", "Admin mob management.");
		commands.put("sql", "Admin sql management.");
	}
	
	public void sql_cmd() {
		AdminSQLCommand cmd = new AdminSQLCommand();	
		cmd.onCommand(sender, null, "sql", this.stripArgs(args, 1));
	}
	
	public void mob_cmd() {
		AdminMobCommand cmd = new AdminMobCommand();	
		cmd.onCommand(sender, null, "mob", this.stripArgs(args, 1));
	}
	
	public void perk_cmd() {
		AdminPerkCommand cmd = new AdminPerkCommand();	
		cmd.onCommand(sender, null, "perk", this.stripArgs(args, 1));
	}
	
	public void endworld_cmd() {
		CivGlobal.endWorld = !CivGlobal.endWorld;
		if (CivGlobal.endWorld) {			
			CivMessage.sendSuccess(sender, "It's the end of the world as we know it.");
		} else {
			CivMessage.sendSuccess(sender, "I feel fine.");
		}
	}
	
	public void clearendgame_cmd() throws CivException {
		String key = getNamedString(1, "enter key.");
		Civilization civ = getNamedCiv(2);
		
		ArrayList<SessionEntry> entries = CivGlobal.getSessionDB().lookup(key);
		if (entries.size() == 0) {
			throw new CivException("No end games by that key.");
		}
		
		for (SessionEntry entry : entries) {
			if (EndGameCondition.getCivFromSessionData(entry.value) == civ) {
				CivGlobal.getSessionDB().delete(entry.request_id, entry.key);
				CivMessage.sendSuccess(sender, "Deleted for "+civ.getName());
			}
		}		
	}
	
	public void cbinstantbreak_cmd() throws CivException {
		Resident resident = getResident();
		
		resident.setControlBlockInstantBreak(!resident.isControlBlockInstantBreak());
		CivMessage.sendSuccess(sender, "Set control block instant break:"+resident.isControlBlockInstantBreak());
	}
	
	public static Inventory spawnInventory = null; 
	public void items_cmd() throws CivException {
		Player player = getPlayer();
		
		if (spawnInventory == null) {
			//spawnInventory = Bukkit.createInventory(player, LoreGuiItem.MAX_INV_SIZE, "Admin Item Spawn");
			spawnInventory = Bukkit.createInventory(player, 36, "Admin Item Spawn");
			
			/* Build the Category Inventory. */
			for (ConfigMaterialCategory cat : ConfigMaterialCategory.getCategories()) {
				int identifier;
				if (cat.name.contains("Element")) {
					identifier = ItemManager.getId(Material.EXP_BOTTLE);
				} else if (cat.name.contains("Gear Tier 0")) {
					identifier = ItemManager.getId(Material.STONE_SWORD);
				} else if (cat.name.contains("Gear Tier 1")) {
					identifier = ItemManager.getId(Material.IRON_HELMET);
				} else if (cat.name.contains("Gear Tier 2")) {
					identifier = ItemManager.getId(Material.GOLD_CHESTPLATE);
				} else if (cat.name.contains("Gear Tier 3")) {
					identifier = ItemManager.getId(Material.CHAINMAIL_LEGGINGS);
				} else if (cat.name.contains("Gear Tier 4")) {
					identifier = ItemManager.getId(Material.DIAMOND_BOOTS);
				} else if (cat.name.contains("Fish")) {
					identifier = ItemManager.getId(Material.RAW_FISH);
				} else if (cat.name.contains("Dyes")) {
					identifier = ItemManager.getId(Material.BOWL);
				} else if (cat.name.contains("Special")) {
					identifier = ItemManager.getId(Material.BEACON);
				} else if (cat.name.contains("Tier 1 Material")) {
					identifier = ItemManager.getId(Material.IRON_BLOCK);
				} else if (cat.name.contains("Tier 2 Material")) {
					identifier = ItemManager.getId(Material.GOLD_BLOCK);
				} else if (cat.name.contains("Tier 3 Material")) {
					identifier = ItemManager.getId(Material.DIAMOND_BLOCK);
				} else if (cat.name.contains("Tier 4 Material")) {
					identifier = ItemManager.getId(Material.EMERALD_BLOCK);
				} else if (cat.name.contains("Upgrader")) {
					identifier = ItemManager.getId(Material.NETHER_STAR);
				} else {
					identifier = ItemManager.getId(Material.WRITTEN_BOOK);
				}
				
				ItemStack infoRec = LoreGuiItem.build(cat.name, identifier, 0, 
						CivColor.LightBlue+cat.materials.size()+" Items", CivColor.Gold+"<Click To Open>");
						infoRec = LoreGuiItem.setAction(infoRec, "OpenInventory");
						infoRec = LoreGuiItem.setActionData(infoRec, "invType", "showGuiInv");
						infoRec = LoreGuiItem.setActionData(infoRec, "invName", cat.name+" Spawn");
						spawnInventory.addItem(infoRec);
						
				/* Build a new GUI Inventory. */
				Inventory inv = Bukkit.createInventory(player, LoreGuiItem.MAX_INV_SIZE, cat.name+" Spawn");
				for (ConfigMaterial mat : cat.materials.values()) {
					LoreCraftableMaterial craftMat = LoreCraftableMaterial.getCraftMaterialFromId(mat.id);
					ItemStack stack = LoreMaterial.spawn(craftMat);
					stack = LoreGuiItem.asGuiItem(stack);
					stack = LoreGuiItem.setAction(stack, "SpawnItem");
					inv.addItem(stack);
					LoreGuiItemListener.guiInventories.put(inv.getName(), inv);			
				}
			}
			

		}
		
		player.openInventory(spawnInventory);
	}
	
	public void road_cmd() {
		AdminRoadCommand cmd = new AdminRoadCommand();	
		cmd.onCommand(sender, null, "camp", this.stripArgs(args, 1));
	}
	
	public void item_cmd() {
		AdminItemCommand cmd = new AdminItemCommand();	
		cmd.onCommand(sender, null, "camp", this.stripArgs(args, 1));
	}
	
	public void timer_cmd() {
		AdminTimerCommand cmd = new AdminTimerCommand();	
		cmd.onCommand(sender, null, "camp", this.stripArgs(args, 1));	
	}
	
	public void camp_cmd() {
		AdminCampCommand cmd = new AdminCampCommand();	
		cmd.onCommand(sender, null, "camp", this.stripArgs(args, 1));
	}
	
	public void playerreport_cmd() {
	
		LinkedList<OfflinePlayer> offplayers = new LinkedList<OfflinePlayer>();
		for (OfflinePlayer offplayer : Bukkit.getOfflinePlayers()) {
			offplayers.add(offplayer);
		}
		
		CivMessage.sendHeading(sender, "Players with Goodies");
		CivMessage.send(sender, "Processing (this may take a while)");
		TaskMaster.syncTask(new ReportPlayerInventoryTask(sender, offplayers), 0);
	}
	
	public void chestreport_cmd() throws CivException {
		Integer radius = getNamedInteger(1);
		Player player = getPlayer();
		
		LinkedList<ChunkCoord> coords = new LinkedList<ChunkCoord>();
		for (int x = -radius; x < radius; x++) {
			for (int z = -radius; z < radius; z++) {
				ChunkCoord coord = new ChunkCoord(player.getLocation());
				coord.setX(coord.getX() + x); coord.setZ(coord.getZ() + z);
				
				coords.add(coord);
			}
		}
		
		CivMessage.sendHeading(sender, "Chests with Goodies");
		CivMessage.send(sender, "Processing (this may take a while)");
		TaskMaster.syncTask(new ReportChestsTask(sender, coords), 0);	
	}
	
	public void spawnunit_cmd() throws CivException {		
		if (args.length < 2) {
			throw new CivException("Enter a unit id.");
		}
		
		ConfigUnit unit = CivSettings.units.get(args[1]);
		if (unit == null) {
			throw new CivException("No unit called "+args[1]);
		}
		
		Player player = getPlayer();
		Town town = getNamedTown(2);
		
//		if (args.length > 2) {
//			try {
//				player = CivGlobal.getPlayer(args[2]);
//			} catch (CivException e) {
//				throw new CivException("Player "+args[2]+" is not online.");
//			}
//		} else {
//			player = getPlayer();
//		}
		
		Class<?> c;
		try {
			c = Class.forName(unit.class_name);
			Method m = c.getMethod("spawn", Inventory.class, Town.class);
			m.invoke(null, player.getInventory(), town);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException 
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new CivException(e.getMessage());
		}

		
		CivMessage.sendSuccess(sender, "Spawned a "+unit.name);
	}
	
	public void server_cmd() {
		CivMessage.send(sender, Bukkit.getServerName());
	}
	
	
	public void recover_cmd() {
		AdminRecoverCommand cmd = new AdminRecoverCommand();	
		cmd.onCommand(sender, null, "recover", this.stripArgs(args, 1));	
	}
	
	public void town_cmd() {
		AdminTownCommand cmd = new AdminTownCommand();	
		cmd.onCommand(sender, null, "town", this.stripArgs(args, 1));
	}
	
	public void civ_cmd() {
		AdminCivCommand cmd = new AdminCivCommand();	
		cmd.onCommand(sender, null, "civ", this.stripArgs(args, 1));
	}

	public void setfullmessage_cmd() {
		if (args.length < 2) {
			CivMessage.send(sender, "Current:"+CivGlobal.fullMessage);
			return;
		}
		
		synchronized(CivGlobal.maxPlayers) {
			CivGlobal.fullMessage = args[1];
		}
		
		CivMessage.sendSuccess(sender, "Set to:"+args[1]);
		
	}
	
	@SuppressWarnings("deprecation")
	public void unban_cmd() throws CivException {
		if (args.length < 2) {
			throw new CivException("Enter a player name to ban");
		}
		
		Resident r = CivGlobal.getResident(args[1]);
		
		OfflinePlayer offplayer = Bukkit.getOfflinePlayer(r.getUUID());
		if (offplayer != null && offplayer.isBanned()) {
			offplayer.setBanned(false);
			Resident resident = CivGlobal.getResident(offplayer.getName());
			if (resident != null) {
				resident.setBanned(false);
				resident.save();
			}
			CivMessage.sendSuccess(sender, "Unbanned "+args[1]);
		} else {
			CivMessage.sendSuccess(sender, "Couldn't find "+args[1]+" or he is not banned.");
		}
	}
	
	public void res_cmd() {
		AdminResCommand cmd = new AdminResCommand();	
		cmd.onCommand(sender, null, "war", this.stripArgs(args, 1));	}
	
	public void chat_cmd() {
		AdminChatCommand cmd = new AdminChatCommand();	
		cmd.onCommand(sender, null, "war", this.stripArgs(args, 1));
	}

	public void war_cmd() {
		AdminWarCommand cmd = new AdminWarCommand();	
		cmd.onCommand(sender, null, "war", this.stripArgs(args, 1));
	}
	
	public void lag_cmd() {
		AdminLagCommand cmd = new AdminLagCommand();	
		cmd.onCommand(sender, null, "war", this.stripArgs(args, 1));
	}
	
	public void build_cmd() {
		AdminBuildCommand cmd = new AdminBuildCommand();	
		cmd.onCommand(sender, null, "war", this.stripArgs(args, 1));
	}
	
	public void perm_cmd() throws CivException {
		Resident resident = getResident();
		
		if (resident.isPermOverride()) {
			resident.setPermOverride(false);
			CivMessage.sendSuccess(sender, "Permission override off.");
			return;
		}
		
		resident.setPermOverride(true);
		CivMessage.sendSuccess(sender, "Permission override on.");
		
	}
	
	public void sbperm_cmd() throws CivException {
		Resident resident = getResident();
		if (resident.isSBPermOverride()) {
			resident.setSBPermOverride(false);
			CivMessage.sendSuccess(sender, "Structure Permission override off.");
			return;
		}
		
		resident.setSBPermOverride(true);
		CivMessage.sendSuccess(sender, "Structure Permission override on.");
	}
	
	

	
	@Override
	public void doDefaultAction() throws CivException {
		showHelp();
	}

	@Override
	public void showHelp() {
		showBasicHelp();
	}

	@Override
	public void permissionCheck() throws CivException {
		
		if (sender instanceof Player) {
			if (((Player)sender).hasPermission(CivSettings.MINI_ADMIN)) {
				return;
			}
		}
		
		
		if (sender.isOp() == false) {
			throw new CivException("Only admins can use this command.");			
		}
	}

	@Override
	public void doLogging() {
		CivLog.adminlog(sender.getName(), "/ad "+this.combineArgs(args));
	}
	
}
