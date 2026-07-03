package com.natamus.seaworthyboats.functions;

import com.natamus.seaworthyboats.data.BoatTier;
import com.natamus.seaworthyboats.data.ClientConstants;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

public class BoatRenderFunctions {

	public static final float THICKNESS = 1.5F;

	private static final float PATCH_SIZE = 2.0F;
	private static final float DARK_SHADE = 0.78F;
	private static final float LIGHT_SHADE = 1.16F;
	private static final float EDGE_SHADE = 0.62F;

	public static void appendItemTrim(PoseStack poseStack, MultiBufferSource bufferSource, ItemStack stack, int tier, int light, int overlay) {
		BakedModel trimModel = ClientConstants.trimModelResolver.apply(stack);
		if (trimModel == null) {
			return;
		}

		int color = BoatTier.getTierColor(tier);
		float r = ((color >> 16) & 0xFF) / 255.0F;
		float g = ((color >> 8) & 0xFF) / 255.0F;
		float b = (color & 0xFF) / 255.0F;

		VertexConsumer buffer = bufferSource.getBuffer(Sheets.translucentItemSheet());

		// The trim is a flat overlay, so offset it proud of the boat item on both faces. A single offset buries the back face inside the item and the trim only shows on one side.
		putItemTrimQuads(poseStack, buffer, trimModel, r, g, b, light, overlay, 0.01F);
		putItemTrimQuads(poseStack, buffer, trimModel, r, g, b, light, overlay, -0.01F);
	}

	private static void putItemTrimQuads(PoseStack poseStack, VertexConsumer buffer, BakedModel trimModel, float r, float g, float b, int light, int overlay, float zOffset) {
		poseStack.pushPose();
		poseStack.translate(0.0F, 0.0F, zOffset);

		PoseStack.Pose pose = poseStack.last();
		RandomSource random = RandomSource.create();
		for (Direction dir : Direction.values()) {
			random.setSeed(42L);
			for (BakedQuad quad : trimModel.getQuads(null, dir, random)) {
				buffer.putBulkData(pose, quad, r, g, b, 1.0F, light, overlay);
			}
		}
		random.setSeed(42L);
		for (BakedQuad quad : trimModel.getQuads(null, null, random)) {
			buffer.putBulkData(pose, quad, r, g, b, 1.0F, light, overlay);
		}

		poseStack.popPose();
	}

	public static void renderTrim(PoseStack poseStack, MultiBufferSource bufferSource, int tier, int light, boolean isRaft, boolean isChest) {
		int base = BoatTier.getTierColor(tier);
		int dark = shade(base, DARK_SHADE);
		int highlight = shade(base, LIGHT_SHADE);
		int edge = shade(base, EDGE_SHADE);

		int[] topPalette = new int[] { base, base, base, highlight, dark };

		float baseY = isRaft ? -2.1F : -3.0F;
		float topY = baseY - THICKNESS;
		float grow = (isChest && isRaft) ? 1.05F : 1.0F;
		float ox = (isRaft ? 14.0F : 16.0F) * grow;
		float ix = (isRaft ? 12.0F : 14.0F) * grow;
		float oz = 10.0F * grow;
		float iz = 8.0F * grow;

		poseStack.pushPose();
		poseStack.scale(0.0625F, 0.0625F, 0.0625F);
		VertexConsumer buffer = bufferSource.getBuffer(RenderType.textBackground());
		PoseStack.Pose pose = poseStack.last();

		topStrip(pose, buffer, topPalette, light, topY, ix, -oz, ox, oz);
		topStrip(pose, buffer, topPalette, light, topY, -ox, -oz, -ix, oz);
		topStrip(pose, buffer, topPalette, light, topY, -ix, iz, ix, oz);
		topStrip(pose, buffer, topPalette, light, topY, -ix, -oz, ix, -iz);

		sideQuad(pose, buffer, edge, light, baseY, topY, -ox, oz, ox, oz);
		sideQuad(pose, buffer, edge, light, baseY, topY, -ox, -oz, ox, -oz);
		sideQuad(pose, buffer, edge, light, baseY, topY, ox, -oz, ox, oz);
		sideQuad(pose, buffer, edge, light, baseY, topY, -ox, -oz, -ox, oz);

		sideQuad(pose, buffer, dark, light, baseY, topY, -ix, iz, ix, iz);
		sideQuad(pose, buffer, dark, light, baseY, topY, -ix, -iz, ix, -iz);
		sideQuad(pose, buffer, dark, light, baseY, topY, ix, -iz, ix, iz);
		sideQuad(pose, buffer, dark, light, baseY, topY, -ix, -iz, -ix, iz);

		poseStack.popPose();
	}

	private static void topStrip(PoseStack.Pose pose, VertexConsumer buffer, int[] palette, int light, float y, float x1, float z1, float x2, float z2) {
		float minX = Math.min(x1, x2);
		float maxX = Math.max(x1, x2);
		float minZ = Math.min(z1, z2);
		float maxZ = Math.max(z1, z2);

		for (float px = minX; px < maxX; px += PATCH_SIZE) {
			float nextX = Math.min(px + PATCH_SIZE, maxX);
			for (float pz = minZ; pz < maxZ; pz += PATCH_SIZE) {
				float nextZ = Math.min(pz + PATCH_SIZE, maxZ);
				int color = palette[patchIndex(px, pz, palette.length)];
				topQuad(pose, buffer, color, light, y, px, pz, nextX, nextZ);
			}
		}
	}

	private static int patchIndex(float x, float z, int paletteSize) {
		int gx = Math.round(x);
		int gz = Math.round(z);
		int hash = gx * 374761393 + gz * 668265263;

		hash = (hash ^ (hash >>> 13)) * 1274126177;
		hash = hash ^ (hash >>> 16);

		return Math.floorMod(hash, paletteSize);
	}

	private static int shade(int argb, float factor) {
		int a = (argb >>> 24) & 0xFF;
		int r = clampColor((int)(((argb >> 16) & 0xFF) * factor));
		int g = clampColor((int)(((argb >> 8) & 0xFF) * factor));
		int b = clampColor((int)((argb & 0xFF) * factor));

		return (a << 24) | (r << 16) | (g << 8) | b;
	}

	private static int clampColor(int value) {
		if (value < 0) {
			return 0;
		}

		return Math.min(value, 255);
	}

	private static void topQuad(PoseStack.Pose pose, VertexConsumer buffer, int color, int light, float y, float x1, float z1, float x2, float z2) {
		vertex(pose, buffer, color, light, x1, y, z1);
		vertex(pose, buffer, color, light, x1, y, z2);
		vertex(pose, buffer, color, light, x2, y, z2);
		vertex(pose, buffer, color, light, x2, y, z1);

		vertex(pose, buffer, color, light, x2, y, z1);
		vertex(pose, buffer, color, light, x2, y, z2);
		vertex(pose, buffer, color, light, x1, y, z2);
		vertex(pose, buffer, color, light, x1, y, z1);
	}

	private static void sideQuad(PoseStack.Pose pose, VertexConsumer buffer, int color, int light, float baseY, float topY, float x1, float z1, float x2, float z2) {
		vertex(pose, buffer, color, light, x1, baseY, z1);
		vertex(pose, buffer, color, light, x2, baseY, z2);
		vertex(pose, buffer, color, light, x2, topY, z2);
		vertex(pose, buffer, color, light, x1, topY, z1);

		vertex(pose, buffer, color, light, x1, topY, z1);
		vertex(pose, buffer, color, light, x2, topY, z2);
		vertex(pose, buffer, color, light, x2, baseY, z2);
		vertex(pose, buffer, color, light, x1, baseY, z1);
	}

	private static void vertex(PoseStack.Pose pose, VertexConsumer buffer, int color, int light, float x, float y, float z) {
		buffer.addVertex(pose, x, y, z).setColor(color).setLight(light);
	}
}
