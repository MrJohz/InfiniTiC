package me.johz.infinitic;

import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.library.events.MaterialEvent;
import slimeknights.tconstruct.library.materials.ArrowShaftMaterialStats;
import slimeknights.tconstruct.library.materials.BowMaterialStats;
import slimeknights.tconstruct.library.materials.BowStringMaterialStats;
import slimeknights.tconstruct.library.materials.ExtraMaterialStats;
import slimeknights.tconstruct.library.materials.FletchingMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.IMaterialStats;

import com.google.common.base.Function;

import me.johz.infinitic.lib.data.MaterialData;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
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
		    		event.getModelRegistry().putObject(location, baked);	    			
	    		}
	    	}
	}
	
	@SubscribeEvent(priority = EventPriority.LOW)
	public void materialTooltip(ItemTooltipEvent event) {
		Block block = Block.getBlockFromItem(event.getItemStack().getItem());
		if (block == null) {
			return;
		}
		// check if the item belongs to a material
		for (MaterialData material : InfiniTiC.MATERIALS) {
			if (block.equals(material.block)) {
				event.getToolTip().add("");
				event.getToolTip().add(TextFormatting.GRAY + "Fluid added by Infini-TiC");
			}
		}
	}
	
	// This seems redundant but has been implemented in-case the user intends
	// to over-ride values set by other mods
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void statRegistered(MaterialEvent.StatRegisterEvent<IMaterialStats> event) {
		for (MaterialData matDat : InfiniTiC.MATERIALS) {
			if (event.material.equals(matDat.material)) {
				if (event.stats instanceof HeadMaterialStats) {
					event.overrideResult(
						new HeadMaterialStats(
								matDat.json.toolData.durability, matDat.json.toolData.miningSpeed,
								matDat.json.toolData.attack, matDat.json.toolData.harvestLevel)
						);
				} else if (event.stats instanceof HandleMaterialStats) {
					event.overrideResult(
						new HandleMaterialStats(
								matDat.json.toolData.handleModifier, matDat.json.toolData.handleDurability)
						);
				} else if (event.stats instanceof ExtraMaterialStats) {
					event.overrideResult(
						new ExtraMaterialStats(matDat.json.toolData.extraDurability)
						);
				} else if (event.stats instanceof BowMaterialStats) {
					event.overrideResult(
						new BowMaterialStats(
								matDat.json.toolData.drawSpeed, matDat.json.toolData.range, 
								matDat.json.toolData.bonusDamage)
						);
				} else if (event.stats instanceof ArrowShaftMaterialStats) {
					event.overrideResult(
						new ArrowShaftMaterialStats(
								matDat.json.toolData.shaftModifier, matDat.json.toolData.bonusAmmo)
						);
				} else if (event.stats instanceof FletchingMaterialStats) {
					event.overrideResult(
						new FletchingMaterialStats(
								matDat.json.toolData.accuracy, matDat.json.toolData.fletchingModifier)
						);
				} else if (event.stats instanceof BowStringMaterialStats) {
					event.overrideResult(
						new BowStringMaterialStats(matDat.json.toolData.stringModifier)
						);
				}
			}
		}
	}
    
}
