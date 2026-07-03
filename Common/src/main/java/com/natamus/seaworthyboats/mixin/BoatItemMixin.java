package com.natamus.seaworthyboats.mixin;

import com.natamus.collective.functions.MessageFunctions;
import com.natamus.seaworthyboats.data.BoatTier;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
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

	@Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
	public boolean use_applyTier(Level level, Entity entity, Level useLevel, Player player, InteractionHand hand) {
		if (entity instanceof Boat boat) {
			ItemStack stack = player.getItemInHand(hand);

			int tier = BoatTier.getTierFromStack(stack);
			if (tier > 0) {
				BoatTier.setTier(boat, tier);
			}

			float damage = BoatTier.getDamageFromStack(stack);
			if (damage > 0.0F) {
				boat.setDamage(damage);
			}
		}

		return level.addFreshEntity(entity);
	}
}
