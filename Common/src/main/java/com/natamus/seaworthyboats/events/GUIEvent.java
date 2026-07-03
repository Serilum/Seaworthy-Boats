package com.natamus.seaworthyboats.events;

import com.natamus.collective.functions.GUIFunctions;
import com.natamus.seaworthyboats.block.ShipyardBlock;
import com.natamus.seaworthyboats.block.ShipyardMode;
import com.natamus.seaworthyboats.config.ConfigHandler;
import com.natamus.seaworthyboats.data.BoatTier;
import com.natamus.seaworthyboats.data.ClientConstants;
import com.natamus.seaworthyboats.functions.ShipyardFunctions;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;

public class GUIEvent {
	private static final Minecraft mc = Minecraft.getInstance();

	public static void renderOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
		ClientConstants.highlightedBoat = null;

		if (GUIFunctions.shouldHideGUI()) {
			return;
		}

		LocalPlayer player = mc.player;
		if (player == null) {
			return;
		}

		renderShipyardInfo(guiGraphics, player);

		if (!ConfigHandler.showBoatHealthBar) {
			return;
		}

		Entity vehicle = player.getVehicle();
		if (!(vehicle instanceof Boat boat)) {
			return;
		}

		int maxHealth = ConfigHandler.boatMaxHealth;
		int hearts = (maxHealth + 1) / 2;
		if (hearts > 30) {
			hearts = 30;
		}

		int currentHealth = (int)Math.ceil(maxHealth - boat.getDamage());
		if (currentHealth < 0) {
			currentHealth = 0;
		}

		int xRight = guiGraphics.guiWidth() / 2 + 91;
		int yo = guiGraphics.guiHeight() - 39;

		for (int baseHealth = 0; hearts > 0; baseHealth += 20) {
			int rowHearts = Math.min(hearts, 10);
			hearts -= rowHearts;

			for (int i = 0; i < rowHearts; i++) {
				int xo = xRight - i * 8 - 9;
				guiGraphics.blitSprite(ClientConstants.HEART_VEHICLE_CONTAINER_SPRITE, xo, yo, 9, 9);
				if (i * 2 + 1 + baseHealth < currentHealth) {
					guiGraphics.blitSprite(ClientConstants.HEART_VEHICLE_FULL_SPRITE, xo, yo, 9, 9);
				}

				if (i * 2 + 1 + baseHealth == currentHealth) {
					guiGraphics.blitSprite(ClientConstants.HEART_VEHICLE_HALF_SPRITE, xo, yo, 9, 9);
				}
			}

			yo -= 10;
		}

		int tier = BoatTier.getTier(boat);
		if (tier > 0) {
			double maxReduction = BoatTier.getMaxDamageReduction();
			double fraction = maxReduction > 0.0 ? BoatTier.getDamageReduction(tier) / maxReduction : 0.0;
			int armourHalves = (int)Math.round(fraction * 20.0);
			if (armourHalves > 20) {
				armourHalves = 20;
			}

			int armourY = guiGraphics.guiHeight() - 49;
			for (int i = 0; i < 10; i++) {
				int xo = xRight - i * 8 - 9;
				guiGraphics.blitSprite(ClientConstants.ARMOR_EMPTY_SPRITE, xo, armourY, 9, 9);
				if (i * 2 + 1 < armourHalves) {
					guiGraphics.blitSprite(ClientConstants.ARMOR_FULL_SPRITE, xo, armourY, 9, 9);
				}
				else if (i * 2 + 1 == armourHalves) {
					guiGraphics.enableScissor(xo + 4, armourY, xo + 9, armourY + 9);
					guiGraphics.blitSprite(ClientConstants.ARMOR_FULL_SPRITE, xo, armourY, 9, 9);
					guiGraphics.disableScissor();
				}
			}
		}
	}

	private static void renderShipyardInfo(GuiGraphics guiGraphics, LocalPlayer player) {
		HitResult hit = mc.hitResult;
		if (hit == null || hit.getType() != HitResult.Type.BLOCK) {
			return;
		}

		BlockPos pos = ((BlockHitResult)hit).getBlockPos();
		Level level = player.level();
		BlockState state = level.getBlockState(pos);
		if (!(state.getBlock() instanceof ShipyardBlock)) {
			return;
		}

		ShipyardMode mode = state.getValue(ShipyardBlock.MODE);
		Boat boat = ShipyardFunctions.getDockedBoat(level, pos);
		ClientConstants.highlightedBoat = boat;

		List<Component> lines = new ArrayList<>();
		List<Integer> colors = new ArrayList<>();

		lines.add(Component.translatable("block.seaworthyboats.shipyard"));
		colors.add(0xFFE8C878);

		if (mode == ShipyardMode.UPGRADE) {
			lines.add(Component.translatable("collective.seaworthyboats.shipyard.mode.upgrade"));
		}
		else {
			lines.add(Component.translatable("collective.seaworthyboats.shipyard.mode.repair"));
		}
		colors.add(modeColor(mode));

		if (boat == null) {
			lines.add(Component.translatable("collective.seaworthyboats.shipyard.noboat"));
			colors.add(0xFFAAAAAA);
		}
		else {
			lines.add(boatName(boat));
			colors.add(0xFFFFFFFF);
			addActionLines(lines, colors, mode, boat, player.getMainHandItem());
		}

		drawTextBox(guiGraphics, lines, colors, Component.translatable("collective.seaworthyboats.shipyard.switchhint").withStyle(style -> style.withItalic(true)));
	}

	private static void drawTextBox(GuiGraphics guiGraphics, List<Component> lines, List<Integer> colors, Component hint) {
		int padding = 7;
		int lineHeight = 12;

		int maxWidth = 0;
		for (int i = 0; i < lines.size(); i++) {
			float scale = (i == 0) ? 1.0F : 0.8F;
			maxWidth = Math.max(maxWidth, Math.round(mc.font.width(lines.get(i)) * scale));
		}
		maxWidth = Math.max(maxWidth, Math.round(mc.font.width(hint) * 0.6F));

		int boxWidth = maxWidth + padding * 2;
		int boxHeight = padding * 2 + lineHeight + (lines.size() - 1) * (lineHeight - 2) + Math.round(mc.font.lineHeight * 0.6F);

		int centerX = guiGraphics.guiWidth() / 2;
		int top = ConfigHandler.shipyardInfoYOffset;
		int left = centerX - boxWidth / 2;
		int right = centerX + boxWidth / 2;
		int bottom = top + boxHeight;

		int outer = 0xFF1C1207;
		int frame = 0xFF4A3520;
		int body = 0xFF3A2A14;
		int highlight = 0xFF6E522E;
		int rivet = 0xFF120C05;

		guiGraphics.fill(left - 3, top - 3, right + 3, bottom + 3, outer);
		guiGraphics.fill(left - 2, top - 2, right + 2, bottom + 2, frame);
		guiGraphics.fill(left, top, right, bottom, body);

		guiGraphics.fill(left, top, right, top + 1, highlight);
		guiGraphics.fill(left, top, left + 1, bottom, highlight);
		guiGraphics.fill(left, bottom - 1, right, bottom, outer);
		guiGraphics.fill(right - 1, top, right, bottom, outer);

		guiGraphics.fill(left + 2, top + 2, left + 4, top + 4, rivet);
		guiGraphics.fill(right - 4, top + 2, right - 2, top + 4, rivet);
		guiGraphics.fill(left + 2, bottom - 4, left + 4, bottom - 2, rivet);
		guiGraphics.fill(right - 4, bottom - 4, right - 2, bottom - 2, rivet);

		int textY = top + padding;
		for (int i = 0; i < lines.size(); i++) {
			float scale = (i == 0) ? 1.0F : 0.8F;
			guiGraphics.pose().pushPose();
			guiGraphics.pose().translate(centerX, textY, 0.0);
			guiGraphics.pose().scale(scale, scale, 1.0F);
			guiGraphics.drawCenteredString(mc.font, lines.get(i), 0, 0, colors.get(i));
			guiGraphics.pose().popPose();
			textY += (i == 0) ? lineHeight : lineHeight - 2;
		}

		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(centerX, textY, 0.0);
		guiGraphics.pose().scale(0.6F, 0.6F, 1.0F);
		guiGraphics.drawCenteredString(mc.font, hint, 0, 0, 0xFF808080);
		guiGraphics.pose().popPose();
	}

	private static int modeColor(ShipyardMode mode) {
		if (mode == ShipyardMode.UPGRADE) {
			return 0xFF55FFFF;
		}

		return 0xFF7FE08A;
	}

	private static Component boatName(Boat boat) {
		// getHoverName() already applies the reinforced prefix + tier colour via ItemStackMixin; wrapping it again doubles the prefix.
		return boat.getPickResult().getHoverName();
	}

	private static void addActionLines(List<Component> lines, List<Integer> colors, ShipyardMode mode, Boat boat, ItemStack held) {
		if (mode == ShipyardMode.REPAIR) {
			int cost = ShipyardFunctions.getRepairCost(boat);
			if (cost <= 0) {
				lines.add(Component.translatable("collective.seaworthyboats.shipyard.fullhealth"));
			}
			else {
				lines.add(Component.translatable("collective.seaworthyboats.shipyard.repair", cost));
			}
			colors.add(0xFFFFFFFF);
			return;
		}

		int tier = BoatTier.getTier(boat);
		if (tier >= BoatTier.HIGHEST_TIER) {
			lines.add(Component.translatable("collective.seaworthyboats.shipyard.maxtier"));
			colors.add(0xFFFFFFFF);
			return;
		}

		if (ConfigHandler.sequentialUpgrades) {
			int nextTier = tier + 1;
			int cost = BoatTier.getUpgradeCost(nextTier);
			lines.add(Component.translatable("collective.seaworthyboats.shipyard.upgrade", BoatTier.getTierName(nextTier), cost, BoatTier.getTierUnit(nextTier, cost)));
			colors.add(0xFFFFFFFF);
			return;
		}

		lines.add(Component.translatable("collective.seaworthyboats.shipyard.available"));
		colors.add(0xFFFFFFFF);

		int heldTier = BoatTier.getTierForMaterial(held.getItem());
		if (heldTier > tier) {
			int cost = BoatTier.getUpgradeCost(heldTier);
			lines.add(Component.translatable("collective.seaworthyboats.shipyard.upgrade", BoatTier.getTierName(heldTier), cost, BoatTier.getTierUnit(heldTier, cost)));
			colors.add(0xFFFFFFFF);
		}
		else {
			lines.add(Component.translatable("collective.seaworthyboats.shipyard.holdmaterial"));
			colors.add(0xFFAAAAAA);
		}
	}
}
