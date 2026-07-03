package com.natamus.seaworthyboats.mixin;

import com.natamus.seaworthyboats.data.BoatTier;
import com.natamus.seaworthyboats.functions.BoatRenderFunctions;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BoatRenderer.class, priority = 1001)
public class BoatRendererMixin {

	@Inject(method = "render(Lnet/minecraft/world/entity/vehicle/Boat;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"))
	public void render(Boat entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
		int tier = BoatTier.getTier(entity);
		if (tier > 0) {
			boolean isRaft = entity.getVariant() == Boat.Type.BAMBOO;
			boolean isChest = entity instanceof ChestBoat;
			BoatRenderFunctions.renderTrim(poseStack, buffer, tier, packedLight, isRaft, isChest);
		}
	}
}
