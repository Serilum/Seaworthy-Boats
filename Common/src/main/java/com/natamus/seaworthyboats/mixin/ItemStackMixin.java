package com.natamus.seaworthyboats.mixin;

import com.natamus.seaworthyboats.data.BoatTier;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemStack.class, priority = 1001)
public class ItemStackMixin {

	@Inject(method = "getHoverName", at = @At("RETURN"), cancellable = true)
	public void getHoverName(CallbackInfoReturnable<Component> cir) {
		ItemStack stack = (ItemStack)(Object)this;
		if (!(stack.getItem() instanceof BoatItem)) {
			return;
		}

		int tier = BoatTier.getTierFromStack(stack);
		if (tier <= 0) {
			return;
		}

		Component name = Component.translatable("collective.seaworthyboats.reinforcedboat", BoatTier.getTierName(tier), cir.getReturnValue()).withColor(BoatTier.getTierTextColor(tier) & 0xFFFFFF);

		cir.setReturnValue(name);
	}
}
