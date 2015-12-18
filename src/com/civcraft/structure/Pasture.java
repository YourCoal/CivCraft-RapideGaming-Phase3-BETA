package com.civcraft.structure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import com.civcraft.components.NonMemberFeeComponent;
import com.civcraft.config.CivSettings;
import com.civcraft.config.ConfigPastureLevel;
import com.civcraft.exception.CivException;
import com.civcraft.exception.InvalidConfiguration;
import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivLog;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Resident;
import com.civcraft.object.StructureSign;
import com.civcraft.object.Town;
import com.civcraft.sessiondb.SessionEntry;
import com.civcraft.threading.TaskMaster;
import com.civcraft.threading.tasks.LoadPastureEntityTask;
import com.civcraft.util.BlockCoord;
import com.civcraft.util.ChunkCoord;
import com.civcraft.util.CivColor;

public class Pasture extends Structure {
	
	/* Global pasture chunks */
	public static Map<ChunkCoord, Pasture> pastureChunks = new ConcurrentHashMap<ChunkCoord, Pasture>();
	public static Map<UUID, Pasture> pastureEntities = new ConcurrentHashMap<UUID, Pasture>();
	
	/* Chunks bound to this pasture. */
	public HashSet<ChunkCoord> chunks = new HashSet<ChunkCoord>();
	public HashSet<UUID> entities = new HashSet<UUID>();
	public ReentrantLock lock = new ReentrantLock(); 
	
	private int pendingBreeds = 0;
	
	private int level = 1;
	private NonMemberFeeComponent nonMemberFeeComponent;
	
	protected Pasture(Location center, String id, Town town) throws CivException {
		super(center, id, town);
		nonMemberFeeComponent = new NonMemberFeeComponent(this);
		nonMemberFeeComponent.onSave();
	}
	
	public Pasture(ResultSet rs) throws SQLException, CivException {
		super(rs);
		nonMemberFeeComponent = new NonMemberFeeComponent(this);
		nonMemberFeeComponent.onLoad();
	}
	
	
	//XXX The Buying of eggs
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public double getNonResidentFee() {
		return nonMemberFeeComponent.getFeeRate();
	}
	
	public void setNonResidentFee(double nonResidentFee) {
		this.nonMemberFeeComponent.setFeeRate(nonResidentFee);
	}
	
	private String getNonResidentFeeString() {
		return "Fee: "+((int)(getNonResidentFee()*100) + "%").toString();		
	}
	
	private StructureSign getSignFromSpecialId(int special_id) {
		for (StructureSign sign : getSigns()) {
			int id = Integer.valueOf(sign.getAction());
			if (id == special_id) {
				return sign;
			}
		}
		return null;
	}
	
	public void sign_buy_material(Player player, String itemName, int id, byte data, int amount, double price) {
		Resident resident;
		int payToTown = (int) Math.round(price*this.getNonResidentFee());
		try {
			resident = CivGlobal.getResident(player.getName());
			Town t = resident.getTown();
			if (t == this.getTown()) {
				// Pay no taxes! You're a member.
				resident.buyItem(itemName, id, data, price, amount);
				CivMessage.send(player, CivColor.LightGreen + "Bought "+amount+" "+itemName+" for "+ price+ " coins.");
				return;
			} else {
				// Pay non-resident taxes
				resident.buyItem(itemName, id, data, price + payToTown, amount);
				getTown().depositDirect(payToTown);
				CivMessage.send(player, CivColor.LightGreen + "Bought "+amount+" "+itemName+" for "+ price+ " coins.");
				CivMessage.send(player, CivColor.Yellow + "Paid "+ payToTown+" coins in non-resident taxes.");
			}
		} catch (CivException e) {
			CivMessage.send(player, CivColor.Rose + e.getMessage());
		}
		return;
	}
	
	@Override
	public void updateSignText() {
		int count = 0;
		for (count = 0; count < level; count++) {
			StructureSign sign = getSignFromSpecialId(count);
			if (sign == null) {
				CivLog.error("sign from special id was null, id:"+count);
				return;
			}
			ConfigPastureLevel pasturelevel = CivSettings.pastureLevels.get(count+1);
			sign.setText("Buy\n"+pasturelevel.itemName+"\n"+
			"For "+pasturelevel.price+" Coins\n"+getNonResidentFeeString());
			sign.update();
		} for (; count < getSigns().size(); count++) {
			StructureSign sign = getSignFromSpecialId(count);
			if (sign == null) {
				CivLog.error("sign from special id was null, id:"+count);
				return;
			}
			sign.setText("Pasture Shelf\nEmpty");
			sign.update();
		}
	}
	
	@Override
	public void processSignAction(Player player, StructureSign sign, PlayerInteractEvent event) {
		int special_id = Integer.valueOf(sign.getAction());
		if (special_id < this.level) {
			ConfigPastureLevel pasturelevel = CivSettings.pastureLevels.get(special_id+1);
			sign_buy_material(player, pasturelevel.itemName, pasturelevel.itemId, 
					(byte)pasturelevel.itemData, pasturelevel.amount, pasturelevel.price);
		} else {
			CivMessage.send(player, CivColor.Rose+"Pasture shelf empty, stock it using /town upgrade.");
		}
	}
	
	
	//XXX The actual mob functioning
	public int getMobCount() {
		return entities.size();
	}
	
	public int getMobMax() {
		int max;
		try {
			max = CivSettings.getInteger(CivSettings.structureConfig, "pasture.max_mobs");
		} catch (InvalidConfiguration e) {
			e.printStackTrace();
			return 0;
		}
		return max;
	}
	
	public boolean processMobBreed(Player player, EntityType type) {	
		if (!this.isActive()) {
			CivMessage.sendError(player, "Pasture is destroyed or currently building. Cannot breed yet.");
			return false;
		}
		if (this.getMobCount() >= this.getMobMax()) {
			CivMessage.sendError(player, "Pasture is the maximum number of mobs that it can support. Slaughter some before you breed.");
			return false;
		}
		if ((getPendingBreeds() + this.getMobCount()) >= this.getMobMax()) {
			CivMessage.sendError(player, "Pasture has too many breed events pending. Pasture is probably at the maximum number of mobs it can support. Slaughter some before you breed.");
			return false;
		}
		return true;
	}
	
	public void bindPastureChunks() {
		for (BlockCoord bcoord : this.structureBlocks.keySet()) {
			ChunkCoord coord = new ChunkCoord(bcoord);
			this.chunks.add(coord);
			pastureChunks.put(coord, this);
		}
	}
	
	public void unbindPastureChunks() {
		for (ChunkCoord coord : this.chunks) {
			pastureChunks.remove(coord);
		}
		
		this.entities.clear();
		this.chunks.clear();
		LinkedList<UUID> removeUs = new LinkedList<UUID>();
		for (UUID id : pastureEntities.keySet()) {
			Pasture pasture = pastureEntities.get(id);
			if (pasture == this) {
				removeUs.add(id);
			}
		} for (UUID id : removeUs) {
			pastureEntities.remove(id);
		}
	}
	
	@Override
	public void onComplete() {
		bindPastureChunks();
	}
	
	@Override
	public void onLoad() throws CivException {
		bindPastureChunks();
		loadEntities();
	}
	
	@Override
	public void delete() throws SQLException {
		super.delete();
		unbindPastureChunks();
		clearEntities();
	}
	
	public void clearEntities() {
	}
	
	public void onBreed(LivingEntity entity) {
		saveEntity(entity.getWorld().getName(), entity.getUniqueId());
		setPendingBreeds(getPendingBreeds() - 1);
	}
	
	public String getEntityKey() {
		return "pasture:"+this.getId();
	}
	
	public String getValue(String worldName, UUID id) {
		return worldName+":"+id;
	}
	
	public void saveEntity(String worldName, UUID id) {
		class AsyncTask implements Runnable {
			Pasture pasture;
			UUID id;
			String worldName;
			public AsyncTask(Pasture pasture, UUID id, String worldName) {
				this.pasture = pasture;
				this.id = id;
				this.worldName = worldName;
			}
			
			@Override
			public void run() {
				pasture.sessionAdd(getEntityKey(), getValue(worldName, id));
				lock.lock();
				try {
					entities.add(id);
					pastureEntities.put(id, pasture);
				} finally {
					lock.unlock();
				}
			}
		}
		TaskMaster.asyncTask(new AsyncTask(this, id, worldName), 0);
	}
	
	public void loadEntities() {
		Queue<SessionEntry> entriesToLoad = new LinkedList<SessionEntry>();
		ArrayList<SessionEntry> entries = CivGlobal.getSessionDB().lookup(getEntityKey());
		entriesToLoad.addAll(entries);
		TaskMaster.syncTask(new LoadPastureEntityTask(entriesToLoad, this));
	}
	
	public void onEntityDeath(LivingEntity entity) {
		class AsyncTask implements Runnable {
			LivingEntity entity;
			public AsyncTask(LivingEntity entity) {
				this.entity = entity;
			}
			
			@Override
			public void run() {
				lock.lock();
				try {
					entities.remove(entity.getUniqueId());
					pastureEntities.remove(entity.getUniqueId());
				} finally {
					lock.unlock();
				}
			}
		}
		TaskMaster.asyncTask(new AsyncTask(entity), 0);
	}
	
	public int getPendingBreeds() {
		return pendingBreeds;
	}
	
	public void setPendingBreeds(int pendingBreeds) {
		this.pendingBreeds = pendingBreeds;
	}
}
