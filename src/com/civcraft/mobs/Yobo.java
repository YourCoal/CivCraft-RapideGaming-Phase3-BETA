package com.civcraft.mobs;

import java.util.LinkedList;

import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.EntityDamageSource;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_8_R3.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_8_R3.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_8_R3.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

import com.civcraft.exception.CivException;
import com.civcraft.main.CivLog;
import com.civcraft.mobs.MobSpawner.CustomMobLevel;
import com.civcraft.mobs.MobSpawner.CustomMobType;
import com.civcraft.mobs.components.MobComponentDefense;
import com.civcraft.util.ItemManager;

import moblib.mob.ICustomMob;
import moblib.mob.MobBaseZombie;

public class Yobo extends CommonCustomMob implements ICustomMob {
	
	private String entityType = MobBaseZombie.class.getName();
	private boolean angry = false;
	
	LinkedList<Entity> minions = new LinkedList<Entity>();
	
	public void onCreate() {
	    initLevelAndType();
	    getGoalSelector().a(7, new PathfinderGoalRandomStroll((EntityCreature) entity, 1.0D));
	    getGoalSelector().a(8, new PathfinderGoalLookAtPlayer((EntityInsentient) entity, EntityHuman.class, 8.0F));
	    getTargetSelector().a(1, new PathfinderGoalHurtByTarget((EntityCreature) entity, true));
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
		    this.coinDrop(1, 15);
			break;
			
		case GREATER:
		    defense = new MobComponentDefense(13);
		    setMaxHealth(16.0);
		    this.setAttack(8.5);
			this.addVanillaDrop(ItemManager.getId(Material.IRON_INGOT), (short)0, 0.1);
			this.addVanillaDrop(ItemManager.getId(Material.GOLD_INGOT), (short)0, 0.05);
		    this.addDrop("civ:bronze_ore", 0.05);
		    this.addDrop("civ:compressed_sugar", 0.1);
		    this.addDrop("civ:refined_sticks", 0.1);
		    this.addDrop("civ:refined_string", 0.1);
		    this.coinDrop(5, 35);
		    break;
		    
		case ELITE:
		    defense = new MobComponentDefense(17);
		    setMaxHealth(20.0);
		    this.setAttack(13.0);
			this.addVanillaDrop(ItemManager.getId(Material.GOLD_INGOT), (short)0, 0.1);
			this.addVanillaDrop(ItemManager.getId(Material.INK_SACK), (short)4, 0.2);
		    this.addDrop("civ:steel_ore", 0.05);
		    this.addDrop("civ:compacted_sticks", 0.1);
		    this.addDrop("civ:wolven_threading", 0.1);
		    this.coinDrop(15, 50);
			break;
			
		case BRUTAL:
		    defense = new MobComponentDefense(21);
		    setMaxHealth(22.0);
		    this.setAttack(17.5);
			this.addVanillaDrop(ItemManager.getId(Material.INK_SACK), (short)4, 0.4);
			this.addVanillaDrop(ItemManager.getId(Material.DIAMOND), (short)0, 0.05);
		    this.addDrop("civ:titanium_ore", 0.05);
		    this.addDrop("civ:refined_compacted_sticks", 0.1);
		    this.addDrop("civ:refined_wolven_threading", 0.1);
		    this.coinDrop(30, 75);
			break;
		default:
		    defense = new MobComponentDefense(2);
			break;
		}
	    this.addComponent(defense);
	}
	
	@Override
	public String getBaseEntity() {
		return entityType;
	}
	
	@Override
	public void onDamage(EntityCreature e, DamageSource damagesource, PathfinderGoalSelector goalSelector, PathfinderGoalSelector targetSelector) {
		if (!(damagesource instanceof EntityDamageSource)) {
			return;
		}
		
		if (this.getLevel() == null) {
			this.setLevel(MobSpawner.CustomMobLevel.valueOf(getData("level")));
			if (this.getLevel() == null) {
				try {
					throw new CivException("Level was null after retry.");
				} catch (CivException e2) {
					CivLog.error("getData(level):"+getData("level"));
					e2.printStackTrace();
				}
			}
		}
		
		if (!angry) {
			angry = true;
			goalSelector.a(2, new PathfinderGoalMeleeAttack(e, EntityHuman.class, 1.0D, false));
			for (int i = 0; i < 4; i++) {
				try {
					this.minions.add(MobSpawner.spawnCustomMob(MobSpawner.CustomMobType.ANGRYYOBO, this.getLevel(), getLocation(e)).entity);
				} catch (CivException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public String getClassName() {
		return Yobo.class.getName();
	}
	
	public static void register() {
		    setValidBiome(CustomMobType.YOBO, CustomMobLevel.LESSER, Biome.PLAINS);
		    setValidBiome(CustomMobType.YOBO, CustomMobLevel.LESSER, Biome.FOREST);
		    setValidBiome(CustomMobType.YOBO, CustomMobLevel.LESSER, Biome.BIRCH_FOREST);
		    setValidBiome(CustomMobType.YOBO, CustomMobLevel.LESSER, Biome.BIRCH_FOREST_HILLS);
		
		    setValidBiome(CustomMobType.YOBO, CustomMobLevel.GREATER, Biome.SUNFLOWER_PLAINS);
		    setValidBiome(CustomMobType.YOBO, CustomMobLevel.GREATER, Biome.FLOWER_FOREST);
		    setValidBiome(CustomMobType.YOBO, CustomMobLevel.GREATER, Biome.BIRCH_FOREST_HILLS_MOUNTAINS);
		    setValidBiome(CustomMobType.YOBO, CustomMobLevel.GREATER, Biome.BIRCH_FOREST_MOUNTAINS);
		    setValidBiome(CustomMobType.YOBO, CustomMobLevel.GREATER, Biome.FOREST_HILLS);

		    setValidBiome(CustomMobType.YOBO, CustomMobLevel.ELITE, Biome.EXTREME_HILLS);
		    setValidBiome(CustomMobType.YOBO, CustomMobLevel.ELITE, Biome.EXTREME_HILLS_PLUS);
		    setValidBiome(CustomMobType.YOBO, CustomMobLevel.ELITE, Biome.ROOFED_FOREST);
		    setValidBiome(CustomMobType.YOBO, CustomMobLevel.ELITE, Biome.ROOFED_FOREST_MOUNTAINS);
	
		    setValidBiome(CustomMobType.YOBO, CustomMobLevel.BRUTAL, Biome.MEGA_SPRUCE_TAIGA_HILLS);
		    setValidBiome(CustomMobType.YOBO, CustomMobLevel.BRUTAL, Biome.EXTREME_HILLS_MOUNTAINS);
		    setValidBiome(CustomMobType.YOBO, CustomMobLevel.BRUTAL, Biome.EXTREME_HILLS_PLUS_MOUNTAINS);
	}
	
	@Override
	public void onTarget(EntityTargetEvent event) {
		super.onTarget(event);
		
		if (event.getReason().equals(TargetReason.FORGOT_TARGET) ||
		    event.getReason().equals(TargetReason.TARGET_DIED)) {
			this.angry = false;
			for (Entity e : minions) {
				e.getBukkitEntity().remove();
			}
		}
	}
}
