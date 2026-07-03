package com.natamus.seaworthyboats;

import com.natamus.collective.functions.CreativeModeTabFunctions;
import com.natamus.collective.globalcallbacks.CollectiveGuiCallback;
import com.natamus.collective.services.Services;
import com.natamus.seaworthyboats.block.ShipyardBlock;
import com.natamus.seaworthyboats.config.ConfigHandler;
import com.natamus.seaworthyboats.data.SeaworthyBlocks;
import com.natamus.seaworthyboats.events.GUIEvent;
import com.natamus.seaworthyboats.util.Reference;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class ModCommon {

	public static void init() {
		ConfigHandler.initConfig();
		load();
	}

	private static void load() {
		if (Services.MODLOADER.isClientSide()) {
			CollectiveGuiCallback.ON_GUI_RENDER.register(((guiGraphics, deltaTracker) -> {
				GUIEvent.renderOverlay(guiGraphics, deltaTracker);
			}));
		}
	}

	public static void registerAssets(Object modEventBusObject) {
		ResourceKey<CreativeModeTab> functionalBlocksResourceKey = CreativeModeTabFunctions.getCreativeModeTabResourceKey("functional_blocks");

		Services.REGISTERBLOCK.registerBlockWithItem(
			modEventBusObject,
			ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "shipyard"),
			() -> new ShipyardBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5F, 3.0F).sound(SoundType.WOOD)),
			functionalBlocksResourceKey,
			true
		);
	}

	public static void setAssets() {
		SeaworthyBlocks.SHIPYARD = (ShipyardBlock)Services.REGISTERBLOCK.getRegisteredBlockWithItem(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "shipyard"));
	}
}
