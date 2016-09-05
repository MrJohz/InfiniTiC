package me.johz.infinitic.lib.data;

import java.util.HashMap;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import me.johz.infinitic.InfiniTiC;
import me.johz.infinitic.blocks.BlockInfiniFluid;
import me.johz.infinitic.items.InfiniBucket;
import me.johz.infinitic.lib.errors.JSONValidationException;
import me.johz.infinitic.lib.helpers.GenericHelper;
import me.johz.infinitic.lib.helpers.NameConversionHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
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
	public Block ore;
	public Item dustSmall;
	public Item dust;
	public Item nugget;
	public ItemStack ingot;
	public ItemStack solidBlock;
	public Item chunk;
	public Item gem;
	
	FluidStack ingotFluid;
	FluidStack oreFluid;
	FluidStack blockFluid;
	
	public Block fluidBlock;
	public InfiniBucket fluidBucket;
	public int meltingValue;
	
	public MaterialData(MaterialJSON json, String filename) {
		this.json = json;

		try {
			json.validate();
		} catch (JSONValidationException e) {
			InfiniTiC.LOGGER.error("Invalid JSON detected, filename = " + filename);
			InfiniTiC.LOGGER.error("Logged error was: " + e.getReason());
		}
	}
	
	public void init() {
		getSolids();
		makeFluid();
		makeBucket();
		addLocalization();
		addMaterial();
		saveRecipes();
	}
	
	private void addLocalization() {
		for (String[] local: json.localizations) {
			if (local.length != 2) continue;
			
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("fluid.tile." + json.name, local[1]);
			map.put("tile." + json.name + ".name", local[1]);
			map.put("material." + json.name, local[1]);
			LanguageRegistry.instance().injectLanguage(local[0], map);
		}
	}
	
	private void getSolids()
	{
		//Get the ore block
		
		//If no ore block exists, make one
		
		//Get the solid block
		solidBlock = GenericHelper.safeFirst(json.getBlocks());		
		//If no solid block exists, make one

		//Get the ingot
		ingot = GenericHelper.safeFirst(json.getIngots());
		//if no ingot exists, make one

	}

	private void saveRecipes() {
		
		if (json.toolData.temperature > 0) {
			meltingValue = json.toolData.temperature;
		}
		else
		{
			meltingValue = defaultMeltingValue;
		}		
		
		// Melting recipes
		
		Block renderBlock = NameConversionHelper.getBlock(json.renderblock); 
		Block renderOre = NameConversionHelper.getBlock(json.renderore); 
		
		for (ItemStack itm: json.getBlocks()) {
			Smeltery.addMelting(itm, renderBlock, json.renderblockMeta, meltingValue * 5, blockFluid);
		}
		
		for (ItemStack itm: json.getOres()) {
			Smeltery.addMelting(itm, renderOre, json.renderoreMeta, meltingValue, oreFluid);
		}
		
		for (ItemStack itm: json.getIngots()) {
			Smeltery.addMelting(itm, renderBlock, json.renderblockMeta, meltingValue, ingotFluid);
		}
		
		for (ItemStack itm: json.getDusts()) {
			Smeltery.addMelting(itm, renderBlock, json.renderblockMeta, meltingValue, ingotFluid);
		}
		
		// Unsmelting recipes		
		if (solidBlock != null) {
			TConstructRegistry.getBasinCasting().addCastingRecipe(solidBlock, blockFluid, 100);
		}
		if (ingot != null) {
			ItemStack ingotPattern = TConstructRegistry.getItemStack("ingotCast");
			TConstructRegistry.getTableCasting().addCastingRecipe(ingot, ingotFluid, ingotPattern, 50);
		}
	}
	
	private void addMaterial() {
		
		// TODO: Work out how to check if there's a material pre-registered
		
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagCompound item;
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
		if (json.toolData.drawspeed > 0) {
			tag.setInteger("Bow_DrawSpeed", json.toolData.drawspeed); 
		}
		if (json.toolData.projectilespeed > 0) {
			tag.setFloat("Bow_ProjectileSpeed", json.toolData.projectilespeed); 
		}
		if (json.toolData.projectilemass > 0) {
			tag.setFloat("Projectile_Mass", json.toolData.projectilemass); 
		}
		if (json.toolData.projectilefragility > 0) {
			tag.setFloat("Projectile_Fragility", json.toolData.projectilefragility); 
		}
		
		FMLInterModComms.sendMessage("TConstruct", "addMaterial", tag);

		//Add the ingot as a repair material
		ItemStack ingotItm = GenericHelper.safeFirst(json.getIngots());
		if (ingotItm != null)
		{
			tag = new NBTTagCompound();
			tag.setInteger("MaterialId", json.toolData.ID);
			tag.setInteger("Value", 2); //2 halves
			item = new NBTTagCompound();
			ingotItm.writeToNBT(item);
			tag.setTag("Item", item);
			FMLInterModComms.sendMessage("TConstruct", "addMaterialItem", tag);		
		}

		//Add the fluid as the casting material
		tag = new NBTTagCompound();
		(new FluidStack(fluid, 1)).writeToNBT(tag);
		tag.setInteger("MaterialId", json.toolData.ID);
		FMLInterModComms.sendMessage("TConstruct", "addPartCastingMaterial", tag);

		/*
		//add the drop and it's chunk to the part builder
		tag = new NBTTagCompound();
		tag.setInteger("MaterialId", json.toolData.ID); // output material id
		item = new NBTTagCompound();
		(new ItemStack(TinkerSmeltery.smeltery, 1, 2)).writeToNBT(item); // seared brick block
		tag.setTag("Item", item);

		item = new NBTTagCompound();
		(new ItemStack(TinkerTools.materials, 1, 2)).writeToNBT(item); // seared brick item
		tag.setTag("Shard", item);

		// 1 value = 1 shard. So 1 blocks like stone usually have value 2.
		// Seared Brick is the shard, the block consists of 4 bricks, therefore value 4
		tag.setInteger("Value", 4);
		FMLInterModComms.sendMessage("TConstruct", "addPartBuilderMaterial", tag);
		*/
	}
	
	private void makeFluid() {
		
		fluid = new Fluid(json.name + ".molten")
			.setLuminosity(12)
			.setDensity(3000)
			.setViscosity(6000)
			.setTemperature(1300);
		
		boolean isRegistered = !(FluidRegistry.registerFluid(fluid));
		fluidBlock = new BlockInfiniFluid(fluid, Material.lava, json.toolData.getColorType());
		fluidBlock.setBlockName(json.name);
		GameRegistry.registerBlock(fluidBlock, "fluid.molten." + json.name);
		fluid.setUnlocalizedName(fluidBlock.getUnlocalizedName());
		
		if (isRegistered) {
			fluid = FluidRegistry.getFluid(json.name + ".molten");
			Block regFluidBlock = fluid.getBlock();
			if (regFluidBlock != null) {
				fluidBlock = regFluidBlock;
			} else {
				fluid.setBlock(fluidBlock);
			}
		} else {
			fluid.setBlock(fluidBlock);
		}
		
		FluidType.registerFluidType(json.name, fluidBlock, 0, 300, fluid, true);
		
		ingotFluid = new FluidStack(fluid, ingotLiquidValue);
		oreFluid = new FluidStack(fluid, oreLiquidValue);
		blockFluid = new FluidStack(fluid, blockLiquidValue);

	}
	
	private void makeBucket()
	{				
		//Attempt at Bucket Implementation
		fluidBucket = new InfiniBucket(fluidBlock, json.name, json.toolData.getColorType());		
		GameRegistry.registerItem(fluidBucket, "bucket_" + json.name);
		FluidContainerRegistry.registerFluidContainer(fluid, new ItemStack(fluidBucket), new ItemStack(Items.bucket));
	}
}
