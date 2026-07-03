package com.natamus.seaworthyboats.mixin;

import com.natamus.seaworthyboats.config.ConfigHandler;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Hud.class, priority = 1001)
public class HudMixin {

	@Inject(method = "extractFood", at = @At(value = "HEAD"), cancellable = true)
	public void extractFood(GuiGraphicsExtractor graphics, Player player, int x, int y, CallbackInfo ci) {
		if (!ConfigHandler.showBoatHealthBar) {
			return;
		}

		// Hide health bar when in a boat, similar to riding horses
		if (player.getVehicle() instanceof AbstractBoat) {
			ci.cancel();
		}
	}
}
