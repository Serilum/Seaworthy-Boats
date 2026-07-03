package com.natamus.seaworthyboats.mixin;

import com.natamus.seaworthyboats.data.BoatTier;
import com.natamus.seaworthyboats.functions.BoatRenderFunctions;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BoatModel.class, priority = 1001)
public class BoatModelMixin {

	@Shadow @Final private ModelPart leftPaddle;
	@Shadow @Final private ModelPart rightPaddle;

	@Inject(method = "setupAnim(Lnet/minecraft/world/entity/vehicle/Boat;FFFFF)V", at = @At("TAIL"))
	public void setupAnim(Boat boat, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
		float offset = BoatTier.getTier(boat) > 0 ? BoatRenderFunctions.THICKNESS : 0.0F;
		this.leftPaddle.z = 9.0F + offset;
		this.rightPaddle.z = -9.0F - offset;
	}
}
