package com.natamus.seaworthyboats.data;

import com.natamus.seaworthyboats.util.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public class ClientConstants {
	public static final ResourceLocation HEART_VEHICLE_CONTAINER_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/vehicle_container");
	public static final ResourceLocation HEART_VEHICLE_FULL_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/vehicle_full");
	public static final ResourceLocation HEART_VEHICLE_HALF_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/vehicle_half");

	public static final ResourceLocation ARMOR_EMPTY_SPRITE = ResourceLocation.withDefaultNamespace("hud/armor_empty");
	public static final ResourceLocation ARMOR_FULL_SPRITE = ResourceLocation.withDefaultNamespace("hud/armor_full");

	public static Boat highlightedBoat;

	public static final String[] TRIM_NAMES = new String[] { "boat_trim", "raft_trim", "chest_boat_trim", "chest_raft_trim" };

	public static final ModelResourceLocation[] TRIM_MODELS = new ModelResourceLocation[TRIM_NAMES.length];
	static {
		for (int i = 0; i < TRIM_NAMES.length; i++) {
			TRIM_MODELS[i] = new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "item/" + TRIM_NAMES[i]), "standalone");
		}
	}

	// Forge/NeoForge resolve added models by their ModelResourceLocation. Fabric overrides this in ModFabricClient.
	public static Function<ItemStack, BakedModel> trimModelResolver = stack ->
		Minecraft.getInstance().getModelManager().getModel(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "item/" + getTrimName(stack)), "standalone"));

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
