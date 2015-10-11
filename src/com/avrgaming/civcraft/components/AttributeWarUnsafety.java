package com.avrgaming.civcraft.components;

import com.avrgaming.civcraft.structure.Buildable;

public class AttributeWarUnsafety extends Component {

	public double value;
	/*
	 * This is another special case. We only want to use this to reduce war unsafety. 
	 * We dont want to generate any actual safety, just reduce the unsafety caused from war.
	 */
	
	@Override
	public void createComponent(Buildable buildable, boolean async) {
		super.createComponent(buildable, async);
		value = this.getDouble("value");
	}
	
}
