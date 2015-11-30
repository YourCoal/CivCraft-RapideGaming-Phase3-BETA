package com.civcraft.mobs;

import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_8_R3.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_8_R3.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_8_R3.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;

import com.civcraft.exception.CivException;
import com.civcraft.mobs.components.MobComponentDefense;
import moblib.mob.ICustomMob;
import moblib.mob.MobBaseZombieGiant;

public class YoboBoss extends CommonCustomMob implements ICustomMob {
	
	private String entityType = MobBaseZombieGiant.class.getName();
	
	public void onCreate() {
	    initLevelAndType();
	    MobBaseZombieGiant zombie = (MobBaseZombieGiant)this.entity;
	    zombie.width *= 6.0f;
	    getGoalSelector().a(7, new PathfinderGoalRandomStroll((EntityCreature) entity, 100.0F));
	    getGoalSelector().a(8, new PathfinderGoalLookAtPlayer((EntityInsentient) entity, EntityHuman.class, 8.0F));
	    getGoalSelector().a(2, new PathfinderGoalMeleeAttack((EntityCreature) entity, EntityHuman.class, 100.0F, false));
	   // getTargetSelector().a(1, new PathfinderGoalHurtByTarget((EntityCreature) entity, true));
	    getTargetSelector().a(2, new PathfinderGoalNearestAttackableTarget<EntityHuman>((EntityCreature) entity, EntityHuman.class, true));
	    MobComponentDefense defense = new MobComponentDefense(9.0);
	    this.addComponent(defense);
	    this.setName(this.getLevel().getName()+" "+this.getType().getName());
	}
	
	public void onCreateAttributes() {
		MobBaseZombieGiant zombie = (MobBaseZombieGiant)this.entity;
		zombie.getAttributeInstance(GenericAttributes.c).setValue(200.0D);
		zombie.getAttributeInstance(GenericAttributes.maxHealth).setValue(5000.0D);
		zombie.getAttributeInstance(GenericAttributes.c).setValue(1.0D);
		zombie.getAttributeInstance(GenericAttributes.c).setValue(200000.0D);
		//AttributeModifier mod = new AttributeModifier()
		zombie.setHealth(5000.0f);
	}
	
	@Override
	public String getBaseEntity() {
		return entityType;
	}
	
	@Override
	public void onDamage(EntityCreature e, DamageSource damagesource, PathfinderGoalSelector goalSelector, PathfinderGoalSelector targetSelector) {
		goalSelector.a(2, new PathfinderGoalMeleeAttack(e, EntityHuman.class, 1.0D, false));
		for (int i = 0; i < 5; i++) {
			try {
				MobSpawner.spawnCustomMob(MobSpawner.CustomMobType.ANGRYYOBO, this.getLevel(), getLocation(e));
			} catch (CivException e1) {
				e1.printStackTrace();
			}
		}		
	}
	
	@Override
	public String getClassName() {
		return YoboBoss.class.getName();
	}
}
