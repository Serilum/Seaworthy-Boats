package com.natamus.seaworthyboats.mixin;

import com.natamus.seaworthyboats.config.ConfigHandler;
import com.natamus.seaworthyboats.data.BoatTier;
import com.natamus.seaworthyboats.functions.BoatFunctions;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Boat.class, priority = 1001)
public class BoatMixin {

	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/Boat;setDamage(F)V"))
	public void tick_disableHealthRecovery(Boat boat, float damage) {
		// Disable tick recovery
	}

	@ModifyConstant(method = "controlBoat", constant = @Constant(floatValue = 0.04F))
	public float controlBoat_tierSpeed(float original) {
		Boat boat = (Boat)(Object)this;
		return (float)(original * BoatTier.getSpeedMultiplier(BoatTier.getTier(boat)));
	}

	@ModifyConstant(method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", constant = @Constant(floatValue = 10.0F))
	public float hurt_damageToHealth(float original) {
		Boat boat = (Boat)(Object)this;
		double reduction = BoatTier.getDamageReduction(BoatTier.getTier(boat));
		return (float)(1.0 - reduction);
	}

	@ModifyConstant(method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", constant = @Constant(floatValue = 40.0F))
	public float hurt_maxHealth(float original) {
		return (float)ConfigHandler.boatMaxHealth;
	}

	@Inject(method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At("HEAD"), cancellable = true)
	public void hurt_disablePlayerDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		if (!ConfigHandler.disablePlayerBoatDamage) {
			return;
		}

		if (!(source.getEntity() instanceof Player player)) {
			return;
		}

		player.displayClientMessage(Component.translatable("collective.seaworthyboats.message.pickuphint").withStyle(ChatFormatting.YELLOW), true);
		cir.setReturnValue(false);
	}

	@Redirect(method = "destroy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/Boat;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;"))
	public ItemEntity destroy_saveTier(Boat boat, ItemLike dropItem) {
		ItemStack stack = new ItemStack(dropItem);

		int tier = BoatTier.getTier(boat);
		if (tier > 0) {
			BoatTier.saveTierToStack(stack, tier);
		}

		float damage = boat.getDamage();
		if (damage > 0.0F) {
			BoatTier.saveDamageToStack(stack, damage);
		}

		return boat.spawnAtLocation(stack);
	}

	@Inject(method = "interact(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;", at = @At("HEAD"), cancellable = true)
	public void interact(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		Boat boat = (Boat)(Object)this;

		if (hand == InteractionHand.MAIN_HAND && player.isShiftKeyDown() && player.getItemInHand(hand).isEmpty()) {
			if (BoatFunctions.pickUp(boat, player)) {
				cir.setReturnValue(InteractionResult.SUCCESS);
			}
		}
	}

	@Inject(method = "getPickResult", at = @At("RETURN"))
	public void getPickResult(CallbackInfoReturnable<ItemStack> cir) {
		Boat boat = (Boat)(Object)this;
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
	public void addAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
		Boat boat = (Boat)(Object)this;

		compound.putFloat("seaworthyboats_damage", boat.getDamage());
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	public void readAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
		Boat boat = (Boat)(Object)this;

		boat.setDamage(compound.getFloat("seaworthyboats_damage"));
	}
}
