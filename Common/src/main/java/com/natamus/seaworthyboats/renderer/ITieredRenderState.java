package com.natamus.seaworthyboats.renderer;

public interface ITieredRenderState {
	int seaworthyboats_getTier();
	void seaworthyboats_setTier(int tier);

	boolean seaworthyboats_isChest();
	void seaworthyboats_setChest(boolean chest);
}
