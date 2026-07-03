package com.natamus.seaworthyboats.data;

import com.natamus.collective.services.Services;
import com.natamus.seaworthyboats.config.ConfigHandler;
import com.natamus.seaworthyboats.util.Reference;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BoatTier {
	public static final ResourceLocation BOAT_TIER = new ResourceLocation(Reference.MOD_ID, "tier");
	public static final int HIGHEST_TIER = 5;

	public static int getTier(Boat boat) {
		return Services.ENTITYDATA.getInt(boat, BOAT_TIER, 0);
	}

	public static void setTier(Boat boat, int tier) {
		Services.ENTITYDATA.setInt(boat, BOAT_TIER, tier);
	}

	public static void saveTierToStack(ItemStack stack, int tier) {
		stack.getOrCreateTag().putInt("seaworthyboats_tier", tier);
	}

	public static int getTierFromStack(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		if (tag == null) {
			return 0;
		}
		return tag.getInt("seaworthyboats_tier");
	}

	public static void saveDamageToStack(ItemStack stack, float damage) {
		stack.getOrCreateTag().putFloat("seaworthyboats_damage", damage);
	}

	public static float getDamageFromStack(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		if (tag == null) {
			return 0.0F;
		}
		return tag.getFloat("seaworthyboats_damage");
	}

	public static void clearDamageFromStack(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		if (tag != null) {
			tag.remove("seaworthyboats_damage");
		}
	}

	public static boolean isStackBroken(ItemStack stack) {
		return getDamageFromStack(stack) >= ConfigHandler.boatMaxHealth;
	}

	public static int getTierForMaterial(Item item) {
		if (item == Items.COPPER_INGOT) { return 1; }
		if (item == Items.GOLD_INGOT) { return 2; }
		if (item == Items.IRON_INGOT) { return 3; }
		if (item == Items.DIAMOND) { return 4; }
		if (item == Items.NETHERITE_INGOT) { return 5; }

		return 0;
	}

	public static int getUpgradeCost(int tier) {
		if (tier == HIGHEST_TIER) {
			return Math.max(1, ConfigHandler.materialsPerUpgrade / 2);
		}

		return ConfigHandler.materialsPerUpgrade;
	}

	public static double getDamageReduction(int tier) {
		switch (tier) {
			case 1 -> { return ConfigHandler.copperArmour; }
			case 2 -> { return ConfigHandler.goldArmour; }
			case 3 -> { return ConfigHandler.ironArmour; }
			case 4 -> { return ConfigHandler.diamondArmour; }
			case 5 -> { return ConfigHandler.netheriteArmour; }
			default -> { return 0.0; }
		}
	}

	public static double getMaxDamageReduction() {
		double max = 0.0;
		for (int tier = 1; tier <= HIGHEST_TIER; tier++) {
			max = Math.max(max, getDamageReduction(tier));
		}
		return max;
	}

	public static double getSpeedMultiplier(int tier) {
		switch (tier) {
			case 1 -> { return ConfigHandler.copperSpeed; }
			case 2 -> { return ConfigHandler.goldSpeed; }
			case 3 -> { return ConfigHandler.ironSpeed; }
			case 4 -> { return ConfigHandler.diamondSpeed; }
			case 5 -> { return ConfigHandler.netheriteSpeed; }
			default -> { return 1.0; }
		}
	}

	public static MutableComponent getTierName(int tier) {
		switch (tier) {
			case 1 -> { return Component.translatable("collective.seaworthyboats.tier.copper"); }
			case 2 -> { return Component.translatable("collective.seaworthyboats.tier.gold"); }
			case 3 -> { return Component.translatable("collective.seaworthyboats.tier.iron"); }
			case 4 -> { return Component.translatable("collective.seaworthyboats.tier.diamond"); }
			case 5 -> { return Component.translatable("collective.seaworthyboats.tier.netherite"); }
			default -> { return Component.empty(); }
		}
	}

	public static MutableComponent getTierUnit(int tier, int count) {
		if (tier == 4) {
			if (count == 1) {
				return Component.translatable("collective.seaworthyboats.unit.diamond");
			}
			return Component.translatable("collective.seaworthyboats.unit.diamonds");
		}

		if (count == 1) {
			return Component.translatable("collective.seaworthyboats.unit.ingot");
		}
		return Component.translatable("collective.seaworthyboats.unit.ingots");
	}

	public static int getTierColor(int tier) {
		switch (tier) {
			case 1 -> { return 0xFFB07045; } // copper
			case 2 -> { return 0xFFC2A24E; } // gold
			case 3 -> { return 0xFF9E9E9E; } // iron
			case 4 -> { return 0xFF6FB0A6; } // diamond
			case 5 -> { return 0xFF332D2E; } // netherite
			default -> { return 0xFFFFFFFF; }
		}
	}

	public static int getTierTextColor(int tier) {
		if (tier == 5) {
			return 0xFF7A7276;
		}

		return getTierColor(tier);
	}
}
