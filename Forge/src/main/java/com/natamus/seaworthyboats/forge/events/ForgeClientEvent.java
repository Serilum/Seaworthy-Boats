package com.natamus.seaworthyboats.forge.events;

import com.natamus.seaworthyboats.data.ClientConstants;
import com.natamus.seaworthyboats.events.GUIEvent;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeClientEvent {
	@SubscribeEvent
	public static void onRegisterAdditionalModels(ModelEvent.RegisterAdditional e) {
		for (ModelResourceLocation model : ClientConstants.TRIM_MODELS) {
			e.register(model);
		}
	}

	@SubscribeEvent
	public static void onRegisterOverlays(RegisterGuiOverlaysEvent e) {
		e.registerAboveAll("seaworthyboats", (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
			GUIEvent.renderOverlay(guiGraphics);
		});
	}
}
