package moblib.moblib;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityBlaze;
import net.minecraft.server.v1_8_R3.EntityCaveSpider;
import net.minecraft.server.v1_8_R3.EntityCreeper;
import net.minecraft.server.v1_8_R3.EntityEnderman;
import net.minecraft.server.v1_8_R3.EntityGiantZombie;
import net.minecraft.server.v1_8_R3.EntityGuardian;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityIronGolem;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityPigZombie;
import net.minecraft.server.v1_8_R3.EntitySkeleton;
import net.minecraft.server.v1_8_R3.EntitySnowman;
import net.minecraft.server.v1_8_R3.EntitySpider;
import net.minecraft.server.v1_8_R3.EntityTypes;
import net.minecraft.server.v1_8_R3.EntityWitch;
import net.minecraft.server.v1_8_R3.EntityWither;
import net.minecraft.server.v1_8_R3.EntityWolf;
import net.minecraft.server.v1_8_R3.EntityZombie;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import moblib.mob.ICustomMob;
import moblib.mob.ISpawnable;
import moblib.mob.MobBaseCaveSpider;
import moblib.mob.MobBaseCreeper;
import moblib.mob.MobBaseEnderman;
import moblib.mob.MobBaseGuardian;
import moblib.mob.MobBaseIronGolem;
import moblib.mob.MobBasePigZombie;
import moblib.mob.MobBaseSkeleton;
import moblib.mob.MobBaseSnowman;
import moblib.mob.MobBaseSpider;
import moblib.mob.MobBaseWitch;
import moblib.mob.MobBaseWither;
import moblib.mob.MobBaseWitherBoss;
import moblib.mob.MobBaseWolf;
import moblib.mob.MobBaseZombie;
import moblib.mob.MobBaseZombieGiant;

public class MobLib extends JavaPlugin {
	
	private static HashMap<UUID, MobLibEntity> entities = new HashMap<UUID, MobLibEntity>();

	@Override
	public void onEnable() {
		//CustomEntityType.registerEntities();
		getCommand("moblib").setExecutor(new MobLibCommand());
		final PluginManager pluginManager = getServer().getPluginManager();
		pluginManager.registerEvents(new MobLibListener(), this);
	}
	
	public static boolean isMobLibEntity(LivingEntity e) {
		
		EntityLiving handle = ((CraftLivingEntity)e).getHandle();
		if (handle instanceof ISpawnable) {
			return true;
		}
		
		return false;
	}
	
	public static void registerEntity(CustomEntityType type) {
		CustomEntityType.registerEntity(type);
	}
	
	public static void registerAllEntities() {
		CustomEntityType.registerEntities();
	}
	
	public static ICustomMob spawnCustom(String customMob, Location loc) {
		try {
			Class<?> customClass = Class.forName(customMob);
			ICustomMob iCustom = (ICustomMob)customClass.newInstance();
			
			String base = iCustom.getBaseEntity();
			if (base == null) {
				System.out.println("ERROR: no base entity set up for "+customMob);
				return null;
			}
			
			Class<?> baseClass = Class.forName(iCustom.getBaseEntity());
			Method spawnMethod = baseClass.getMethod("spawnCustom", Location.class, ICustomMob.class);
			spawnMethod.invoke(null, loc, iCustom);
			
			return iCustom;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | 
				NoSuchMethodException | SecurityException | IllegalArgumentException | 
				InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static MobLibEntity createNamedEntity(String customMob, Location loc, String name) {
		Method spawnMethod;
		try {
			Class<?> customClass = Class.forName(customMob);
			ICustomMob iCustom = (ICustomMob)customClass.newInstance();
			
			String base = iCustom.getBaseEntity();
			if (base == null) {
				System.out.println("ERROR: no base entity set up for "+customMob);
				return null;
			}
			
			Class<?> baseClass = Class.forName(iCustom.getBaseEntity());
			spawnMethod = baseClass.getMethod("spawn", Location.class, ICustomMob.class, String.class);
			Entity entity = (Entity)spawnMethod.invoke(null, loc, iCustom, name);
			MobLibEntity mobEntity = new MobLibEntity(entity.getUniqueID(), entity);
			entities.put(mobEntity.getUid(), mobEntity);
			return mobEntity;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	public enum CustomEntityType {
		PIG_ZOMBIE("PigZombie", 57, EntityType.PIG_ZOMBIE, EntityPigZombie.class, MobBasePigZombie.class),
	    ZOMBIE("Zombie", 54, EntityType.ZOMBIE, EntityZombie.class, MobBaseZombie.class),
	    ZOMBIE_GIANT("Giant", 53, EntityType.GIANT, EntityGiantZombie.class, MobBaseZombieGiant.class),
	    WITCH("Witch", 66, EntityType.WITCH, EntityWitch.class, MobBaseWitch.class),
	    IRON_GOLEM("VillagerGolem", 99, EntityType.IRON_GOLEM, EntityIronGolem.class, MobBaseIronGolem.class),
		WITHER("WitherBoss", 64, EntityType.WITHER, EntityWither.class, MobBaseWither.class),
/////Ferovesa added this these\\\\\ 		
		BLAZE("Blaze", 61, EntityType.BLAZE, EntityBlaze.class, MobBaseWither.class),
		CAVE_SPIDER("CaveSpider", 59, EntityType.CAVE_SPIDER, EntityCaveSpider.class, MobBaseCaveSpider.class),
		CREEPER("Creeper", 50, EntityType.CREEPER, EntityCreeper.class, MobBaseCreeper.class),
		GUARDIAN("Guardian", 68, EntityType.GUARDIAN, EntityGuardian.class, MobBaseGuardian.class),
		SPIDER("Spider", 52, EntityType.SPIDER, EntitySpider.class, MobBaseSpider.class),
		WITHER_BOSS("WitherBoss", 64, EntityType.WITHER, EntityWither.class, MobBaseWitherBoss.class),
		WOLF("Wolf", 95, EntityType.WOLF, EntityWolf.class, MobBaseWolf.class),
		ENDERMAN("Enderman", 58, EntityType.ENDERMAN, EntityEnderman.class, MobBaseEnderman.class),
////COAL ADDED THESE\\\\\
		SKELETON("Skeleton", 51, EntityType.SKELETON, EntitySkeleton.class, MobBaseSkeleton.class),
		SNOWMAN("Snowman", 97, EntityType.SNOWMAN, EntitySnowman.class, MobBaseSnowman.class);
		//WITHER_HEAD("WitherSkull", 19, EntityType.WITHER_SKULL, EntityWitherSkull.class, MobBaseWitherSkull.class);
	    //BAT("Bat", 65, EntityType.BAT, EntityBat.class, MobBaseZombie.class);
	    
	    private String name;
	    private int id;
	    private Class<? extends EntityInsentient> customClass;
	 
		private CustomEntityType(String name, int id, EntityType entityType, Class<? extends EntityInsentient> nmsClass, Class<? extends EntityInsentient> customClass) {
	        this.name = name;
	        this.id = id;
	        this.customClass = customClass;
	    }
	 
	    public String getName() {
	        return this.name;
	    }
	 
	    public int getID() {
	        return this.id;
	    }
	 
	 //   public EntityType getEntityType() {
	  //      return this.entityType;
	  //  }
	 
	  //  public Class<? extends EntityInsentient> getNMSClass() {
	   //     return this.nmsClass;
	   // }
	 
	    public Class<? extends EntityInsentient> getCustomClass() {
	        return this.customClass;
	    }
	 
	    /**
	    * Register our entities.
	    */
	    public static void registerEntities() {
	        for (CustomEntityType entity : values())
	            a(entity.getCustomClass(), entity.getName(), entity.getID());
	    }
	 
	    public static void registerEntity(CustomEntityType type) {
	    	a(type.getCustomClass(), type.getName(), type.getID());
	    }
	    
	    /**
	    * A convenience method.
	    * @param clazz The class.
	    * @param f The string representation of the private static field.
	    * @return The object found
	    * @throws Exception if unable to get the object.
	    */
	    @SuppressWarnings("rawtypes")
	    public static Object getPrivateStatic(Class clazz, String f) throws Exception {
	        Field field = clazz.getDeclaredField(f);
	        field.setAccessible(true);
	        return field.get(null);
	    }
	 
	    @SuppressWarnings({ "unchecked", "rawtypes" })
	    private static void a(Class paramClass, String paramString, int paramInt) {
	        try {
	            ((Map) getPrivateStatic(EntityTypes.class, "c")).put(paramString, paramClass);
	            ((Map) getPrivateStatic(EntityTypes.class, "d")).put(paramClass, paramString);
	            //((Map) getPrivateStatic(EntityTypes.class, "e")).put(50, paramClass);
	            //((Map) getPrivateStatic(EntityTypes.class, "f")).put(50, paramClass);
	            //((Map) getPrivateStatic(EntityTypes.class, "g")).put(50, paramClass);
	            
	            ((Map) getPrivateStatic(EntityTypes.class, "e")).put(Integer.valueOf(paramInt), paramClass);
	            ((Map) getPrivateStatic(EntityTypes.class, "f")).put(paramClass, Integer.valueOf(paramInt));
	            ((Map) getPrivateStatic(EntityTypes.class, "g")).put(paramString, Integer.valueOf(paramInt));
	        } catch (Exception exc) {
	            // Unable to register the new class.
	        }
	    }
	 
	}
	
	
	public static void removeEntity(UUID id) {
		MobLibEntity mobEntity = entities.get(id);
		if (mobEntity != null) {
			mobEntity.getEntity().world.removeEntity(mobEntity.getEntity());
		}
	}

	
	
	
	
	
	

	
}
