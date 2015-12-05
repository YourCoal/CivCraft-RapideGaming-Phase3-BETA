package com.civcraft.mobs;

import java.util.List;

import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.IRangedEntity;
import net.minecraft.server.v1_8_R3.PathfinderGoalArrowAttack;
import net.minecraft.server.v1_8_R3.PathfinderGoalFloat;
import net.minecraft.server.v1_8_R3.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_8_R3.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_8_R3.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_8_R3.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_8_R3.PathfinderGoalRandomStroll;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;

import com.civcraft.mobs.MobSpawner.CustomMobLevel;
import com.civcraft.mobs.MobSpawner.CustomMobType;
import com.civcraft.mobs.components.MobComponentDefense;
import com.civcraft.threading.TaskMaster;
import com.civcraft.threading.tasks.FireWorkTask;
import com.civcraft.util.ItemManager;
import moblib.mob.ICustomMob;
import moblib.mob.MobBaseWitch;

public class Ruffian extends CommonCustomMob implements ICustomMob {
	
	private double damage;
	
	public void onCreate() {
	    initLevelAndType();
	    getGoalSelector().a(1, new PathfinderGoalFloat((EntityInsentient) entity));
	    getGoalSelector().a(2, new PathfinderGoalArrowAttack((IRangedEntity) entity, 1.0D, 60, 10.0F));
	    getGoalSelector().a(2, new PathfinderGoalRandomStroll((EntityCreature) entity, 1.0D));
	    getGoalSelector().a(3, new PathfinderGoalLookAtPlayer((EntityInsentient) entity, EntityHuman.class, 8.0F));
	    getGoalSelector().a(3, new PathfinderGoalRandomLookaround((EntityInsentient) entity));
	    getTargetSelector().a(1, new PathfinderGoalHurtByTarget((EntityCreature) entity, false));
	    getTargetSelector().a(2, new PathfinderGoalNearestAttackableTarget<EntityHuman>((EntityCreature) entity, EntityHuman.class, true));
	    this.setName(this.getLevel().getName()+" "+this.getType().getName());
	}
	
	public void onCreateAttributes() {
		MobComponentDefense defense;
	    this.setKnockbackResistance(0.85);
		switch (this.getLevel()) {
		case LESSER:
		    defense = new MobComponentDefense(6);
		    setMaxHealth(12.0);
		    this.setAttack(4.0);
		    this.addDrop("civ:refined_sugar", 0.1);
		    this.addDrop("civ:crafted_sticks", 0.1);
		    this.addDrop("civ:crafted_string", 0.1);
			this.addVanillaDrop(ItemManager.getId(Material.IRON_INGOT), (short)0, 0.05);
		    this.coinDrop(1, 20);
			break;
			
		case GREATER:
		    defense = new MobComponentDefense(13.5);
		    setMaxHealth(17.0);
		    this.setAttack(8.5);
			this.addVanillaDrop(ItemManager.getId(Material.IRON_INGOT), (short)0, 0.1);
			this.addVanillaDrop(ItemManager.getId(Material.GOLD_INGOT), (short)0, 0.05);
		    this.addDrop("civ:bronze_ore", 0.05);
		    this.addDrop("civ:compressed_sugar", 0.1);
		    this.addDrop("civ:refined_sticks", 0.1);
		    this.addDrop("civ:refined_string", 0.1);
		    this.coinDrop(5, 45);
		    break;
		    
		case ELITE:
		    defense = new MobComponentDefense(17.5);
		    setMaxHealth(22.0);
		    this.setAttack(13.0);
			this.addVanillaDrop(ItemManager.getId(Material.GOLD_INGOT), (short)0, 0.1);
			this.addVanillaDrop(ItemManager.getId(Material.INK_SACK), (short)4, 0.2);
		    this.addDrop("civ:steel_ore", 0.05);
		    this.addDrop("civ:compacted_sticks", 0.1);
		    this.addDrop("civ:wolven_threading", 0.1);
		    this.coinDrop(20, 60);
			break;
			
		case BRUTAL:
		    defense = new MobComponentDefense(21.5);
		    setMaxHealth(27.0);
		    this.setAttack(17.5);
			this.addVanillaDrop(ItemManager.getId(Material.INK_SACK), (short)4, 0.4);
			this.addVanillaDrop(ItemManager.getId(Material.DIAMOND), (short)0, 0.05);
		    this.addDrop("civ:titanium_ore", 0.05);
		    this.addDrop("civ:refined_compacted_sticks", 0.1);
		    this.addDrop("civ:refined_wolven_threading", 0.1);
		    this.coinDrop(35, 100);
			break;
		default:
		    defense = new MobComponentDefense(1);
			break;
		}
	    this.addComponent(defense);
	}
	
	@Override
	public String getBaseEntity() {
		return MobBaseWitch.class.getName();
	}
	
	@Override
	public void onRangedAttack(Entity target) {
		if (!(target instanceof EntityPlayer)) {
			return;
		}
		
		class RuffianProjectile {
			Location loc;
			Location target;
			org.bukkit.entity.Entity attacker;
			int speed = 1;
			double damage;
			int splash = 6;
			
			public RuffianProjectile(Location loc, Location target, org.bukkit.entity.Entity attacker, double damage) {
				this.loc = loc;
				this.target = target;
				this.attacker = attacker;
				this.damage = damage;
			}
			
			public Vector getVectorBetween(Location to, Location from) {
				Vector dir = new Vector();
				dir.setX(to.getX() - from.getX());
				dir.setY(to.getY() - from.getY());
				dir.setZ(to.getZ() - from.getZ());
				return dir;
			}
			
			public boolean advance() {
				Vector dir = getVectorBetween(target, loc).normalize();
				double distance = loc.distanceSquared(target);		
				dir.multiply(speed);
				loc.add(dir);
				loc.getWorld().createExplosion(loc, 0.0f, false);
				distance = loc.distanceSquared(target);
				
				if (distance < speed*1.5) {
					loc.setX(target.getX());
					loc.setY(target.getY());
					loc.setZ(target.getZ());
					this.onHit();
					return true;
				}
				return false;
			}
			
			public void onHit() {				
				int spread = 3;
				int[][] offset = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
				for (int i = 0; i < 4; i++) {
					int x = offset[i][0]*spread;
					int y = 0;
					int z = offset[i][1]*spread;
					Location location = new Location(loc.getWorld(), loc.getX(),loc.getY(), loc.getZ());
					location = location.add(x, y, z);
					launchExplodeFirework(location);
					//loc.getWorld().createExplosion(location, 1.0f, true);
					//setFireAt(location, spread);
				}
				launchExplodeFirework(loc);
				//loc.getWorld().createExplosion(loc, 1.0f, true);
				damagePlayers(loc, splash);
				//setFireAt(loc, spread);		
			}
			
			@SuppressWarnings("deprecation")
			private void damagePlayers(Location loc, int radius) {
				double x = loc.getX()+0.5;
				double y = loc.getY()+0.5;
				double z = loc.getZ()+0.5;
				double r = (double)radius;
				
				CraftWorld craftWorld = (CraftWorld)attacker.getWorld();
				AxisAlignedBB bb = AxisAlignedBB.a(x-r, y-r, z-r, x+r, y+r, z+r);
				List<Entity> entities = craftWorld.getHandle().getEntities(((CraftEntity)attacker).getHandle(), bb);
				
				for (Entity e : entities) {
					if (e instanceof EntityPlayer) {
						EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(attacker, ((EntityPlayer)e).getBukkitEntity(), DamageCause.ENTITY_ATTACK, damage);
						Bukkit.getServer().getPluginManager().callEvent(event);
						e.damageEntity(DamageSource.GENERIC, (float) event.getDamage());
					}
				}
			}
			
//			private void setFireAt(Location loc, int radius) {
//				//Set the entire area on fire.
//				for (int x = -radius; x < radius; x++) {
//					for (int y = -3; y < 3; y++) {
//						for (int z = -radius; z < radius; z++) {
//							Block block = loc.getWorld().getBlockAt(loc.getBlockX()+x, loc.getBlockY()+y, loc.getBlockZ()+z);
//							if (ItemManager.getId(block) == CivData.AIR) {
//								ItemManager.setTypeId(block, CivData.FIRE);
//								ItemManager.setData(block, 0, true);
//							}
//						}
//					}
//				}
//			}
			
			private void launchExplodeFirework(Location loc) {
				FireworkEffect fe = FireworkEffect.builder().withColor(Color.ORANGE).withColor(Color.YELLOW).flicker(true).with(Type.BURST).build();		
				TaskMaster.syncTask(new FireWorkTask(fe, loc.getWorld(), loc, 3), 0);
			}
		}
		
		class SyncFollow implements Runnable {
			public RuffianProjectile proj;
			
			@Override
			public void run() {
				
				if (proj.advance()) {
					proj = null;
					return;
				}
				TaskMaster.syncTask(this, 1);
			}
		}
		SyncFollow follow = new SyncFollow();
		RuffianProjectile proj = new RuffianProjectile(getLocation((EntityCreature) entity), 
				getLocation((EntityPlayer)target), this.entity.getBukkitEntity(), damage);
		follow.proj = proj;
		TaskMaster.syncTask(follow);		
	}
	
	public Location getLocation(EntityPlayer p) {
		World world = Bukkit.getWorld(p.world.getWorld().getName());
		Location loc = new Location(world, p.locX, p.locY, p.locZ);
		return loc;
	}
	
	@Override
	public String getClassName() {
		return Ruffian.class.getName();
	}
	
	public static void register() {
	    setValidBiome(CustomMobType.RUFFIAN, CustomMobLevel.LESSER, Biome.JUNGLE);
	    setValidBiome(CustomMobType.RUFFIAN, CustomMobLevel.LESSER, Biome.MEGA_TAIGA);
	    setValidBiome(CustomMobType.RUFFIAN, CustomMobLevel.LESSER, Biome.JUNGLE_EDGE);
	    setValidBiome(CustomMobType.RUFFIAN, CustomMobLevel.LESSER, Biome.JUNGLE_EDGE_MOUNTAINS);
	    setValidBiome(CustomMobType.RUFFIAN, CustomMobLevel.LESSER, Biome.SWAMPLAND);
	    
	    setValidBiome(CustomMobType.RUFFIAN, CustomMobLevel.GREATER, Biome.MEGA_SPRUCE_TAIGA_HILLS);
	    setValidBiome(CustomMobType.RUFFIAN, CustomMobLevel.GREATER, Biome.JUNGLE_HILLS);
	    
	    setValidBiome(CustomMobType.RUFFIAN, CustomMobLevel.ELITE, Biome.BIRCH_FOREST_HILLS_MOUNTAINS);
	    setValidBiome(CustomMobType.RUFFIAN, CustomMobLevel.ELITE, Biome.ROOFED_FOREST_MOUNTAINS);
	    setValidBiome(CustomMobType.RUFFIAN, CustomMobLevel.BRUTAL, Biome.JUNGLE_MOUNTAINS);
	    setValidBiome(CustomMobType.RUFFIAN, CustomMobLevel.BRUTAL, Biome.SWAMPLAND_MOUNTAINS);
	}
}
