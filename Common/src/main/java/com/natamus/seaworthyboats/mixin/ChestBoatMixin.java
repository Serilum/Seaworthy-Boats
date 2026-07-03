package com.natamus.seaworthyboats.mixin;

import com.natamus.seaworthyboats.functions.BoatFunctions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ChestBoat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ChestBoat.class, priority = 1001)
public class ChestBoatMixin {

	@Inject(method = "interact(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;", at = @At("HEAD"), cancellable = true)
	public void interact(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		ChestBoat boat = (ChestBoat)(Object)this;

		if (hand == InteractionHand.MAIN_HAND && player.isShiftKeyDown() && player.getItemInHand(hand).isEmpty()) {
			if (BoatFunctions.tryPickUp(boat, player)) {
				cir.setReturnValue(InteractionResult.SUCCESS);
			}
		}
	}
}
