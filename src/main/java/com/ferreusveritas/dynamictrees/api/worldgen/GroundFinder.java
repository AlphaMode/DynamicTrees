package com.ferreusveritas.dynamictrees.api.worldgen;

import com.ferreusveritas.dynamictrees.worldgen.OverworldGroundFinder;
import com.ferreusveritas.dynamictrees.worldgen.SubterraneanGroundFinder;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;

/**
 * Implementations will find a suitable area to generate a tree on the ground.
 */
@FunctionalInterface
public interface GroundFinder {

    GroundFinder OVERWORLD = new OverworldGroundFinder();
    GroundFinder SUBTERRANEAN = new SubterraneanGroundFinder();

    /**
     * Finds the {@link BlockPos} of the first ground block for the y-column of the start {@link BlockPos} given.
     *
     * @param world The {@link WorldGenLevel} world object.
     * @param start The {@link BlockPos} to start from.
     * @return The {@link BlockPos} of the first ground block.
     */
    List<BlockPos> findGround(WorldGenLevel world, BlockPos start);

}
