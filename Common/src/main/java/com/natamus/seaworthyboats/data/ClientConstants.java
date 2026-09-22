package com.natamus.seaworthyboats.data;

import com.natamus.seaworthyboats.util.Reference;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.item.ItemStack;

public class ClientConstants {
	public static final Identifier HEART_VEHICLE_CONTAINER_SPRITE = Identifier.withDefaultNamespace("hud/heart/vehicle_container");
	public static final Identifier HEART_VEHICLE_FULL_SPRITE = Identifier.withDefaultNamespace("hud/heart/vehicle_full");
	public static final Identifier HEART_VEHICLE_HALF_SPRITE = Identifier.withDefaultNamespace("hud/heart/vehicle_half");

	public static final Identifier ARMOR_EMPTY_SPRITE = Identifier.withDefaultNamespace("hud/armor_empty");
	public static final Identifier ARMOR_FULL_SPRITE = Identifier.withDefaultNamespace("hud/armor_full");

	public static final Identifier BOAT_TRIM_MODEL = Identifier.fromNamespaceAndPath(Reference.MOD_ID, "boat_trim");
	public static final Identifier RAFT_TRIM_MODEL = Identifier.fromNamespaceAndPath(Reference.MOD_ID, "raft_trim");
	public static final Identifier CHEST_BOAT_TRIM_MODEL = Identifier.fromNamespaceAndPath(Reference.MOD_ID, "chest_boat_trim");
	public static final Identifier CHEST_RAFT_TRIM_MODEL = Identifier.fromNamespaceAndPath(Reference.MOD_ID, "chest_raft_trim");

	public static final Identifier BOAT_TRIM_TEXTURE = Identifier.fromNamespaceAndPath(Reference.MOD_ID, "textures/entity/boat_trim.png");

	public static AbstractBoat highlightedBoat;

	public static Identifier getTrimModel(ItemStack stack) {
		String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
		boolean hasChest = path.contains("chest");
		boolean isRaft = path.contains("raft");

		if (isRaft && hasChest) {
			return CHEST_RAFT_TRIM_MODEL;
		}
		if (isRaft) {
			return RAFT_TRIM_MODEL;
		}
		if (hasChest) {
			return CHEST_BOAT_TRIM_MODEL;
		}

		return BOAT_TRIM_MODEL;
	}
}
