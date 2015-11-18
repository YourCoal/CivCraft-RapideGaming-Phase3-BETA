package com.civcraft.mobs;

import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.PathfinderGoalFloat;
import net.minecraft.server.v1_8_R3.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_8_R3.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_8_R3.PathfinderGoalNearestAttackableTarget;

import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.event.entity.EntityTargetEvent;

import com.avrgaming.mob.ICustomMob;
import com.avrgaming.mob.MobBasePigZombie;
import com.civcraft.mobs.MobSpawner.CustomMobLevel;
import com.civcraft.mobs.MobSpawner.CustomMobType;
import com.civcraft.mobs.components.MobComponentDefense;

public class Savage extends CommonCustomMob implements ICustomMob {
	
	public static int TARGET_DISTANCE = 32;
	
	public void onCreate() {
	    initLevelAndType();	    
	    
	    getGoalSelector().a(0, new PathfinderGoalFloat((EntityInsentient) entity));
	    getGoalSelector().a(2, new PathfinderGoalMeleeAttack((EntityCreature) entity, EntityHuman.class, 1.0D, false));
	    getGoalSelector().a(8, new PathfinderGoalLookAtPlayer((EntityInsentient) entity, EntityHuman.class, 8.0F));
	    getTargetSelector().a(2, new PathfinderGoalNearestAttackableTarget<EntityHuman>((EntityCreature) entity, EntityHuman.class, true));
	    
	    this.setName(this.getLevel().getName()+" "+this.getType().getName());
	}
	
	public void onCreateAttributes() {
		MobComponentDefense defense;
	    this.setKnockbackResistance(0.5D);
	    this.setMovementSpeed(0.2);
	    this.setFollowRange(10.0D);
	    
		switch (this.getLevel()) {
		case LESSER:
		    defense = new MobComponentDefense(3.5);
		    setMaxHealth(10.0);
		    modifySpeed(1.4);
		    this.setAttack(5.0);
		    this.addDrop("mat_metallic_crystal_fragment_1", 0.05);
		    this.addDrop("mat_forged_clay", 0.1);
		    this.addDrop("mat_crafted_reeds", 0.1);
		    this.addDrop("mat_crafted_sticks", 0.1);
		    this.coinDrop(1, 25);
			break;
			
		case GREATER:
		    defense = new MobComponentDefense(10);
		    setMaxHealth(20.0);
		    modifySpeed(1.5);
		    this.setAttack(10.0);
		    this.addDrop("mat_metallic_crystal_fragment_2", 0.05);
		    this.addDrop("mat_aged_wood_stave", 0.1);
		    this.addDrop("mat_crafted_string", 0.05);
		    this.addDrop("mat_varnish", 0.01);
		    this.addDrop("mat_sticky_resin", 0.01);
		    this.coinDrop(10, 50);
		    break;
		    
		case ELITE:
		    defense = new MobComponentDefense(16);
		    setMaxHealth(40.0);
		    modifySpeed(1.6);
		    this.setAttack(15.0);
		    this.addDrop("mat_metallic_crystal_fragment_3", 0.05);
		    this.addDrop("mat_aged_wood_stave", 0.1);
		    this.addDrop("mat_varnish", 0.05);
		    this.addDrop("mat_woven_threading", 0.1);
		    this.addDrop("mat_sticky_resin", 0.1);
		    this.addDrop("mat_smithy_resin", 0.01);
		    this.coinDrop(20, 80);
			break;
			
		case BRUTAL:
		    defense = new MobComponentDefense(20);
		    setMaxHealth(80.0);
		    modifySpeed(1.6);
		    this.setAttack(22.0);
		    this.addDrop("mat_metallic_crystal_fragment_4", 0.05);
		    this.addDrop("mat_longbow_stave", 0.1);
		    this.addDrop("mat_reinforced_braid", 0.15);
		    this.addDrop("mat_leather_straps", 0.1);
		    this.addDrop("mat_sticky_resin", 0.1);
		    this.addDrop("mat_smithy_resin", 0.01);
		    this.coinDrop(20, 150);
			break;
		default:
		    defense = new MobComponentDefense(2);
			break;
		}
	    this.addComponent(defense);
	}
	
	@Override
	public String getBaseEntity() {
		return MobBasePigZombie.class.getName();
	}
	
	@Override
	public String getClassName() {
		return Savage.class.getName();
	}
	
	public static void register() {
	    setValidBiome(CustomMobType.SAVAGE, CustomMobLevel.LESSER, Biome.DESERT);
	    setValidBiome(CustomMobType.SAVAGE, CustomMobLevel.LESSER, Biome.DESERT_HILLS);
	    setValidBiome(CustomMobType.SAVAGE, CustomMobLevel.LESSER, Biome.DESERT_MOUNTAINS);
	
	    setValidBiome(CustomMobType.SAVAGE, CustomMobLevel.GREATER, Biome.SAVANNA);
	    setValidBiome(CustomMobType.SAVAGE, CustomMobLevel.GREATER, Biome.SAVANNA_MOUNTAINS);
	    setValidBiome(CustomMobType.SAVAGE, CustomMobLevel.GREATER, Biome.SAVANNA_PLATEAU);
	    setValidBiome(CustomMobType.SAVAGE, CustomMobLevel.GREATER, Biome.SAVANNA_PLATEAU_MOUNTAINS);

	    setValidBiome(CustomMobType.SAVAGE, CustomMobLevel.ELITE, Biome.MESA);
	    setValidBiome(CustomMobType.SAVAGE, CustomMobLevel.ELITE, Biome.MESA_PLATEAU);
	    setValidBiome(CustomMobType.SAVAGE, CustomMobLevel.ELITE, Biome.MEGA_TAIGA);
	    setValidBiome(CustomMobType.SAVAGE, CustomMobLevel.ELITE, Biome.MEGA_SPRUCE_TAIGA);

	    setValidBiome(CustomMobType.SAVAGE, CustomMobLevel.BRUTAL, Biome.MESA_BRYCE);
	    setValidBiome(CustomMobType.SAVAGE, CustomMobLevel.ELITE, Biome.MESA_PLATEAU_FOREST);
	    setValidBiome(CustomMobType.SAVAGE, CustomMobLevel.BRUTAL, Biome.MESA_PLATEAU_MOUNTAINS);
	    setValidBiome(CustomMobType.SAVAGE, CustomMobLevel.BRUTAL, Biome.MESA_PLATEAU_FOREST_MOUNTAINS);
	    setValidBiome(CustomMobType.SAVAGE, CustomMobLevel.ELITE, Biome.MEGA_SPRUCE_TAIGA_HILLS);
	    setValidBiome(CustomMobType.SAVAGE, CustomMobLevel.ELITE, Biome.MEGA_TAIGA_HILLS);
	}
	
	public void onTarget(EntityTargetEvent event) {
		Location current = getLocation((EntityCreature) entity);
		Location targetLoc = event.getTarget().getLocation();
		
		if (current.distance(targetLoc) > TARGET_DISTANCE) {
			event.setCancelled(true);
		}
		super.onTarget(event);
	}
}
