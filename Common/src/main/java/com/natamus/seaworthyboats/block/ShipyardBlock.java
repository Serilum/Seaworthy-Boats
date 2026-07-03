package com.natamus.seaworthyboats.block;

import com.natamus.seaworthyboats.functions.ShipyardFunctions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

public class ShipyardBlock extends Block {
	public static final EnumProperty<ShipyardMode> MODE = EnumProperty.create("mode", ShipyardMode.class);

	public ShipyardBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(MODE, ShipyardMode.REPAIR));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(MODE);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		ItemStack itemStack = player.getItemInHand(hand);
		if (player.isShiftKeyDown()) {
			return ShipyardFunctions.toggle(level, pos, state, player);
		}

		return ShipyardFunctions.tryAction(level, pos, state, player, itemStack);
	}
}
