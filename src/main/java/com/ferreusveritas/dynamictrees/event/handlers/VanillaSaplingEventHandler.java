package com.ferreusveritas.dynamictrees.event.handlers;

import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.blocks.DynamicSaplingBlock;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.util.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.SaplingGrowTreeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class VanillaSaplingEventHandler {

    @SubscribeEvent
    public void onPlayerPlaceBlock(BlockEvent.EntityPlaceEvent event) {
        final BlockState state = event.getPlacedBlock();

        if (!(event.getWorld() instanceof Level) || !TreeRegistry.SAPLING_REPLACERS.containsKey(state)) {
            return;
        }

        final Level world = (Level) event.getWorld();
        final BlockPos pos = event.getPos();
        final Species targetSpecies = TreeRegistry.SAPLING_REPLACERS.get(state);

        // If we should be overriding for this location, then correct the species to the override.
        final Species species = targetSpecies.selfOrLocationOverride(world, pos);

        world.removeBlock(pos, false); // Remove the block so the plantTree function won't automatically fail.

        if (!species.plantSapling(world, pos, targetSpecies != species)) { // If it fails then give a seed back to the player.
            ItemUtils.spawnItemStack(world, pos, species.getSeedStack(1));
        }
    }

    @SubscribeEvent
    public void onSaplingGrowTree(SaplingGrowTreeEvent event) {
        final LevelAccessor iWorld = event.getWorld();
        final BlockPos pos = event.getPos();
        final BlockState blockState = iWorld.getBlockState(pos);

        if (!(iWorld instanceof Level) || !TreeRegistry.SAPLING_REPLACERS.containsKey(blockState)) {
            return;
        }

        final Level world = ((Level) iWorld);
        final Species species = TreeRegistry.SAPLING_REPLACERS.get(blockState)
                .selfOrLocationOverride(world, pos);

        world.removeBlock(pos, false); // Remove the block so the plantTree function won't automatically fail.
        event.setResult(Event.Result.DENY);

        if (species.isValid()) {
            if (DynamicSaplingBlock.canSaplingStay(world, species, pos)) {
                species.transitionToTree(world, pos);
            }
        }
    }

}
