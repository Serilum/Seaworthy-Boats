package com.natamus.seaworthyboats.mixin;

import com.natamus.seaworthyboats.config.ConfigHandler;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Gui.class, priority = 1001)
public class GuiMixin {

	@Inject(method = "renderFood(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/player/Player;II)V", at = @At("HEAD"), cancellable = true)
	public void renderFood(GuiGraphics guiGraphics, Player player, int x, int y, CallbackInfo ci) {
		if (!ConfigHandler.showBoatHealthBar) {
			return;
		}

		if (player.getVehicle() instanceof Boat) {
			ci.cancel();
		}
	}
}
