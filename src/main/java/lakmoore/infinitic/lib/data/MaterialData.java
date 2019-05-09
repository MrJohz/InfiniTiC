package lakmoore.infinitic.lib.data;

import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;

import lakmoore.infinitic.BlockInfiniFluid;
import lakmoore.infinitic.InfiniTiC;
import lakmoore.infinitic.lib.errors.JSONValidationException;
import lakmoore.infinitic.lib.helpers.GenericHelper;
import net.minecraft.block.Block;
import slimeknights.tconstruct.library.materials.ArrowShaftMaterialStats;
import slimeknights.tconstruct.library.materials.BowMaterialStats;
import slimeknights.tconstruct.library.materials.BowStringMaterialStats;
import slimeknights.tconstruct.library.materials.ExtraMaterialStats;
import slimeknights.tconstruct.library.materials.FletchingMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.materials.ProjectileMaterialStats;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.traits.ITrait;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import slimeknights.tconstruct.library.MaterialIntegration;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.fluid.FluidMolten;

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
	
	public boolean isValid() {
		return isValid;
	}

	public void addToOreDict() {
		String suffix = GenericHelper.capitalizeFirstLetter(json.name);
		String[] types = {"ingot", "nugget", "gem", "dust", "block", "ore"};
		
		for (String type : types) {
			for (ItemStack item : json.getOfType(type)) {
				OreDictionary.registerOre(type + suffix, item);				
			}			
		}
	}

	public void integrateMaterial() {
		
		//if there is no fluid then we can only use the part builder
		if (fluid == null) {
	        material.setCraftable(true).setCastable(false);			
		}
		else {
			//else we can only use the smeltry
	        material.setCraftable(false).setCastable(true);
		}

		String prefix = "ingot";
		String suffix = GenericHelper.capitalizeFirstLetter(json.name);
		ItemStack item = ingot;

		if(json.hasGems()) {
			prefix = "gem";
			item = json.getGems()[0];
		}
		
		if (item != null) {
			material.addItem(prefix + suffix, 1, Material.VALUE_Ingot);
	        material.setRepresentativeItem(item);									
		}
		integration = new MaterialIntegration(prefix + suffix, material, fluid, suffix);
		if(json.canCraftToolForge) {			
			integration.toolforge();
		}
		integration.preInit();
	}

	public void makeMaterial() {
		
		material = TinkerRegistry.getMaterial(json.name);
		if (material != Material.UNKNOWN) {
			//There is already a material registered with this name
			InfiniTiC.LOGGER.error("A material named " + json.name + " has already been registered by the " + TinkerRegistry.getTrace(material).getName() + " mod.  Values and stats will be over-ridden.  If this does not suit your purpose re-name your Infini-TiC material.  For example, it is not possible to remove a stat or trait registered by another mod!");
		}
		else {
			material = new Material(json.name, json.getTextColorInt());
		}		

	}

	public void getSolids()
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
	
	public void addTraitType(String[] traitsList, String dependency) {
		ArrayList<String> traitsSoFar = new ArrayList<String>();

		for (String traitId : traitsList) {
			IModifier modifier = TinkerRegistry.getModifier(traitId);
			if (modifier != null && modifier instanceof ITrait) {
				if (traitsSoFar.contains(traitId)) {
					InfiniTiC.LOGGER.error("Unable to add the same trait ( " + traitId + " ) twice to material " + dependency + " " + json.name);
				}
				else {
					if (dependency == "")
						material.addTrait((ITrait)modifier);
					else
						material.addTrait((ITrait)modifier, dependency);
					traitsSoFar.add(traitId);					
				}
			}
		}
	}

	public void addMaterialTraits() {			
		addTraitType(json.toolData.headTraits, MaterialTypes.HEAD);
		addTraitType(json.toolData.handleTraits, MaterialTypes.HANDLE);
		addTraitType(json.toolData.extraTraits, MaterialTypes.EXTRA);
		addTraitType(json.toolData.bowTraits, MaterialTypes.BOW);
		addTraitType(json.toolData.stringTraits, MaterialTypes.BOWSTRING);
		addTraitType(json.toolData.projectileTraits, MaterialTypes.PROJECTILE);
		addTraitType(json.toolData.shaftTraits, MaterialTypes.SHAFT);
		addTraitType(json.toolData.fletchingTraits, MaterialTypes.FLETCHING);
		addTraitType(json.toolData.traits, "");
	}
	
	public void addMaterialStats() {
		
		//can we make heads out of this material?
	    if(json.toolData.miningSpeed != 0 && json.toolData.attack != 0 && json.toolData.durability != 0 && json.toolData.harvestLevel != 0) {
		    TinkerRegistry.addMaterialStats(material,
	                new HeadMaterialStats(json.toolData.durability, json.toolData.miningSpeed, json.toolData.attack, json.toolData.harvestLevel)
	                );
	    }

		//can we make handles out of this material?
	    if(json.toolData.handleModifier != 0 && json.toolData.handleDurability != 0) {
		    TinkerRegistry.addMaterialStats(material,
	                new HandleMaterialStats(json.toolData.handleModifier, json.toolData.handleDurability)
	                );
	    }

	    //can we make bindings (etc.) out of this material?
	    if(json.toolData.extraDurability != 0) {
		    TinkerRegistry.addMaterialStats(material,
	                new ExtraMaterialStats(json.toolData.extraDurability)
	                );
	    }
	    
	    //can we make bow parts out of this material?
	    if (json.toolData.drawSpeed != 0 || json.toolData.range != 0) {
		    TinkerRegistry.addMaterialStats(material,
			    	new BowMaterialStats(json.toolData.drawSpeed, json.toolData.range, json.toolData.bonusDamage)
			    	);
	    }

	    //can we make arrow shafts out of this material?
	    if (json.toolData.shaftModifier != 0) {
		    TinkerRegistry.addMaterialStats(material,
			    	new ArrowShaftMaterialStats(json.toolData.shaftModifier, json.toolData.bonusAmmo)
			    	);
	    }

	    //can we make arrow fletchings out of this material?
	    if (json.toolData.accuracy != 0 && json.toolData.fletchingModifier != 0) {
		    TinkerRegistry.addMaterialStats(material,
			    	new FletchingMaterialStats(json.toolData.accuracy, json.toolData.fletchingModifier)
			    	);
	    }

	    //can we make bow strings out of this material?
	    if (json.toolData.stringModifier != 0) {
		    TinkerRegistry.addMaterialStats(material,
			    	new BowStringMaterialStats(json.toolData.stringModifier)
			    	);
	    }
	    
	    //can we make projectiles (e.g. Shurikens) out of this material?
	    //Tinkers auto-adds this stat to any material used to make tool heads
	    //and trying to add it a second time throws an exception, so check before adding.
	    if (json.toolData.projectiles && json.toolData.attack != 0 && !material.hasStats(MaterialTypes.PROJECTILE)) {
		    TinkerRegistry.addMaterialStats(material,
			    	new ProjectileMaterialStats()
			    	);
	    }
	    
	}
	
	public void makeFluid() {
		if(!json.getMake("fluid")) {
			return;	    			
		}

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
		block = new BlockInfiniFluid(fluid)
		    .setUnlocalizedName(InfiniTiC.MODID + "." + name)  //For localization
		    .setRegistryName(regName)
		    .setCreativeTab(null);
	    ForgeRegistries.BLOCKS.register(block);
	    fluidItem = new ItemBlock(block)
				.setRegistryName(regName);
	    ForgeRegistries.ITEMS.register(fluidItem);

		FluidRegistry.addBucketForFluid(fluid);
		
	}

	public void doIntegration() {
	    if(integration != null) integration.integrate();	    	
	}
	
}
