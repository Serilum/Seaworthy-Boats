package com.natamus.seaworthyboats;

import com.natamus.collective.check.RegisterMod;
import com.natamus.collective.check.ShouldLoadCheck;
import com.natamus.seaworthyboats.util.Reference;
import net.fabricmc.api.ModInitializer;

public class ModFabric implements ModInitializer {
	
	@Override
	public void onInitialize() {
		if (!ShouldLoadCheck.shouldLoad(Reference.MOD_ID)) {
			return;
		}

		setGlobalConstants();
		ModCommon.init();

		ModCommon.registerAssets(null);
		ModCommon.setAssets();

		loadEvents();

		RegisterMod.register(Reference.NAME, Reference.MOD_ID, Reference.VERSION, Reference.ACCEPTED_VERSIONS);
	}

	private void loadEvents() {

	}

	private static void setGlobalConstants() {

	}
}
