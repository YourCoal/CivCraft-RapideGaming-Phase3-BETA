package com.civcraft.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CivCache {
	
	public static Map<UUID, ArrowFiredCache> arrowsFired = new HashMap<UUID, ArrowFiredCache>();
	public static Map<UUID, CannonFiredCache> cannonBallsFired = new HashMap<UUID, CannonFiredCache>();
}
