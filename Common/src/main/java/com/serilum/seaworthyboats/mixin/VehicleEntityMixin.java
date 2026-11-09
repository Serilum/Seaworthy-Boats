package com.serilum.seaworthyboats.mixin;

import com.serilum.seaworthyboats.config.ConfigHandler;
import com.serilum.seaworthyboats.data.BoatTier;
import com.serilum.seaworthyboats.functions.BoatFunctions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = VehicleEntity.class, priority = 1001)
public class VehicleEntityMixin {

	@ModifyConstant(method = "hurtServer", constant = @Constant(floatValue = 10.0F))
	public float hurtServer_damageToHealth(float original) {
		if (!((VehicleEntity)(Object)this instanceof AbstractBoat boat)) {
			return original;
		}

		double reduction = BoatTier.getDamageReduction(BoatTier.getTier(boat));
		return (float)(1.0 - reduction);
	}

	@ModifyConstant(method = "hurtServer", constant = @Constant(floatValue = 40.0F))
	public float hurtServer_maxHealth(float original) {
		if (!((VehicleEntity)(Object)this instanceof AbstractBoat)) {
			return original;
		}

		return (float)ConfigHandler.boatMaxHealth;
	}

	@ModifyArg(method = "destroy(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/Item;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/VehicleEntity;spawnAtLocation(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/item/ItemEntity;"), index = 1)
	public ItemStack destroy_saveTier(ItemStack stack) {
		if ((VehicleEntity)(Object)this instanceof AbstractBoat boat) {
			int tier = BoatTier.getTier(boat);
			if (tier > 0) {
				BoatTier.saveTierToStack(stack, tier);
			}

			float damage = boat.getDamage();
			if (damage > 0.0F) {
				BoatTier.saveDamageToStack(stack, damage);
			}
		}

		return stack;
	}

	@Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
	public void hurtServer_playerHit(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		if (!((VehicleEntity)(Object)this instanceof AbstractBoat boat)) {
			return;
		}

		if (!(source.getEntity() instanceof Player player)) {
			return;
		}

		if (source.getDirectEntity() == player && BoatFunctions.canCrouchHitToPickUp(player)) {
			BoatFunctions.tryPickUp(boat, player);
			cir.setReturnValue(false);
			return;
		}

		if (!ConfigHandler.disablePlayerBoatDamage) {
			return;
		}

		BoatFunctions.sendPickupHint(player);
		cir.setReturnValue(false);
	}
}
