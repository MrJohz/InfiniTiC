package me.johz.infinitic.lib.data;

import java.awt.Color;
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
	
	public Block block;
	public InfiniBucket fluidBucket;
	public int meltingValue;
	
	private boolean isValid = false;
	
	public MaterialData(MaterialJSON json, String filename) {
		this.json = json;

		try {
			json.validate();
	        isValid = true;
		} catch (JSONValidationException e) {
			InfiniTiC.LOGGER.error("Invalid JSON detected, filename = " + filename);
			InfiniTiC.LOGGER.error("Logged error was: " + e.getReason());
		}
	}
	
	public void init() {
	    if (isValid)
	    {
	        getSolids();
	        makeFluid();
	        makeBucket();
	        addLocalization();
	        addMaterial();
	        saveRecipes();	        
	    }
	}
	
	private void addLocalization() {
		for (LocalizationJSON locale: json.localizations) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("tile.fluid.molten." + json.name + ".name", locale.liquid);
            map.put("item.infinitic.bucket." + json.name + ".name", locale.bucket);
			LanguageRegistry.instance().injectLanguage(locale.locale, map);
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
	    	    	    
		fluid = registerFluid(json.name, json.toolData.getColorType());
				
		FluidType.registerFluidType(json.name, block, 0, 300, fluid, true);
		
		ingotFluid = new FluidStack(fluid, ingotLiquidValue);
		oreFluid = new FluidStack(fluid, oreLiquidValue);
		blockFluid = new FluidStack(fluid, blockLiquidValue);

	}
	
    public Fluid registerFluid(String name, Color color) {
        return registerFluid(name, name + ".molten", "fluid.molten." + name, color, 3000, 6000, 1300, Material.lava);
    }

    public Fluid registerFluid(String name, String fluidName, String blockName, Color color, int density, int viscosity, int temperature, Material material) {
        
        String texture = "infiliquid_" + name;
        
        // create the new fluid
        Fluid fluid = new Fluid(fluidName).setDensity(density).setViscosity(viscosity).setTemperature(temperature);

        if(material == Material.lava)
            fluid.setLuminosity(12);

        // register it if it's not already existing
        boolean isFluidPreRegistered = !FluidRegistry.registerFluid(fluid);

        // register our fluid block for the fluid
        block = new BlockInfiniFluid(fluid, material, texture, color);
        block.setBlockName(blockName);
        GameRegistry.registerBlock(block, blockName);

        fluid.setBlock(block);
        ((BlockInfiniFluid)block).setFluid(fluid);

        // if the fluid was already registered we use that one instead
        if (isFluidPreRegistered)
        {
            fluid = FluidRegistry.getFluid(fluidName);

            // don't change the fluid icons of already existing fluids
            if(fluid.getBlock() != null)
                ((BlockInfiniFluid)block).suppressOverwritingFluidIcons();
            // if no block is registered with an existing liquid, we set our own
            else
                fluid.setBlock(block);
        }

        return fluid;
    }
	
	private void makeBucket()
	{				
        //Attempt at Bucket Implementation
        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(fluid, 1000), new ItemStack(Items.bucket)) == null) {
            fluidBucket = new InfiniBucket(block, json.name, json.toolData.getColorType());     
            GameRegistry.registerItem(fluidBucket, "bucket_" + json.name);
            FluidContainerRegistry.registerFluidContainer(fluid, new ItemStack(fluidBucket), new ItemStack(Items.bucket));            
        }
	}
}
