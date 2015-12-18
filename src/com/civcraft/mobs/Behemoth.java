package com.civcraft.mobs;

import moblib.mob.ICustomMob;
import moblib.mob.MobBaseIronGolem;
import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_8_R3.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_8_R3.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_8_R3.PathfinderGoalRandomStroll;

import org.bukkit.Material;
import org.bukkit.block.Biome;

import com.civcraft.mobs.MobSpawner.CustomMobLevel;
import com.civcraft.mobs.MobSpawner.CustomMobType;
import com.civcraft.mobs.components.MobComponentDefense;
import com.civcraft.util.ItemManager;

public class Behemoth extends CommonCustomMob implements ICustomMob {
	
	public void onCreate() {
	    initLevelAndType();
		getGoalSelector().a(7, new PathfinderGoalRandomStroll((EntityCreature) entity, 1.0D));
		getGoalSelector().a(8, new PathfinderGoalLookAtPlayer((EntityInsentient) entity, EntityHuman.class, 8.0F));
	    getGoalSelector().a(2, new PathfinderGoalMeleeAttack((EntityCreature) entity, EntityHuman.class, 1.0D, false));
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
		return MobBaseIronGolem.class.getName();
	}
	
	@Override
	public String getClassName() {
		return Behemoth.class.getName();
	}
	
	public static void register() {
		    setValidBiome(CustomMobType.BEHEMOTH, CustomMobLevel.LESSER, Biome.FROZEN_RIVER);
		    setValidBiome(CustomMobType.BEHEMOTH, CustomMobLevel.LESSER, Biome.FROZEN_OCEAN);
		    setValidBiome(CustomMobType.BEHEMOTH, CustomMobLevel.LESSER, Biome.COLD_BEACH);
		    setValidBiome(CustomMobType.BEHEMOTH, CustomMobLevel.LESSER, Biome.COLD_TAIGA);
	
		    setValidBiome(CustomMobType.BEHEMOTH, CustomMobLevel.GREATER, Biome.COLD_TAIGA_HILLS);
		    setValidBiome(CustomMobType.BEHEMOTH, CustomMobLevel.GREATER, Biome.COLD_TAIGA_MOUNTAINS);
		
		    setValidBiome(CustomMobType.BEHEMOTH, CustomMobLevel.ELITE, Biome.ICE_PLAINS);
		    setValidBiome(CustomMobType.BEHEMOTH, CustomMobLevel.GREATER, Biome.ICE_MOUNTAINS);

		    setValidBiome(CustomMobType.BEHEMOTH, CustomMobLevel.BRUTAL, Biome.ICE_PLAINS_SPIKES);
	}
}
