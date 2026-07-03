package com.natamus.seaworthyboats.mixin;

import com.natamus.seaworthyboats.renderer.ILayeredRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ItemStackRenderState.class, priority = 1001)
public class ItemStackRenderStateMixin implements ILayeredRenderState {

	@Shadow private int activeLayerCount;
	@Shadow private ItemStackRenderState.LayerRenderState[] layers;

	@Override
	public ItemStackRenderState.LayerRenderState seaworthyboats_getLastLayer() {
		if (activeLayerCount == 0) {
			return null;
		}

		return layers[activeLayerCount - 1];
	}
}
