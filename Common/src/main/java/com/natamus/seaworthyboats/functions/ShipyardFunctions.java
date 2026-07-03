package com.natamus.seaworthyboats.functions;

import com.natamus.collective.functions.MessageFunctions;
import com.natamus.seaworthyboats.block.ShipyardBlock;
import com.natamus.seaworthyboats.block.ShipyardMode;
import com.natamus.seaworthyboats.config.ConfigHandler;
import com.natamus.seaworthyboats.data.BoatTier;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ShipyardFunctions {

	public static Boat getDockedBoat(Level level, BlockPos pos) {
		AABB area = new AABB(pos).inflate(ConfigHandler.shipyardRange);
		List<Boat> boats = level.getEntitiesOfClass(Boat.class, area);

		Boat nearest = null;
		double nearestDistance = Double.MAX_VALUE;
		Vec3 center = Vec3.atCenterOf(pos);
		for (Boat boat : boats) {
			double distance = boat.position().distanceToSqr(center);
			if (distance < nearestDistance) {
				nearestDistance = distance;
				nearest = boat;
			}
		}

		return nearest;
	}

	public static int getRepairCost(Boat boat) {
		float damage = boat.getDamage();
		if (damage <= 0.0F) {
			return 0;
		}

		return (int)Math.ceil(damage / ConfigHandler.healthPerPlank);
	}

	public static ItemInteractionResult toggle(Level level, BlockPos pos, BlockState state, Player player) {
		if (level.isClientSide()) {
			return ItemInteractionResult.SUCCESS;
		}

		ShipyardMode next = ShipyardMode.UPGRADE;
		if (state.getValue(ShipyardBlock.MODE) == ShipyardMode.UPGRADE) {
			next = ShipyardMode.REPAIR;
		}
		level.setBlock(pos, state.setValue(ShipyardBlock.MODE, next), 3);

		float pitch = 0.7F;
		if (next == ShipyardMode.UPGRADE) {
			pitch = 1.0F;
		}

		level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.4F, pitch);
		if (next == ShipyardMode.UPGRADE) {
			player.displayClientMessage(Component.translatable("collective.seaworthyboats.message.mode.upgrade").withStyle(ChatFormatting.AQUA), true);
		}
		else {
			player.displayClientMessage(Component.translatable("collective.seaworthyboats.message.mode.repair").withStyle(ChatFormatting.AQUA), true);
		}

		return ItemInteractionResult.SUCCESS;
	}

	public static ItemInteractionResult tryAction(Level level, BlockPos pos, BlockState state, Player player, ItemStack stack) {
		if (stack.getItem() instanceof BoatItem && BoatTier.getDamageFromStack(stack) > 0.0F) {
			return repairItem(level, pos, player, stack);
		}

		Boat boat = getDockedBoat(level, pos);

		if (state.getValue(ShipyardBlock.MODE) == ShipyardMode.REPAIR) {
			return repair(level, boat, player, stack);
		}

		if (boat == null) {
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}

		boolean upgraded = BoatFunctions.tryReinforce(boat, player, stack);
		if (upgraded) {
			return ItemInteractionResult.SUCCESS;
		}

		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	private static ItemInteractionResult repair(Level level, Boat boat, Player player, ItemStack stack) {
		if (!stack.is(ItemTags.PLANKS)) {
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}

		if (boat == null || boat.getDamage() <= 0.0F) {
			return ItemInteractionResult.SUCCESS;
		}

		if (!level.isClientSide()) {
			boolean creative = player.getAbilities().instabuild;
			int needed = getRepairCost(boat);
			int available = needed;
			if (!creative) {
				available = Math.min(needed, stack.getCount());
			}

			float healed = available * (float)ConfigHandler.healthPerPlank;
			boat.setDamage(Math.max(0.0F, boat.getDamage() - healed));

			if (!creative) {
				stack.shrink(available);
			}

			MessageFunctions.sendMessage(player, Component.translatable("collective.seaworthyboats.message.repaired").withStyle(ChatFormatting.GREEN));
			BoatFunctions.playWorkEffects(level, boat.getX(), boat.getY() + 0.5, boat.getZ());
		}

		return ItemInteractionResult.SUCCESS;
	}

	private static ItemInteractionResult repairItem(Level level, BlockPos pos, Player player, ItemStack stack) {
		if (!level.isClientSide()) {
			float damage = BoatTier.getDamageFromStack(stack);
			int needed = (int)Math.ceil(damage / ConfigHandler.healthPerPlank);
			boolean creative = player.getAbilities().instabuild;

			int available = needed;
			if (!creative) {
				available = consumePlanks(player, needed);
			}

			if (available <= 0) {
				MessageFunctions.sendMessage(player, Component.translatable("collective.seaworthyboats.message.needplanks", needed).withStyle(ChatFormatting.RED));
				return ItemInteractionResult.SUCCESS;
			}

			float healed = available * (float)ConfigHandler.healthPerPlank;
			float newDamage = Math.max(0.0F, damage - healed);
			if (newDamage <= 0.0F) {
				BoatTier.clearDamageFromStack(stack);
			}
			else {
				BoatTier.saveDamageToStack(stack, newDamage);
			}

			MessageFunctions.sendMessage(player, Component.translatable("collective.seaworthyboats.message.repaired").withStyle(ChatFormatting.GREEN));
			BoatFunctions.playWorkEffects(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
		}

		return ItemInteractionResult.SUCCESS;
	}

	private static int consumePlanks(Player player, int needed) {
		int consumed = 0;
		Inventory inventory = player.getInventory();
		for (int slot = 0; slot < inventory.getContainerSize() && consumed < needed; slot++) {
			ItemStack inv = inventory.getItem(slot);
			if (!inv.is(ItemTags.PLANKS)) {
				continue;
			}

			int take = Math.min(needed - consumed, inv.getCount());
			inv.shrink(take);
			consumed += take;
		}

		return consumed;
	}
}
