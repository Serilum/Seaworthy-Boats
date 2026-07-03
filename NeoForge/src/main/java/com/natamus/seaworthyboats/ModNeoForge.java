package com.natamus.seaworthyboats;

import com.natamus.collective.check.RegisterMod;
import com.natamus.collective.check.ShouldLoadCheck;
import com.natamus.seaworthyboats.neoforge.config.IntegrateNeoForgeConfig;
import com.natamus.seaworthyboats.neoforge.events.NeoForgeClientEvent;
import com.natamus.seaworthyboats.util.Reference;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(Reference.MOD_ID)
public class ModNeoForge {
	public ModNeoForge(IEventBus modEventBus) {
		if (!ShouldLoadCheck.shouldLoad(Reference.MOD_ID)) {
			return;
		}

		modEventBus.addListener(this::loadComplete);

		if (FMLEnvironment.dist.equals(Dist.CLIENT)) {
			modEventBus.register(NeoForgeClientEvent.class);
		}

		setGlobalConstants();
		ModCommon.init();
		ModCommon.registerAssets(modEventBus);

		IntegrateNeoForgeConfig.registerScreen(ModLoadingContext.get());

		RegisterMod.register(Reference.NAME, Reference.MOD_ID, Reference.VERSION, Reference.ACCEPTED_VERSIONS);
	}

	private void loadComplete(final FMLLoadCompleteEvent event) {
		ModCommon.setAssets();
	}

	private static void setGlobalConstants() {

	}
}
