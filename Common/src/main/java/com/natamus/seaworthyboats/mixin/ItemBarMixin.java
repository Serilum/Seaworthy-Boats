package com.natamus.seaworthyboats.mixin;

import com.natamus.seaworthyboats.config.ConfigHandler;
import com.natamus.seaworthyboats.data.BoatTier;

import net.minecraft.util.Mth;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Item.class, priority = 1001)
public class ItemBarMixin {

	@Inject(method = "isBarVisible", at = @At("HEAD"), cancellable = true)
	public void isBarVisible(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (stack.getItem() instanceof BoatItem && BoatTier.getDamageFromStack(stack) > 0.0F) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "getBarWidth", at = @At("HEAD"), cancellable = true)
	public void getBarWidth(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
		if (stack.getItem() instanceof BoatItem && BoatTier.getDamageFromStack(stack) > 0.0F) {
			cir.setReturnValue(Math.round(13.0F * seaworthyboats_remainingFraction(stack)));
		}
	}

	@Inject(method = "getBarColor", at = @At("HEAD"), cancellable = true)
	public void getBarColor(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
		if (stack.getItem() instanceof BoatItem && BoatTier.getDamageFromStack(stack) > 0.0F) {
			cir.setReturnValue(Mth.hsvToRgb(seaworthyboats_remainingFraction(stack) / 3.0F, 1.0F, 1.0F));
		}
	}

	@Unique
	private static float seaworthyboats_remainingFraction(ItemStack stack) {
		float remaining = ConfigHandler.boatMaxHealth - BoatTier.getDamageFromStack(stack);
		if (remaining < 0.0F) {
			remaining = 0.0F;
		}

		return remaining / ConfigHandler.boatMaxHealth;
	}
}
