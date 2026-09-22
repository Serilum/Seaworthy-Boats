package com.natamus.seaworthyboats.functions;

import com.natamus.collective.functions.MessageFunctions;
import com.natamus.seaworthyboats.config.ConfigHandler;
import com.natamus.seaworthyboats.data.BoatTier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Prediction;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.entity.vehicle.boat.AbstractChestBoat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BoatFunctions {

	// Chest boats with contents need a second sneak-click to confirm pickup (it spills the chest). Maps each boat to the tick its arm window expires; server-side only.
	private static final Map<UUID, Long> chestPickupArmed = new HashMap<UUID, Long>();
	private static final long CHEST_PICKUP_WINDOW = 40L;

	public static boolean tryReinforce(AbstractBoat boat, Player player, ItemStack stack) {
		int materialTier = BoatTier.getTierForMaterial(stack.getItem());
		if (materialTier == 0) {
			return false;
		}

		int currentTier = BoatTier.getTier(boat);
		if (materialTier <= currentTier) {
			return false;
		}

		if (ConfigHandler.sequentialUpgrades && materialTier != currentTier + 1) {
			return false;
		}

		Level level = boat.level();
		if (!level.isClientSide()) {
			int cost = BoatTier.getUpgradeCost(materialTier);

			boolean creative = player.getAbilities().instabuild;
			if (!creative && stack.getCount() < cost) {
				MessageFunctions.sendMessage(player, Component.translatable("collective.seaworthyboats.message.notenough", cost, stack.getHoverName()).withStyle(ChatFormatting.RED));
				return true;
			}

			if (!creative) {
				stack.shrink(cost);
			}

			BoatTier.setTier(boat, materialTier);

			MessageFunctions.sendMessage(player, Component.translatable("collective.seaworthyboats.message.reinforced", stack.getHoverName()).withStyle(ChatFormatting.GREEN));
			playWorkEffects(level, boat.getX(), boat.getY() + 0.5, boat.getZ());
		}

		return true;
	}

	public static void playWorkEffects(Level level, double x, double y, double z) {
		level.playSound(null, x, y, z, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.6F, 1.0F);
		if (level instanceof ServerLevel serverLevel) {
			serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, x, y, z, 10, 0.4, 0.4, 0.4, 0.0);
		}
	}

	public static boolean tryPickUp(AbstractBoat boat, Player player) {
		Level level = boat.level();
		if (level.isClientSide()) {
			return true;
		}

		if (boat instanceof AbstractChestBoat && !((Container)boat).isEmpty()) {
			long now = level.getGameTime();
			Long armedUntil = chestPickupArmed.get(boat.getUUID());
			if (armedUntil == null || now > armedUntil) {
				chestPickupArmed.put(boat.getUUID(), now + CHEST_PICKUP_WINDOW);
				player.sendOverlayMessage(Component.translatable("collective.seaworthyboats.message.chestpickupconfirm").withStyle(ChatFormatting.YELLOW));
				return true;
			}

			chestPickupArmed.remove(boat.getUUID());
		}

		return pickUp(boat, player);
	}

	public static boolean pickUp(AbstractBoat boat, Player player) {
		Level level = boat.level();
		if (level.isClientSide()) {
			return true;
		}

		ItemStack stack = boat.getPickResult();
		if (stack.isEmpty()) {
			return false;
		}

		if (!player.addItem(stack)) {
			player.drop(stack, false, Prediction.PREDICTED);
		}

		boat.discard();
		return true;
	}
}
