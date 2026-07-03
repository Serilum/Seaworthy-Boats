package com.natamus.seaworthyboats.mixin;

import com.natamus.collective.functions.MessageFunctions;
import com.natamus.seaworthyboats.data.BoatTier;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BoatItem.class, priority = 1001)
public class BoatItemMixin {

	@Inject(method = "use", at = @At("HEAD"), cancellable = true)
	public void use(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
		ItemStack stack = player.getItemInHand(hand);
		if (!BoatTier.isStackBroken(stack)) {
			return;
		}

		if (!level.isClientSide()) {
			MessageFunctions.sendMessage(player, Component.translatable("collective.seaworthyboats.message.broken").withStyle(ChatFormatting.RED));
		}

		cir.setReturnValue(InteractionResultHolder.fail(stack));
	}

	@Inject(method = "getBoat", at = @At("RETURN"))
	public void getBoat(Level level, HitResult hitResult, ItemStack itemStack, Player player, CallbackInfoReturnable<Boat> cir) {
		Boat boat = cir.getReturnValue();
		if (boat == null) {
			return;
		}

		int tier = BoatTier.getTierFromStack(itemStack);
		if (tier > 0) {
			BoatTier.setTier(boat, tier);
		}

		float damage = BoatTier.getDamageFromStack(itemStack);
		if (damage > 0.0F) {
			boat.setDamage(damage);
		}
	}
}
