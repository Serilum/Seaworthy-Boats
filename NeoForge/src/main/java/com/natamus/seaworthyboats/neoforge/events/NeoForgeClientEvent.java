package com.natamus.seaworthyboats.neoforge.events;

import com.natamus.seaworthyboats.data.ClientConstants;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ModelEvent;

public class NeoForgeClientEvent {
	@SubscribeEvent
	public static void onRegisterAdditionalModels(ModelEvent.RegisterAdditional e) {
		for (ModelResourceLocation model : ClientConstants.TRIM_MODELS) {
			e.register(model);
		}
	}
}
