package com.moblib.mob;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityVillager;
import net.minecraft.server.v1_8_R3.World;

public class MobBaseVillager extends EntityVillager {

	public MobBaseVillager(World arg0) {
		super(arg0);
	}
	
	
	public static Entity spawn(Location loc, String name) {
		CraftWorld world = (CraftWorld) loc.getWorld();
		World mcWorld = world.getHandle();
		MobBaseVillager zombie = new MobBaseVillager(mcWorld);
		
//		if (name != null) {
//			zombie.setCustomName(name);
//			zombie.setCustomNameVisible(true);
//		}
		
		zombie.setPosition(loc.getX(), loc.getY(), loc.getZ());
		mcWorld.addEntity(zombie, SpawnReason.CUSTOM);
		
		return zombie;
	}

}
