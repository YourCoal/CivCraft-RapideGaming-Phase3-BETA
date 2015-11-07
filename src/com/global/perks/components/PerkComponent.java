package com.global.perks.components;

import java.sql.SQLException;
import java.util.HashMap;

import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Resident;
import com.civcraft.util.CivColor;
import com.global.perks.NotVerifiedException;
import com.global.perks.Perk;


public class PerkComponent {
	
	private HashMap<String, String> attributes = new HashMap<String, String>();
	private String name;
	private Perk parent;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getString(String key) {
		return attributes.get(key);
	}
	
	public double getDouble(String key) {
		return Double.valueOf(attributes.get(key));
	}
	
	public void setAttribute(String key, String value) {
		attributes.put(key, value);
	}
	
	public Perk getParent() {
		return parent;
	}
	public void setParent(Perk parent) {
		this.parent = parent;
	}
	
	public void markAsUsed(Resident resident) {
		this.getParent().count--;
		if (this.getParent().count <= 0) {
			resident.perks.remove(this.getParent().getIdent());
		}
		
		try {
			CivGlobal.perkManager.markAsUsed(resident, this.getParent());
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NotVerifiedException e) {
			CivMessage.send(resident, CivColor.Rose+"You're not verified!? Please contact an admin.");
			e.printStackTrace();
		}
	}
	
	public void onActivate(Resident resident) {}
	public void createComponent() {}

}
