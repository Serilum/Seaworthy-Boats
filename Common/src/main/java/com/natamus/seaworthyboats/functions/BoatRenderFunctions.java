package com.natamus.seaworthyboats.functions;

import com.natamus.seaworthyboats.data.BoatTier;
import com.natamus.seaworthyboats.data.ClientConstants;
import com.natamus.seaworthyboats.renderer.ILayeredRenderState;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BoatRenderFunctions {

	public static final float THICKNESS = 1.5F;

	private static final float PATCH_SIZE = 2.0F;
	private static final float DARK_SHADE = 0.78F;
	private static final float LIGHT_SHADE = 1.16F;
	private static final float EDGE_SHADE = 0.62F;

	public static void appendItemTrim(ItemModelResolver resolver, ModelManager modelManager, ItemStackRenderState output, ItemStack stack, ItemDisplayContext displayContext, Level level, ItemOwner owner, int seed) {
		if (!(stack.getItem() instanceof BoatItem)) {
			return;
		}

		int tier = BoatTier.getTierFromStack(stack);
		if (tier <= 0) {
			return;
		}

		ItemModel trimModel = modelManager.getItemModel(ClientConstants.getTrimModel(stack));
		ClientLevel clientLevel = level instanceof ClientLevel castLevel ? castLevel : null;
		trimModel.update(output, stack, resolver, displayContext, clientLevel, owner, seed);

		ItemStackRenderState.LayerRenderState trimLayer = ((ILayeredRenderState)output).seaworthyboats_getLastLayer();
		if (trimLayer == null) {
			return;
		}

		trimLayer.tintLayers().add(BoatTier.getTierColor(tier));
		output.appendModelIdentityElement(tier);
	}

	public static void renderTrim(PoseStack poseStack, SubmitNodeCollector collector, int tier, int light, boolean isRaft, boolean isChest) {
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
		collector.submitCustomGeometry(poseStack, RenderTypes.entitySolid(ClientConstants.BOAT_TRIM_TEXTURE), (pose, buffer) -> {
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
		});
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
		vertex(pose, buffer, color, light, x1, y, z1, 0.0F, 1.0F, 0.0F);
		vertex(pose, buffer, color, light, x1, y, z2, 0.0F, 1.0F, 0.0F);
		vertex(pose, buffer, color, light, x2, y, z2, 0.0F, 1.0F, 0.0F);
		vertex(pose, buffer, color, light, x2, y, z1, 0.0F, 1.0F, 0.0F);

		vertex(pose, buffer, color, light, x2, y, z1, 0.0F, -1.0F, 0.0F);
		vertex(pose, buffer, color, light, x2, y, z2, 0.0F, -1.0F, 0.0F);
		vertex(pose, buffer, color, light, x1, y, z2, 0.0F, -1.0F, 0.0F);
		vertex(pose, buffer, color, light, x1, y, z1, 0.0F, -1.0F, 0.0F);
	}

	private static void sideQuad(PoseStack.Pose pose, VertexConsumer buffer, int color, int light, float baseY, float topY, float x1, float z1, float x2, float z2) {
		float spanX = x2 - x1;
		float spanZ = z2 - z1;
		float span = Mth.sqrt(spanX * spanX + spanZ * spanZ);
		float nx = spanZ / span;
		float nz = -spanX / span;

		vertex(pose, buffer, color, light, x1, baseY, z1, nx, 0.0F, nz);
		vertex(pose, buffer, color, light, x2, baseY, z2, nx, 0.0F, nz);
		vertex(pose, buffer, color, light, x2, topY, z2, nx, 0.0F, nz);
		vertex(pose, buffer, color, light, x1, topY, z1, nx, 0.0F, nz);

		vertex(pose, buffer, color, light, x1, topY, z1, -nx, 0.0F, -nz);
		vertex(pose, buffer, color, light, x2, topY, z2, -nx, 0.0F, -nz);
		vertex(pose, buffer, color, light, x2, baseY, z2, -nx, 0.0F, -nz);
		vertex(pose, buffer, color, light, x1, baseY, z1, -nx, 0.0F, -nz);
	}

	private static void vertex(PoseStack.Pose pose, VertexConsumer buffer, int color, int light, float x, float y, float z, float nx, float ny, float nz) {
		buffer.addVertex(pose, x, y, z).setColor(color).setUv(0.5F, 0.5F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, nx, ny, nz);
	}
}
