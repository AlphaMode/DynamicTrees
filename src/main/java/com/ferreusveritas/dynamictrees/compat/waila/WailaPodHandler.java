package com.ferreusveritas.dynamictrees.compat.waila;

import com.ferreusveritas.dynamictrees.blocks.PodBlock;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class WailaPodHandler implements IComponentProvider {

    @Override
    public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        float growthValue = ((PodBlock) accessor.getBlock()).getAge(accessor.getBlockState()) / 2.0F * 100.0F;
        if (growthValue < 100.0F) {
            tooltip.add(new TranslationTextComponent("tooltip.waila.crop_growth", String.format("%.0f%%", growthValue)));
        } else {
            tooltip.add(new TranslationTextComponent("tooltip.waila.crop_growth", new TranslationTextComponent("tooltip.waila.crop_mature")));
        }
    }
}
