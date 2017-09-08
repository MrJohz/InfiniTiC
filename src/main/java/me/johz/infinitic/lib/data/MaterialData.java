package me.johz.infinitic.lib.data;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;
import me.johz.infinitic.InfiniTiC;
import me.johz.infinitic.client.model.InfiniFluidStateMapper;
import me.johz.infinitic.lib.errors.JSONValidationException;
import me.johz.infinitic.lib.helpers.GenericHelper;
import net.minecraft.block.Block;
import slimeknights.tconstruct.library.materials.ExtraMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import slimeknights.tconstruct.library.MaterialIntegration;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.client.MaterialRenderInfo;
import slimeknights.tconstruct.library.fluid.FluidMolten;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.BlockMolten;

public class MaterialData {
		
	public MaterialJSON json;

	public Material material;
	public Fluid fluid;
	public Item fluidItem;
	public Block ore;
	public Item dustSmall;
	public Item dust;
	public Item nugget;
	public ItemStack ingot;
	public ItemStack solidBlock;
	public Item chunk;
	public Item gem;
		
	public Block block;
	public int meltingValue;
	
	protected MaterialIntegration integration;
	
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
	
	public void preInit(Side side) {
	    if (isValid)
	    {
	    		getSolids();
	    		addToOreDict();
	    		makeMaterial();
	        makeFluid();
	    		integrateMaterial();

	    		if(side == Side.CLIENT) {
	    			
	    		    Item fluidItem = Item.getItemFromBlock(block);
	    		    InfiniFluidStateMapper mapper = new InfiniFluidStateMapper(fluid);
	    		    // item-model
	    		    ModelLoader.registerItemVariants(fluidItem);
	    		    ModelLoader.setCustomMeshDefinition(fluidItem, mapper);
	    		    // block-model
	    		    ModelLoader.setCustomStateMapper(block, mapper);

			    int color = json.getToolColorInt();
			    float shinyness = 0.25f;
			    float brightness = 0.5f;
			    float hueshift = -0.1f;	    
			    material.setRenderInfo(new MaterialRenderInfo.Metal(color, shinyness, brightness, hueshift));
			}

	    		addMaterialStats();
	        
	    }
	}

	public void init(Side side) {
	    if (isValid)
	    {
		    if(integration != null) integration.integrateRecipes();
		    
			if (json.hasGems()) {
				TinkerRegistry.registerMelting("gem" + GenericHelper.capitalizeFirstLetter(json.name), fluid, Material.VALUE_Ingot);
				TinkerRegistry.registerTableCasting(json.getGems()[0], TinkerSmeltery.castGem, fluid, Material.VALUE_Gem);				
			}

	    }		
	}

	private void addToOreDict() {
		String suffix = GenericHelper.capitalizeFirstLetter(json.name);
		String[] types = {"ingot", "nugget", "gem", "dust", "block", "ore"};
		
		for (String type : types) {
			for (ItemStack item : json.getOfType(type)) {
				OreDictionary.registerOre(type + suffix, item);				
			}			
		}
	}

	private void integrateMaterial() {
		String prefix = "ingot";
		String suffix = GenericHelper.capitalizeFirstLetter(json.name);
		ItemStack item = ingot;

		if(json.hasGems()) {
			prefix = "gem";
			item = json.getGems()[0];
		}
		
		material.addItem(prefix + suffix, 1, Material.VALUE_Ingot);
        material.setRepresentativeItem(item);						
		integration = new MaterialIntegration(prefix + suffix, material, fluid, suffix);
		if(json.canCraftToolForge) {			
			integration.toolforge();
		}
		integration.integrate();
	}

	private void makeMaterial() {
		material = new Material(json.name, json.getTextColorInt());
        material.setCraftable(false).setCastable(true);

	}

	private void getSolids()
	{
		//Get the ore block		
		//TODO: If no ore block exists, make one???
		
		//Get the solid block
		solidBlock = GenericHelper.safeFirst(json.getBlocks());		
		//TODO: If no solid block exists, make one???

		//Get the ingot
		ingot = GenericHelper.safeFirst(json.getIngots());
		//TODO: if no ingot exists, make one???

		//TODO: if no nugget exists, make one???
		//TODO: if no dust exists, make one???
		//TODO: if no gear exists, make one???

	}
	
	private void addMaterialStats() {
		
	    TinkerRegistry.addMaterialStats(material,
                new HeadMaterialStats(json.toolData.durability, json.toolData.miningspeed, json.toolData.attack, json.toolData.harvestLevel),
                new HandleMaterialStats(json.toolData.handleModifier, json.toolData.durability));

	    if(json.toolData.extraDurability != 0) {
		    TinkerRegistry.addMaterialStats(material,
	                new ExtraMaterialStats(json.toolData.extraDurability));
	    }
	    
	    //TODO: Tinker Bows don't appear to be implemented in MC 1.8.9!

	}
	
	private void makeFluid() {
	    	    	    
	    	String name = json.name.toLowerCase();
		fluid = new FluidMolten(name, json.getFluidColorInt()); //, InfiniTiC.ICON_StillFluid, InfiniTiC.ICON_FlowingFluid);
	    fluid.setUnlocalizedName(InfiniTiC.MODID + "." + name);  //For localization
	    FluidRegistry.registerFluid(fluid);

		if (json.fluidData != null) {
			if(json.fluidData.density > 0) fluid.setDensity(json.fluidData.density);
			if(json.fluidData.temperature > 0) fluid.setTemperature(json.fluidData.temperature);
			if(json.fluidData.viscosity > 0) fluid.setViscosity(json.fluidData.viscosity);
			if(json.fluidData.luminosity > 0) fluid.setLuminosity(json.fluidData.luminosity);			
			fluid.setRarity(json.fluidData.getRarity());
		}
		
        name = "molten_" + name;
		ResourceLocation regName = new ResourceLocation(InfiniTiC.MODID, name);
	    block = new BlockMolten(fluid)
		    .setUnlocalizedName(InfiniTiC.MODID + "." + name)  //For localization
		    .setRegistryName(regName);
	    GameRegistry.register(block);
		GameRegistry.register(
			new ItemBlock(block)
				.setRegistryName(regName)
		);

		FluidRegistry.addBucketForFluid(fluid);
		
	}
	
}
