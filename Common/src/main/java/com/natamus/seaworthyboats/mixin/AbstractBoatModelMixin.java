package com.natamus.seaworthyboats.mixin;

import com.natamus.seaworthyboats.renderer.ITieredRenderState;
import com.natamus.seaworthyboats.functions.BoatRenderFunctions;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.object.boat.AbstractBoatModel;
import net.minecraft.client.renderer.entity.state.BoatRenderState;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractBoatModel.class, priority = 1001)
public class AbstractBoatModelMixin {

	@Shadow @Final private ModelPart leftPaddle;
	@Shadow @Final private ModelPart rightPaddle;

	@Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/BoatRenderState;)V", at = @At("TAIL"))
	public void setupAnim(BoatRenderState state, CallbackInfo ci) {
		float offset = ((ITieredRenderState)state).seaworthyboats_getTier() > 0 ? BoatRenderFunctions.THICKNESS : 0.0F;
		this.leftPaddle.z = 9.0F + offset;
		this.rightPaddle.z = -9.0F - offset;
	}
}
