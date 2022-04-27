package com.ferreusveritas.dynamictrees.trees.families;

import com.ferreusveritas.dynamictrees.api.registry.TypedRegistry;
import com.ferreusveritas.dynamictrees.data.DTBlockTags;
import com.ferreusveritas.dynamictrees.data.DTItemTags;
import com.ferreusveritas.dynamictrees.trees.Family;
import com.ferreusveritas.dynamictrees.util.BlockBounds;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

/**
 * @author Harley O'Connor
 */
public class NetherFungusFamily extends Family {

    public static final TypedRegistry.EntryType<Family> TYPE = TypedRegistry.newType(NetherFungusFamily::new);

    public NetherFungusFamily(ResourceLocation name) {
        super(name);
    }

    @Override
    public int getPrimaryThickness() {
        return 3;
    }

    @Override
    public int getSecondaryThickness() {
        return 4;
    }

    @Override
    public Material getDefaultBranchMaterial() {
        return Material.NETHER_WOOD;
    }

    @Override
    public SoundType getDefaultBranchSoundType() {
        return SoundType.HARD_CROP;
    }

    @Override
    public boolean isFireProof() {
        return true;
    }

    public BlockBounds expandLeavesBlockBounds(BlockBounds bounds) {
        return bounds.expand(1).expand(Direction.DOWN, 3);
    }

    @Override
    public List<Tag.Named<Block>> defaultBranchTags() {
        return Collections.singletonList(DTBlockTags.FUNGUS_BRANCHES);
    }

    @Override
    public List<Tag.Named<Item>> defaultBranchItemTags() {
        return Collections.singletonList(DTItemTags.FUNGUS_BRANCHES);
    }

    @Override
    public List<Tag.Named<Block>> defaultStrippedBranchTags() {
        return Collections.singletonList(DTBlockTags.STRIPPED_FUNGUS_BRANCHES);
    }

}
