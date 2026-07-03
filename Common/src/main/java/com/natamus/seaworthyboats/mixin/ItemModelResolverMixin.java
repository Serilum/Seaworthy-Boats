package com.natamus.seaworthyboats.mixin;

import com.natamus.seaworthyboats.functions.BoatRenderFunctions;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemModelResolver.class, priority = 1001)
public class ItemModelResolverMixin {

	@Shadow @Final private ModelManager modelManager;

	@Inject(method = "appendItemLayers", at = @At("TAIL"))
	public void appendItemLayers(ItemStackRenderState output, ItemStack item, ItemDisplayContext displayContext, Level level, ItemOwner owner, int seed, CallbackInfo ci) {
		BoatRenderFunctions.appendItemTrim((ItemModelResolver)(Object)this, this.modelManager, output, item, displayContext, level, owner, seed);
	}
}
