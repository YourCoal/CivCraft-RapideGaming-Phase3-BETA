package com.civcraft.components;

import com.civcraft.object.CultureChunk;

public abstract class AttributeBiomeBase extends Component {

	public AttributeBiomeBase() {
		this.typeName = "AttributeBiomeBase";
	}
	
	public abstract double getGenerated(CultureChunk cultureChunk);
	public abstract String getAttribute();
}
