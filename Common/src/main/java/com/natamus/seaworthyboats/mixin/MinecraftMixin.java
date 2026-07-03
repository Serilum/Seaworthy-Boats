package com.natamus.seaworthyboats.mixin;

import com.natamus.seaworthyboats.data.ClientConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Minecraft.class, priority = 1001)
public class MinecraftMixin {

	@Inject(method = "shouldEntityAppearGlowing", at = @At("HEAD"), cancellable = true)
	private void seaworthyboats_glowDockedBoat(Entity entity, CallbackInfoReturnable<Boolean> cir) {
		if (entity == ClientConstants.highlightedBoat) {
			cir.setReturnValue(true);
		}
	}
}
