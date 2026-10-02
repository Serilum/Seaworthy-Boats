package com.serilum.seaworthyboats.forge.events;

import com.serilum.seaworthyboats.data.ClientConstants;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeClientEvent {
	@SubscribeEvent
	public static void onRegisterAdditionalModels(ModelEvent.RegisterAdditional e) {
		for (ModelResourceLocation model : ClientConstants.TRIM_MODELS) {
			e.register(model);
		}
	}
}
