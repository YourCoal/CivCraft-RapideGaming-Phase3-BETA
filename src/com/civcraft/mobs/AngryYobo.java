package com.civcraft.mobs;

import moblib.mob.ICustomMob;
import moblib.mob.MobBaseZombie;
import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.PathfinderGoalFloat;
import net.minecraft.server.v1_8_R3.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_8_R3.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_8_R3.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;

import org.bukkit.Material;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

import com.civcraft.mobs.components.MobComponentDefense;
import com.civcraft.util.ItemManager;

public class AngryYobo extends CommonCustomMob implements ICustomMob {
	
	public void onCreate() {
	    initLevelAndType();
	    getGoalSelector().a(0, new PathfinderGoalFloat((EntityInsentient) entity));
	    getGoalSelector().a(2, new PathfinderGoalMeleeAttack((EntityCreature) entity, EntityHuman.class, 1.0D, false));
	    getGoalSelector().a(8, new PathfinderGoalLookAtPlayer((EntityInsentient) entity, EntityHuman.class, 8.0F));
	    getTargetSelector().a(2, new PathfinderGoalNearestAttackableTarget<EntityHuman>((EntityCreature) entity, EntityHuman.class, true));
	    this.setName(this.getLevel().getName()+" "+this.getType().getName());
	    MobBaseZombie zombie = ((MobBaseZombie)this.entity);
	    zombie.setBaby(true);
	}
	
	@Override
	public void onTick() {
		super.onTick();		
	}
	
	@Override
	public String getBaseEntity() {
		return MobBaseZombie.class.getName();
	}
	
	public void onDamage(EntityCreature e, DamageSource damagesource, PathfinderGoalSelector goalSelector, PathfinderGoalSelector targetSelector) {
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
	public void onRangedAttack(Entity target) {
	}
	
	@Override
	public String getClassName() {
		return AngryYobo.class.getName();
	}
	
	@Override
	public void onTarget(EntityTargetEvent event) {
		super.onTarget(event);
		
		if (event.getReason().equals(TargetReason.FORGOT_TARGET) ||
		    event.getReason().equals(TargetReason.TARGET_DIED)) {
			event.getEntity().remove();
		}
	}
}
