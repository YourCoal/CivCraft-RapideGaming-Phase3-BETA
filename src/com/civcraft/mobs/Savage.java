package com.civcraft.mobs;

import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.PathfinderGoalFloat;
import net.minecraft.server.v1_8_R3.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_8_R3.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_8_R3.PathfinderGoalNearestAttackableTarget;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.event.entity.EntityTargetEvent;

import com.civcraft.mobs.MobSpawner.CustomMobLevel;
import com.civcraft.mobs.MobSpawner.CustomMobType;
import com.civcraft.mobs.components.MobComponentDefense;
import com.civcraft.util.ItemManager;

import moblib.mob.ICustomMob;
import moblib.mob.MobBasePigZombie;

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
