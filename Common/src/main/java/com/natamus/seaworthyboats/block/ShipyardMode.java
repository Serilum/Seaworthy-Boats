package com.natamus.seaworthyboats.block;

import net.minecraft.util.StringRepresentable;

public enum ShipyardMode implements StringRepresentable {
	REPAIR("repair"),
	UPGRADE("upgrade");

	private final String name;

	ShipyardMode(String name) {
		this.name = name;
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}
