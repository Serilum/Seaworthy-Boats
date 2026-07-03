package com.natamus.seaworthyboats.mixin;

import com.natamus.seaworthyboats.config.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = Gui.class, priority = 1001)
public class GuiMixin {

	// 1.20.1 has no renderFood method: the hunger bar is drawn inline in renderPlayerHealth. The food icons sit on the v=27 row of icons.png, so skip those blits while riding a boat to hide the hunger bar like a mount does.
	@Redirect(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"))
	public void renderPlayerHealth_hideBoatFood(GuiGraphics guiGraphics, ResourceLocation atlasLocation, int x, int y, int u, int v, int width, int height) {
		if (ConfigHandler.showBoatHealthBar && v == 27) {
			Player player = Minecraft.getInstance().player;
			if (player != null && player.getVehicle() instanceof Boat) {
				return;
			}
		}

		guiGraphics.blit(atlasLocation, x, y, u, v, width, height);
	}
}
