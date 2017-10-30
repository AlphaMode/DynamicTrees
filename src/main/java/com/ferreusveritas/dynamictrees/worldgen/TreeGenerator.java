package com.ferreusveritas.dynamictrees.worldgen;

import java.util.ArrayList;
import java.util.Random;

import com.ferreusveritas.dynamictrees.ConfigHandler;
import com.ferreusveritas.dynamictrees.api.worldgen.IBiomeDensityProvider.EnumChance;
import com.ferreusveritas.dynamictrees.api.worldgen.IBiomeTreeSelector.Decision;
import com.ferreusveritas.dynamictrees.trees.DynamicTree;
import com.ferreusveritas.dynamictrees.util.Circle;

import net.minecraft.block.BlockColored;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.common.IWorldGenerator;

public class TreeGenerator implements IWorldGenerator {

	public BiomeTreeHandler biomeTreeHandler; //Provides forest properties for a biome
	public BiomeRadiusCoordinator radiusCoordinator; //Finds radius for coordinates
	public TreeCodeStore codeStore;
	protected ChunkCircleManager circleMan;

	public enum EnumGeneratorResult {
		GENERATED,
		NOTREE,
		UNHANDLEDBIOME,
		FAILSOIL,
		FAILCHANCE,
		NOJOCODE
	}
	
	public TreeGenerator() {
		biomeTreeHandler = new BiomeTreeHandler();
		radiusCoordinator = new BiomeRadiusCoordinator(biomeTreeHandler);
		codeStore = new TreeCodeStore();
		circleMan = new ChunkCircleManager(radiusCoordinator);
	}

	public void onWorldUnload() {
		circleMan = new ChunkCircleManager(radiusCoordinator);//Clears the cached circles
	}

	public ChunkCircleManager getChunkCircleManager() {
		return circleMan;
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		switch (world.provider.getDimension()) {
		case 0: //Overworld
			generateOverworld(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
			break;
		case -1: //Nether

			break;
		case 1: //End

			break;
		}
	}

	EnumFacing getRandomDir(Random rand) {
		return EnumFacing.getFront(2 + rand.nextInt(4));//Return NSWE
	}

	private void generateOverworld(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		ArrayList<Circle> circles = circleMan.getCircles(world, random, chunkX, chunkZ);
		
		for(Circle c: circles) {
			makeTree(world, c);
		}
		
		BlockPos pos = new BlockPos(chunkX * 16, 0, chunkZ * 16);
		if(BiomeDictionary.isBiomeOfType(world.getBiome(pos), Type.SPOOKY)) {
			roofedForestCompensation(world, random, pos);
		}
	}

	/**
	 * Decorate the roofedForest exactly like Minecraft, except leave out the trees and just make giant mushrooms
	 * 
	 * @param world
	 * @param random
	 * @param pos
	 */
	public void roofedForestCompensation(World world, Random random, BlockPos pos) {
		for (int xi = 0; xi < 4; ++xi) {
			for (int zi = 0; zi < 4; ++zi) {
				int posX = xi * 4 + 1 + 8 + random.nextInt(3);
				int posZ = zi * 4 + 1 + 8 + random.nextInt(3);
				BlockPos blockpos = world.getHeight(pos.add(posX, 0, posZ));

				if (random.nextInt(16) == 0) {
					new WorldGenBigMushroom().generate(world, random, blockpos);
				}
			}
		}
	}
	
	public void makeWoolCircle(World world, Circle circle, int h, EnumGeneratorResult resultType) {
		makeWoolCircle(world, circle, h, resultType, 0);
	}
	
	public void makeWoolCircle(World world, Circle circle, int h, EnumGeneratorResult resultType, int flags) {
		//System.out.println("Making circle at: " + circle.x + "," + circle.z + ":" + circle.radius + " H: " + h);
				
		for(int ix = -circle.radius; ix <= circle.radius; ix++) {
			for(int iz = -circle.radius; iz <= circle.radius; iz++) {
				if(circle.isEdge(circle.x + ix, circle.z + iz)) {
					world.setBlockState(new BlockPos(circle.x + ix, h, circle.z + iz), Blocks.WOOL.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.byMetadata((circle.x ^ circle.z) & 0xF)), flags);
				}
			}
		}
		
		if(resultType != EnumGeneratorResult.GENERATED) {

			BlockPos pos = new BlockPos(circle.x, h, circle.z);
			EnumDyeColor color;

			switch(resultType) {
				default: color = EnumDyeColor.WHITE;
				break;
				case FAILCHANCE: color = EnumDyeColor.BLUE; 
				break;
				case FAILSOIL: color = EnumDyeColor.BROWN;
				break;
				case NOJOCODE: color = EnumDyeColor.GRAY;
				break;
				case UNHANDLEDBIOME: color = EnumDyeColor.RED;
				break;
				case NOTREE: color = EnumDyeColor.BLACK;
				break;
			}

			world.setBlockState(pos, Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, color));
			world.setBlockState(pos.up(), Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, color));
		}
	}

	private EnumGeneratorResult makeTree(World world, Circle circle) {
		
		circle.add(8, 8);//Move the circle into the "stage"
		
		BlockPos pos = world.getHeight(new BlockPos(circle.x, 0, circle.z)).down();
		IBlockState blockState = world.getBlockState(pos);
		
		EnumGeneratorResult result = EnumGeneratorResult.GENERATED;
		
		Biome biome = world.getBiome(pos);
		Decision decision = biomeTreeHandler.getTree(world, biome, pos, blockState);
		if(decision.isHandled()) {
			DynamicTree tree = decision.getTree();
			if(tree != null) {
				if(tree.getSeed().isAcceptableSoil(blockState, tree.getSeedStack())) {
					if(biomeTreeHandler.chance(biome, tree, circle.radius, world.rand) == EnumChance.OK) {
						JoCode code = codeStore.getRandomCode(tree, circle.radius, world.rand);
						if(code != null) {
							code.growTree(world, tree, pos, getRandomDir(world.rand), circle.radius + 3);
						} else {
							result = EnumGeneratorResult.NOJOCODE;
						}
					} else {
						result = EnumGeneratorResult.FAILCHANCE;
					}
				} else {
					result = EnumGeneratorResult.FAILSOIL;
				}
			} else {
				result = EnumGeneratorResult.NOTREE;
			}
		} else {
			result = EnumGeneratorResult.UNHANDLEDBIOME;
		}

		//Display wool circles for testing the circle growing algorithm
		if(ConfigHandler.worldGenDebug) {
			makeWoolCircle(world, circle, pos.getY(), result);
		}
		
		circle.add(-8, -8);//Move the circle back to normal coords
		
		return result;
	}

}