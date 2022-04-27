package com.ferreusveritas.dynamictrees.client;

import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.AABB;

public class ModelUtils {

    public static float[] getUVs(AABB box, Direction face) {
        switch (face) {
            default:
            case DOWN:
                return new float[]{(float) box.minX, 16f - (float) box.minZ, (float) box.maxX, 16f - (float) box.maxZ};
            case UP:
                return new float[]{(float) box.minX, (float) box.minZ, (float) box.maxX, (float) box.maxZ};
            case NORTH:
                return new float[]{16f - (float) box.maxX, (float) box.minY, 16f - (float) box.minX, (float) box.maxY};
            case SOUTH:
                return new float[]{(float) box.minX, (float) box.minY, (float) box.maxX, (float) box.maxY};
            case WEST:
                return new float[]{(float) box.minZ, (float) box.minY, (float) box.maxZ, (float) box.maxY};
            case EAST:
                return new float[]{16f - (float) box.maxZ, (float) box.minY, 16f - (float) box.minZ, (float) box.maxY};
        }
    }

    /**
     * A Hack to determine the UV face angle for a block column on a certain axis
     *
     * @param axis
     * @param face
     * @return
     */
    public static int getFaceAngle(Axis axis, Direction face) {
        if (axis == Axis.Y) { //UP / DOWN
            return 0;
        } else if (axis == Axis.Z) {//NORTH / SOUTH
            switch (face) {
                case UP:
                    return 0;
                case WEST:
                    return 270;
                case DOWN:
                    return 180;
                case NORTH:
                    return 270;
                default:
                    return 90;
            }
        } else { //EAST/WEST
            return (face == Direction.NORTH) ? 270 : 90;
        }
    }

    public static float[] modUV(float[] uvs) {
        uvs[0] = (int) uvs[0] & 0xf;
        uvs[1] = (int) uvs[1] & 0xf;
        uvs[2] = (((int) uvs[2] - 1) & 0xf) + 1;
        uvs[3] = (((int) uvs[3] - 1) & 0xf) + 1;
        return uvs;
    }

    public static Vector3f[] AABBLimits(AABB aabb) {
        return new Vector3f[]{
                new Vector3f((float) aabb.minX, (float) aabb.minY, (float) aabb.minZ),
                new Vector3f((float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ),
        };
    }

    public static BakedQuad makeBakedQuad(BlockElement blockPart, BlockElementFace partFace, TextureAtlasSprite atlasSprite, Direction dir, BlockModelRotation modelRotation, ResourceLocation modelResLoc) {
        return new FaceBakery().bakeQuad(blockPart.from, blockPart.to, partFace, atlasSprite, dir, modelRotation, blockPart.rotation, true, modelResLoc);
    }

    @SuppressWarnings("deprecation")
    public static TextureAtlasSprite getTexture(ResourceLocation resLoc) {
        return getTexture(resLoc, InventoryMenu.BLOCK_ATLAS);
    }

    public static TextureAtlasSprite getTexture(ResourceLocation resLoc, ResourceLocation atlasResLoc) {
        return Minecraft.getInstance().getTextureAtlas(atlasResLoc).apply(resLoc);
    }

}
