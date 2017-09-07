package me.johz.infinitic;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.google.common.base.Function;

import me.johz.infinitic.lib.data.MaterialData;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelFluid;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public class InfiniEvents {
	
	public static Function<ResourceLocation, TextureAtlasSprite> textureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
		@Override
		public TextureAtlasSprite apply(ResourceLocation location) {
			return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
		}
	};
		  
	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event) {
		ModelResourceLocation location;
		IModel model;
		IBakedModel baked;
	    	for (MaterialData mat : InfiniTiC.MATERIALS) {
	    		if (mat.fluid != null) {
		    		model = new ModelFluid(mat.fluid);
		    		baked = model.bake(model.getDefaultState(), Attributes.DEFAULT_BAKED_FORMAT, textureGetter);
		    		location = new ModelResourceLocation(InfiniTiC.MODID + ":fluid_block", mat.fluid.getName());
		    		event.modelRegistry.putObject(location, baked);	    			
	    		}
	    		else {
	    			InfiniTiC.LOGGER.error("Invalid Material found.  Unable to add the fluid model!" + mat.json.name);
	    		}
	    	}
	}
	
	@SubscribeEvent
	public void materialTooltip(ItemTooltipEvent event) {
		Block block = Block.getBlockFromItem(event.itemStack.getItem());
		if (block == null) {
			return;
		}
		// check if the item belongs to a material
		for (MaterialData material : InfiniTiC.MATERIALS) {
			if (block.equals(material.block)) {
				event.toolTip.add("");
				event.toolTip.add(EnumChatFormatting.GRAY + "Fluid added by Infini-TiC");
			}
		}
	}
    
}
