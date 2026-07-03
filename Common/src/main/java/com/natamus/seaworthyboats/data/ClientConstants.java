package com.natamus.seaworthyboats.data;

import com.natamus.seaworthyboats.util.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public class ClientConstants {
	public static Boat highlightedBoat;

	public static final String[] TRIM_NAMES = new String[] { "boat_trim", "raft_trim", "chest_boat_trim", "chest_raft_trim" };

	public static final ModelResourceLocation[] TRIM_MODELS = new ModelResourceLocation[TRIM_NAMES.length];
	static {
		for (int i = 0; i < TRIM_NAMES.length; i++) {
			TRIM_MODELS[i] = new ModelResourceLocation(Reference.MOD_ID, TRIM_NAMES[i], "inventory");
		}
	}

	// Forge resolves added models by their ModelResourceLocation. The inventory variant maps to models/item/<name>. Fabric overrides this in ModFabricClient.
	public static Function<ItemStack, BakedModel> trimModelResolver = stack ->
		Minecraft.getInstance().getModelManager().getModel(new ModelResourceLocation(Reference.MOD_ID, getTrimName(stack), "inventory"));

	public static String getTrimName(ItemStack stack) {
		String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
		boolean hasChest = path.contains("chest");
		boolean isRaft = path.contains("raft");

		if (isRaft && hasChest) {
			return "chest_raft_trim";
		}
		if (isRaft) {
			return "raft_trim";
		}
		if (hasChest) {
			return "chest_boat_trim";
		}

		return "boat_trim";
	}
}
