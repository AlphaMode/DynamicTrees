package com.ferreusveritas.dynamictrees.items;

import java.awt.Color;
import java.util.List;

import com.ferreusveritas.dynamictrees.DynamicTrees;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.api.network.MapSignal;
import com.ferreusveritas.dynamictrees.api.treedata.ITreePart;
import com.ferreusveritas.dynamictrees.blocks.BlockBranch;
import com.ferreusveritas.dynamictrees.trees.DynamicTree;
import com.ferreusveritas.dynamictrees.worldgen.JoCode;
import com.google.common.collect.Multimap;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
* Try the following in a command block to demonstrate the extra tag functionality.
* /give @p dynamictrees:staff 1 0 tag:{color:"#88FF00",code:"OUiVpPzkbtJ9uSRPbZP",readonly:1,tree:"birch",display:{Name:"Frog"}}
*/
public class Staff extends Item {

	public Staff() {
		this("staff");
	}
	
	public Staff(String name) {
		setCreativeTab(DynamicTrees.dynamicTreesTab);
		setMaxStackSize(1);
		setUnlocalizedName(name);
		setRegistryName(name);
	}
	
	@Override
	public EnumActionResult onItemUse(ItemStack heldStack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

		IBlockState clickedBlock = world.getBlockState(pos);
		ITreePart treePart = TreeHelper.getSafeTreePart(clickedBlock);
		BlockPos rootPos = pos;
		
		//Check if the tree part is a branch and look for the root node if so
		BlockBranch branch = TreeHelper.getBranch(treePart);
		if(branch != null) {
			MapSignal signal = branch.analyse(world, pos, null, new MapSignal());//Analyze entire tree network to find root node
			if(signal.found) {
				rootPos = new BlockPos(signal.root);
				treePart = TreeHelper.getSafeTreePart(world, signal.root);
			}
		}

		//Get the code from a tree or rooty dirt and set it in the staff
		if(!isReadOnly(heldStack) && treePart.isRootNode()) {
			DynamicTree tree = treePart.getTree(world, rootPos);
			if(tree != null) {
				if(!player.isSneaking()) {
					String code = new JoCode().buildFromTree(world, rootPos, getPlayerDirection(player)).toString();
					setCode(heldStack, code);
					GuiScreen.setClipboardString(code);//Put the code in the system clipboard to annoy everyone.
				}
				setTree(heldStack, tree);
				return EnumActionResult.SUCCESS;
			}
		}

		//Create a tree from right clicking on soil
		DynamicTree tree = getTree(heldStack);
		if(tree != null && tree.isAcceptableSoil(clickedBlock)) {
			new JoCode(getCode(heldStack)).setCareful(true).growTree(world, tree, pos, getPlayerDirection(player), 8);
			heldStack.stackSize--;//If the player is in creative this will have no effect.
			return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.FAIL;
	}

	/**
	* Gets the NBT for the itemStack or creates a new one if it doesn't exist
	* 
	* @param itemStack
	* @return
	*/
	public NBTTagCompound getNBT(ItemStack itemStack) {
		return itemStack.hasTagCompound() ? itemStack.getTagCompound() : new NBTTagCompound();
	}

	public boolean isReadOnly(ItemStack itemStack) {
		return getNBT(itemStack).getBoolean("readonly");
	}

	public Staff setReadOnly(ItemStack itemStack, boolean readonly) {
		NBTTagCompound nbt = getNBT(itemStack);
		nbt.setBoolean("readonly", readonly);
		itemStack.setTagCompound(nbt);
		return this;
	}

	public Staff setTree(ItemStack itemStack, DynamicTree tree) {
		NBTTagCompound nbt = getNBT(itemStack);
		nbt.setString("tree", tree.getFullName());
		itemStack.setTagCompound(nbt);
		return this;
	}

	public Staff setCode(ItemStack itemStack, String code) {
		NBTTagCompound nbt = getNBT(itemStack);
		nbt.setString("code", code);
		itemStack.setTagCompound(nbt);
		return this;
	}

	public DynamicTree getTree(ItemStack itemStack) {
		NBTTagCompound nbt = getNBT(itemStack);

		if(nbt.hasKey("tree")) {
			return TreeRegistry.findTree(nbt.getString("tree"));
		} else {
			DynamicTree tree = TreeRegistry.findTree("oak");
			setTree(itemStack, tree);
			return tree;
		}
	}

	public int getColor(ItemStack itemStack) {
		NBTTagCompound nbt = getNBT(itemStack);

		int color = 0x0000FFFF;
		
		if(nbt.hasKey("color")) {
			try {
				color = Color.decode(nbt.getString("color")).getRGB();
			} catch (NumberFormatException e) {
				nbt.removeTag("color");
			}
		}

		return color;
	}

	public Staff setColor(ItemStack itemStack, String colStr) {
		NBTTagCompound nbt = getNBT(itemStack);
		nbt.setString("color", colStr);
		itemStack.setTagCompound(nbt);
		return this;
	}

	public String getCode(ItemStack itemStack) {
		String code = "P";//Code of a sapling
		NBTTagCompound nbt = getNBT(itemStack);

		if(nbt.hasKey("code")) {
			code = nbt.getString("code");
		} else {
			nbt.setString("code", code);
			itemStack.setTagCompound(nbt);
		}

		return code;
	}

	public EnumFacing getPlayerDirection(EntityPlayer player) {
		return player.getHorizontalFacing();
	}
	
	/**
	* returns the action that specifies what animation to play when the items are being used
	*/
	@Override
	public EnumAction getItemUseAction(ItemStack itemStack) {
		return EnumAction.BLOCK;
	}

	/**
	* Make the player hold the staff like a sword
	*/
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isFull3D() {
		return true;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advancedTooltips) {
		DynamicTree tree = getTree(stack);
		tooltip.add("Tree: " + ((tree != null) ? tree.getFullName() : "none"));
		tooltip.add("Code: §6" + getCode(stack));
	}

	/**
	* Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
	*/
	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot, ItemStack stack) {
		Multimap multimap = super.getAttributeModifiers(equipmentSlot, stack);
		if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 5.0, 0));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED. getAttributeUnlocalizedName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -2.4, 0));
		}
		return multimap;
	}

	///////////////////////////////////////////
	// RENDERING
	///////////////////////////////////////////

}