package com.natamus.seaworthyboats.mixin;

import com.natamus.seaworthyboats.config.ConfigHandler;
import com.natamus.seaworthyboats.data.BoatTier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = VehicleEntity.class, priority = 1001)
public class VehicleEntityMixin {

	@ModifyConstant(method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", constant = @Constant(floatValue = 10.0F))
	public float hurt_damageToHealth(float original) {
		if (!((VehicleEntity)(Object)this instanceof Boat boat)) {
			return original;
		}

		double reduction = BoatTier.getDamageReduction(BoatTier.getTier(boat));
		return (float)(1.0 - reduction);
	}

	@ModifyConstant(method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", constant = @Constant(floatValue = 40.0F))
	public float hurt_maxHealth(float original) {
		if (!((VehicleEntity)(Object)this instanceof Boat)) {
			return original;
		}

		return (float)ConfigHandler.boatMaxHealth;
	}

	@ModifyArg(method = "destroy(Lnet/minecraft/world/item/Item;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/VehicleEntity;spawnAtLocation(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/item/ItemEntity;"), index = 0)
	public ItemStack destroy_saveTier(ItemStack stack) {
		if ((VehicleEntity)(Object)this instanceof Boat boat) {
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

	@Inject(method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At("HEAD"), cancellable = true)
	public void hurt_disablePlayerDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		if (!ConfigHandler.disablePlayerBoatDamage) {
			return;
		}

		if (!((VehicleEntity)(Object)this instanceof Boat)) {
			return;
		}

		if (!(source.getEntity() instanceof Player player)) {
			return;
		}

		player.displayClientMessage(Component.translatable("collective.seaworthyboats.message.pickuphint").withStyle(ChatFormatting.YELLOW), true);
		cir.setReturnValue(false);
	}
}
