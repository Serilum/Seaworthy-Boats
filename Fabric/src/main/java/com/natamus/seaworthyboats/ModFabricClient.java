package com.natamus.seaworthyboats;

import com.natamus.collective.check.ShouldLoadCheck;
import com.natamus.seaworthyboats.data.ClientConstants;
import com.natamus.seaworthyboats.events.GUIEvent;
import com.natamus.seaworthyboats.util.Reference;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class ModFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		if (!ShouldLoadCheck.shouldLoad(Reference.MOD_ID)) {
			return;
		}

		registerEvents();
	}

	private void registerEvents() {
		ModelLoadingPlugin.register(pluginContext -> {
			for (String name : ClientConstants.TRIM_NAMES) {
				pluginContext.addModels(new ResourceLocation(Reference.MOD_ID, "item/" + name));
			}
		});

		ClientConstants.trimModelResolver = stack ->
			Minecraft.getInstance().getModelManager().getModel(
				new ResourceLocation(Reference.MOD_ID, "item/" + ClientConstants.getTrimName(stack)));

		HudRenderCallback.EVENT.register((guiGraphics, tickDelta) -> GUIEvent.renderOverlay(guiGraphics));
	}
}
