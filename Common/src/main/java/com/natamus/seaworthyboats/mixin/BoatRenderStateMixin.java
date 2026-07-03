package com.natamus.seaworthyboats.mixin;

import com.natamus.seaworthyboats.renderer.ITieredRenderState;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = BoatRenderState.class, priority = 1001)
public class BoatRenderStateMixin implements ITieredRenderState {

	@Unique
	private int seaworthyboats_tier = 0;

	@Unique
	private boolean seaworthyboats_chest = false;

	@Override
	public int seaworthyboats_getTier() {
		return seaworthyboats_tier;
	}

	@Override
	public void seaworthyboats_setTier(int tier) {
		seaworthyboats_tier = tier;
	}

	@Override
	public boolean seaworthyboats_isChest() {
		return seaworthyboats_chest;
	}

	@Override
	public void seaworthyboats_setChest(boolean chest) {
		seaworthyboats_chest = chest;
	}
}
