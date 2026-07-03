package com.natamus.seaworthyboats.mixin;

import com.natamus.seaworthyboats.data.BoatTier;
import com.natamus.seaworthyboats.functions.BoatFunctions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractBoat.class, priority = 1001)
public class AbstractBoatMixin {

	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/boat/AbstractBoat;setDamage(F)V"))
	public void tick_disableHealthRecovery(AbstractBoat boat, float damage) {
		// Disable tick recovery
	}

	@ModifyConstant(method = "controlBoat", constant = @Constant(floatValue = 0.04F))
	public float controlBoat_tierSpeed(float original) {
		AbstractBoat boat = (AbstractBoat)(Object)this;
		return (float)(original * BoatTier.getSpeedMultiplier(BoatTier.getTier(boat)));
	}

	@Inject(method = "interact", at = @At("HEAD"), cancellable = true)
	public void interact(Player player, InteractionHand hand, Vec3 location, CallbackInfoReturnable<InteractionResult> cir) {
		AbstractBoat boat = (AbstractBoat)(Object)this;

		if (hand == InteractionHand.MAIN_HAND && player.isShiftKeyDown() && player.getItemInHand(hand).isEmpty()) {
			if (BoatFunctions.tryPickUp(boat, player)) {
				cir.setReturnValue(InteractionResult.SUCCESS);
			}
		}
	}

	@Inject(method = "getPickResult", at = @At("RETURN"))
	public void getPickResult(CallbackInfoReturnable<ItemStack> cir) {
		AbstractBoat boat = (AbstractBoat)(Object)this;
		ItemStack stack = cir.getReturnValue();
		if (stack.isEmpty()) {
			return;
		}

		int tier = BoatTier.getTier(boat);
		if (tier > 0) {
			BoatTier.saveTierToStack(stack, tier);
		}

		float damage = boat.getDamage();
		if (damage > 0.0F) {
			BoatTier.saveDamageToStack(stack, damage);
		}
	}

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	public void addAdditionalSaveData(ValueOutput output, CallbackInfo ci) {
		AbstractBoat boat = (AbstractBoat)(Object)this;

		output.putFloat("seaworthyboats_damage", boat.getDamage());
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	public void readAdditionalSaveData(ValueInput input, CallbackInfo ci) {
		AbstractBoat boat = (AbstractBoat)(Object)this;

		boat.setDamage(input.getFloatOr("seaworthyboats_damage", 0.0F));
	}
}
