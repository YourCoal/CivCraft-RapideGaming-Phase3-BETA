package com.civcraft.mobs;

import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_8_R3.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_8_R3.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_8_R3.PathfinderGoalRandomStroll;

import org.bukkit.block.Biome;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

import moblib.mob.ICustomMob;
import moblib.mob.MobBaseIronGolem;

import com.civcraft.mobs.MobSpawner.CustomMobLevel;
import com.civcraft.mobs.MobSpawner.CustomMobType;
import com.civcraft.mobs.components.MobComponentDefense;

public class TorridOgre extends CommonCustomMob implements ICustomMob {
	
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
	    this.setKnockbackResistance(0.85D);
	    this.setMovementSpeed(0.15);
		switch (this.getLevel()) {
		case LESSER:
		    defense = new MobComponentDefense(3);
		    setMaxHealth(30);
		    modifySpeed(1.0);
		    this.setAttack(4.0);
		    this.addDrop("civ:catalyst_1", 0.05);
		    this.addDrop("civ:material_1", 0.25);
		    this.addDrop("civ:material_2", 0.25);
		    this.addDrop("civ:material_3", 0.25);
		    this.coinDrop(5, 20);
			break;
		case GREATER:
		    defense = new MobComponentDefense(9);
		    setMaxHealth(45);
		    modifySpeed(1.3);
		    this.setAttack(8.0);
		    this.addDrop("civ:catalyst_2", 0.05);
		    this.addDrop("civ:material_1", 0.25);
		    this.addDrop("civ:material_2", 0.25);
		    this.addDrop("civ:material_3", 0.25);
		    this.coinDrop(10, 40);
			break;
		case ELITE:
		    defense = new MobComponentDefense(13);
		    setMaxHealth(60);
		    modifySpeed(1.4);
		    this.setAttack(12.0);
		    this.addDrop("civ:catalyst_3", 0.05);
		    this.addDrop("civ:material_1", 0.25);
		    this.addDrop("civ:material_2", 0.25);
		    this.addDrop("civ:material_3", 0.25);
		    this.coinDrop(25, 65);
			break;
		case BRUTAL:
		    defense = new MobComponentDefense(17);
		    setMaxHealth(60);
		    modifySpeed(1.5);
		    this.setAttack(16.0);
		    this.addDrop("civ:catalyst_4", 0.05);
		    this.addDrop("civ:material_1", 0.25);
		    this.addDrop("civ:material_2", 0.25);
		    this.addDrop("civ:material_3", 0.25);
		    this.coinDrop(40, 80);
			break;
		default:
		    defense = new MobComponentDefense(1);
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
		return TorridOgre.class.getName();
	}
	
	public static void register() {
		    setValidBiome(CustomMobType.TORRID_OGRE, CustomMobLevel.LESSER, Biome.FROZEN_RIVER);
		    setValidBiome(CustomMobType.TORRID_OGRE, CustomMobLevel.LESSER, Biome.FROZEN_OCEAN);
		    setValidBiome(CustomMobType.TORRID_OGRE, CustomMobLevel.LESSER, Biome.COLD_BEACH);
		    setValidBiome(CustomMobType.TORRID_OGRE, CustomMobLevel.LESSER, Biome.COLD_TAIGA);
		    
		    setValidBiome(CustomMobType.TORRID_OGRE, CustomMobLevel.GREATER, Biome.COLD_TAIGA_HILLS);
		    setValidBiome(CustomMobType.TORRID_OGRE, CustomMobLevel.GREATER, Biome.COLD_TAIGA_MOUNTAINS);
		    
		    setValidBiome(CustomMobType.TORRID_OGRE, CustomMobLevel.ELITE, Biome.ICE_PLAINS);
		    setValidBiome(CustomMobType.TORRID_OGRE, CustomMobLevel.GREATER, Biome.ICE_MOUNTAINS);
		    
		    setValidBiome(CustomMobType.TORRID_OGRE, CustomMobLevel.BRUTAL, Biome.ICE_PLAINS_SPIKES);
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void onTarget(EntityTargetEvent event) {
		super.onTarget(event);
		if (event.getReason().equals(TargetReason.FORGOT_TARGET) ||
		    event.getReason().equals(TargetReason.TARGET_DIED)) {
			this.customMobs.remove(this.entity.getUniqueID());
		}
	}
}
