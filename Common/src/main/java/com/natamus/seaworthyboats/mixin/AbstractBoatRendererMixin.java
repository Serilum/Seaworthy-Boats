package com.natamus.seaworthyboats.mixin;

import com.natamus.seaworthyboats.data.BoatTier;
import com.natamus.seaworthyboats.renderer.ITieredRenderState;
import com.natamus.seaworthyboats.functions.BoatRenderFunctions;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.object.boat.RaftModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.AbstractBoatRenderer;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.entity.vehicle.boat.AbstractChestBoat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractBoatRenderer.class, priority = 1001)
public abstract class AbstractBoatRendererMixin {

	@Shadow protected abstract EntityModel<BoatRenderState> model();

	@Inject(method = "extractRenderState(Lnet/minecraft/world/entity/vehicle/boat/AbstractBoat;Lnet/minecraft/client/renderer/entity/state/BoatRenderState;F)V", at = @At("TAIL"))
	public void extractRenderState(AbstractBoat entity, BoatRenderState state, float partialTicks, CallbackInfo ci) {
		((ITieredRenderState)state).seaworthyboats_setTier(BoatTier.getTier(entity));
		((ITieredRenderState)state).seaworthyboats_setChest(entity instanceof AbstractChestBoat);
	}

	@Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/BoatRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"))
	public void submit(BoatRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, CallbackInfo ci) {
		int tier = ((ITieredRenderState)state).seaworthyboats_getTier();

		if (tier > 0) {
			boolean isRaft = this.model() instanceof RaftModel;
			boolean isChest = ((ITieredRenderState)state).seaworthyboats_isChest();
			BoatRenderFunctions.renderTrim(poseStack, submitNodeCollector, tier, state.lightCoords, isRaft, isChest);
		}
	}
}
