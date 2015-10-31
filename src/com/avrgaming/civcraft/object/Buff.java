/*************************************************************************
 * 
 * AVRGAMING LLC
 * __________________
 * 
 *  [2013] AVRGAMING LLC
 *  All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of AVRGAMING LLC and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to AVRGAMING LLC
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from AVRGAMING LLC.
 */
package com.avrgaming.civcraft.object;

import java.text.DecimalFormat;

import com.avrgaming.civcraft.config.CivSettings;
import com.avrgaming.civcraft.config.ConfigBuff;

public class Buff {
	
	/* Quick redefines for id/name from yml. */
	public static final String CONSTRUCTION = "buff:construction";
	public static final String FINE_ART = "buff:fine_art";
	public static final String FISHING = "buff:fishing";
	public static final String GROWTH_RATE = "buff:year_of_plenty";
	public static final String REDUCE_CONSUME = "buff:preservative";
	public static final String SCIENCE_RATE = "buff:innovation";
	public static final String SHIPPING = "buff:shipping";
	public static final String TRADE = "buff:monopoly";
	public static final String EXTRA_CULTURE = "buff:doesnotexist";
	public static final String COTTAGE_RATE = "buff:doesnotexist";
	public static final String ADVANCED_TOOLING = "buff:advanced_tooling";
	public static final String BARRICADE = "buff:barricade";
	public static final String BARTER = "buff:barter";
	public static final String EXTRACTION = "buff:extraction";
	public static final String FIRE_BOMB = "buff:fire_bomb";
	public static final String MEDICINE = "buff:medicine";
	public static final String RUSH = "buff:rush";
	
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
