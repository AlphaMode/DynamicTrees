package com.ferreusveritas.dynamictrees.growthlogic;

import com.ferreusveritas.dynamictrees.systems.GrowSignal;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.util.CoordUtils;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NetherFungusLogic implements IGrowthLogicKit {

	@Override
	public int[] directionManipulation(World world, BlockPos pos, Species species, int radius, GrowSignal signal, int[] probMap) {
		//TODO
		return probMap;
	}

	@Override
	public Direction newDirectionSelected(Species species, Direction newDir, GrowSignal signal) {
		return newDir;
	}

	@Override
	public float getEnergy(World world, BlockPos pos, Species species, float signalEnergy) {
		return signalEnergy;
	}
	
}
