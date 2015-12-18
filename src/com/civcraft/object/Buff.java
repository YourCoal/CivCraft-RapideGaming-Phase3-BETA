package com.civcraft.object;

import java.text.DecimalFormat;

import com.civcraft.config.CivSettings;
import com.civcraft.config.ConfigBuff;

public class Buff {
	
	/* Quick redefines for id/name from yml. */
	public static final String ADVANCED_MINING = "buff_advanced_mining";
	public static final String ADVANCED_TESTING = "buff_advanced_testing";
	public static final String ADVANCED_WATER_TRADING = "buff_advanced_water_trading";
	public static final String FEEDING = "buff_feeding";
	
	public static final String FINE_ART = "buff_fine_art";
	public static final String CONSTRUCTION = "buff_construction";
	public static final String GROWTH_RATE = "buff_year_of_plenty";
	public static final String TRADE = "buff_monopoly";
	public static final String REDUCE_CONSUME = "buff_preservative";	
	public static final String SCIENCE_RATE = "buff_innovation";
	public static final String EXTRA_CULTURE = "buff_doesnotexist";
	public static final String COTTAGE_RATE = "buff_doesnotexist";
	public static final String BARRICADE = "buff_barricade";
	public static final String BARTER = "buff_barter";
	public static final String EXTRACTION = "buff_extraction";
	public static final String FUSION = "buff_fusion";
	public static final String FIRE_BOMB = "buff_fire_bomb";
	public static final String FISHING = "buff_fishing";
	public static final String MEDICINE = "buff_medicine";
	public static final String RUSH = "buff_rush";
	public static final String DEBUFF_PYRAMID_LEECH = "debuff_pyramid_leech";
	
	private ConfigBuff config;
	private String source;
	private String key;
	
	public Buff(String buffkey, String buff_id, String source) {
		config = CivSettings.buffs.get(buff_id);
		setKey(buffkey);
		this.source = source;
	}
	
	@Override
	public int hashCode() {
		return config.id.toString().hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof Buff) {
			Buff otherBuff = (Buff)other;
			if (otherBuff.getConfig().id.equals(this.getConfig().id)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return the config
	 */
	public ConfigBuff getConfig() {
		return config;
	}

	/**
	 * @param config the config to set
	 */
	public void setConfig(ConfigBuff config) {
		this.config = config;
	}

	public boolean isStackable() {
		return config.stackable;
	}
	
	public String getId() {
		return config.id;
	}

	public Object getParent() {
		return config.parent;
	}

	public String getValue() {
		return config.value;
	}
	
	public String getDisplayDouble() {
		try {
			double d = Double.valueOf(config.value);
			DecimalFormat df = new DecimalFormat();
			return df.format(d*100)+"%";
		} catch (NumberFormatException e) {
			return "NAN!";
		}
	}
	
	public String getDisplayInt() {
		try {
			int i = Integer.valueOf(config.value);
			return ""+i;
		} catch (NumberFormatException e) {
			return "NAN!";
		}
	}

	public String getDisplayName() {
		return config.name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
