package com.natamus.seaworthyboats;

import com.natamus.collective.check.RegisterMod;
import com.natamus.collective.check.ShouldLoadCheck;
import com.natamus.seaworthyboats.forge.config.IntegrateForgeConfig;
import com.natamus.seaworthyboats.forge.events.ForgeClientEvent;
import com.natamus.seaworthyboats.util.Reference;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Reference.MOD_ID)
public class ModForge {

	public ModForge() {
		if (!ShouldLoadCheck.shouldLoad(Reference.MOD_ID)) {
			return;
		}

		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::loadComplete);

		if (FMLEnvironment.dist.equals(Dist.CLIENT)) {
			modEventBus.register(ForgeClientEvent.class);
		}

		setGlobalConstants();
		ModCommon.init();
		ModCommon.registerAssets(modEventBus);

		IntegrateForgeConfig.registerScreen(ModLoadingContext.get());

		RegisterMod.register(Reference.NAME, Reference.MOD_ID, Reference.VERSION, Reference.ACCEPTED_VERSIONS);
	}

	private void loadComplete(final FMLLoadCompleteEvent event) {
		ModCommon.setAssets();
	}

	private static void setGlobalConstants() {

	}
}
