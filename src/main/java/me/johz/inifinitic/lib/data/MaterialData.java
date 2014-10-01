package me.johz.inifinitic.lib.data;

import java.util.HashMap;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import me.johz.inifinitic.InfiniTiC;
import me.johz.inifinitic.blocks.BlockInfiniFluid;
import me.johz.inifinitic.lib.errors.JSONValidationException;
import me.johz.inifinitic.lib.helpers.GenericHelper;
import me.johz.inifinitic.lib.helpers.NameConversionHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.FluidType;
import tconstruct.library.crafting.Smeltery;

public class MaterialData {
	
	private static final int ingotLiquidValue = 144;
	private static final int oreLiquidValue = ingotLiquidValue * 2;
	private static final int blockLiquidValue = ingotLiquidValue * 9;
	@SuppressWarnings("unused")
	private static final int chunkLiquidValue = ingotLiquidValue / 2;
	@SuppressWarnings("unused")
	private static final int nuggetLiquidValue = ingotLiquidValue / 9;
	private static final int defaultMeltingValue = 500;
	
	public MaterialJSON json;
	public Fluid fluid;
	public Block fluidBlock;
	
	public MaterialData(MaterialJSON json, String filename) {
		this.json = json;
		
		try {
			json.validate();
		} catch (JSONValidationException e) {
			InfiniTiC.LOGGER.error("InfiniTiC Is About To Break!");
			InfiniTiC.LOGGER.error("Invalid JSON detected, filename = " + filename);
			InfiniTiC.LOGGER.error("Logged error was: " + e.getReason());
		}
	}
	
	public void init() {
		setFluid(json.name);
		setLocalize(json.name);
		setMaterial();
		setRecipes();
	}
	
	private void setLocalize(String name) {
		for (String[] local: json.localizations) {
			if (local.length != 2) continue;
			
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("fluid.tile." + name, local[1]);
			map.put("tile." + name + ".name", local[1]);
			map.put("material." + name, local[1]);
			LanguageRegistry.instance().injectLanguage(local[0], map);
		}
	}

	private void setRecipes() {
		
		FluidStack ingotFluid = new FluidStack(fluid, ingotLiquidValue);
		FluidStack oreFluid = new FluidStack(fluid, oreLiquidValue);
		FluidStack blockFluid = new FluidStack(fluid, blockLiquidValue);
		
		
		// Melting recipes
		
		Block renderBlock = NameConversionHelper.getBlock(json.renderblock); 
		
		for (ItemStack itm: json.getBlocks()) {
			Smeltery.addMelting(itm, renderBlock, json.renderblockMeta, defaultMeltingValue, blockFluid);
		}
		
		for (ItemStack itm: json.getOres()) {
			Smeltery.addMelting(itm, renderBlock, json.renderblockMeta, defaultMeltingValue, oreFluid);
		}
		
		for (ItemStack itm: json.getIngots()) {
			Smeltery.addMelting(itm, renderBlock, json.renderblockMeta, defaultMeltingValue, ingotFluid);
		}
		
		for (ItemStack itm: json.getDusts()) {
			Smeltery.addMelting(itm, renderBlock, json.renderblockMeta, defaultMeltingValue, ingotFluid);
		}
		
		
		// Unsmelting recipes
		
		ItemStack blockItm = GenericHelper.safeFirst(json.getBlocks());
		if (blockItm != null) {
			TConstructRegistry.getBasinCasting().addCastingRecipe(blockItm, blockFluid, 100);
		}
		
		ItemStack ingotItm = GenericHelper.safeFirst(json.getIngots());
		ItemStack ingotPattern = TConstructRegistry.getItemStack("ingotCast");
		if (ingotItm != null) {
			TConstructRegistry.getTableCasting().addCastingRecipe(ingotItm, ingotFluid, ingotPattern, 50);
		}
	}
	
	private void setMaterial() {
		
		// TODO: Work out how to check if there's a material pre-registered
		
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("Id", json.toolData.ID);
		tag.setString("Name", json.name);
		tag.setInteger("Durability", json.toolData.durability);
		tag.setInteger("HarvestLevel", json.toolData.harvestLevel);
		tag.setInteger("MiningSpeed", json.toolData.miningspeed);
		tag.setFloat("HandleModifier", json.toolData.handleModifier);
		tag.setInteger("Color", json.toolData.getColor(true));
		
		if (json.toolData.attack != 0) {
			tag.setInteger("Attack", json.toolData.attack);
		}
		if (json.toolData.reinforced != 0) {
			tag.setInteger("Reinforced", json.toolData.reinforced);
		}
		if (json.toolData.style != null) {
			tag.setString("Style", json.toolData.style);
		}
		if (json.toolData.stonebound > 0) {
			tag.setFloat("Stonebound", json.toolData.stonebound);
		} else if (json.toolData.stonebound < 0) {
			tag.setFloat("Jagged", json.toolData.stonebound);
		}
		
		FMLInterModComms.sendMessage("TConstruct", "addMaterial", tag);
		
		tag = new NBTTagCompound();
		(new FluidStack(fluid, 1)).writeToNBT(tag);
		tag.setInteger("MaterialId", json.toolData.ID);
		FMLInterModComms.sendMessage("TConstruct", "addPartCastingMaterial", tag);
	}
	
	private void setFluid(String name) {
		
		fluid = new Fluid(name + ".molten")
			.setLuminosity(12)
			.setDensity(3000)
			.setViscosity(6000)
			.setTemperature(1300);
		
		boolean isRegistered = !(FluidRegistry.registerFluid(fluid));
		fluidBlock = new BlockInfiniFluid(fluid, Material.lava, json.toolData.getColorType());
		fluidBlock.setBlockName(name);
		GameRegistry.registerBlock(fluidBlock, "fluid.molten." + name);
		fluid.setUnlocalizedName(fluidBlock.getUnlocalizedName());
		
		if (isRegistered) {
			fluid = FluidRegistry.getFluid(name + ".molten");
			Block regFluidBlock = fluid.getBlock();
			if (regFluidBlock != null) {
				fluidBlock = regFluidBlock;
			} else {
				fluid.setBlock(fluidBlock);
			}
		} else {
			fluid.setBlock(fluidBlock);
		}
		
		FluidType.registerFluidType(name, fluidBlock, 0, 300, fluid, true);
	}
}
