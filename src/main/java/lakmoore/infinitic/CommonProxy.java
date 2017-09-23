package lakmoore.infinitic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import lakmoore.infinitic.lib.data.MaterialData;
import lakmoore.infinitic.lib.data.MaterialJSON;
import lakmoore.infinitic.lib.helpers.GenericHelper;
import lakmoore.infinitic.lib.helpers.JsonConfigHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import slimeknights.tconstruct.library.events.MaterialEvent;
import slimeknights.tconstruct.library.fluid.FluidMolten;
import slimeknights.tconstruct.library.materials.ArrowShaftMaterialStats;
import slimeknights.tconstruct.library.materials.BowMaterialStats;
import slimeknights.tconstruct.library.materials.BowStringMaterialStats;
import slimeknights.tconstruct.library.materials.ExtraMaterialStats;
import slimeknights.tconstruct.library.materials.FletchingMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.IMaterialStats;
import slimeknights.tconstruct.smeltery.block.BlockMolten;

public class CommonProxy {
	protected FluidMolten infinifluid;
	protected BlockMolten infinifluidBlock;
	protected Item infinifluidItem;

	public void preInit(FMLPreInitializationEvent e) {

		// Read configs
	    InfiniTiC.MATERIALS = makeMaterials(InfiniTiC.CONFIGDIR);

		// we need one unused but registered fluid to duplicate later
		makeInfiniFluid();

		// make the materials and register them with TConstruct and the game
		for (MaterialData material : InfiniTiC.MATERIALS) {
		    if (material.isValid())
		    {
			    	material.getSolids();
			    	material.addToOreDict();
			    	material.makeMaterial();
	    			material.makeFluid();	    			
		    		material.integrateMaterial();
		    		material.addMaterialStats();
		    }
		}

	}

	public void init(FMLInitializationEvent e) {
		for (MaterialData mat : InfiniTiC.MATERIALS) {
			mat.init(e.getSide());
		}
	}

	public void postInit(FMLPostInitializationEvent e) {
		for (MaterialData material : InfiniTiC.MATERIALS) {
		    if (material.isValid())
		    {
		    		material.doIntegration();
		    }
		}
	}
	
	protected void makeInfiniFluid() {
		String name = "infinifluid";
		infinifluid = new FluidMolten(name, 0xFFFF0000); // , InfiniTiC.ICON_StillFluid, InfiniTiC.ICON_FlowingFluid);
		infinifluid.setUnlocalizedName("unused");
		FluidRegistry.registerFluid(infinifluid);

		ResourceLocation regName = new ResourceLocation(InfiniTiC.MODID, name);
		infinifluidBlock = new BlockMolten(infinifluid);
		infinifluidBlock.setCreativeTab(null)
			.setUnlocalizedName(InfiniTiC.MODID + "." + name)
			.setRegistryName(regName);
		ForgeRegistries.BLOCKS.register(infinifluidBlock);

		infinifluidItem = new ItemBlock(infinifluidBlock)
			.setRegistryName(regName);
		ForgeRegistries.ITEMS.register(infinifluidItem);
	}
	
	private MaterialData[] makeMaterials(File dir) {
		assert dir.isDirectory() : "Asked to make materials from a non-directory, panic!";

		List<MaterialData> ds = new ArrayList<MaterialData>();

		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				for (MaterialData md : makeMaterials(file)) {
					ds.add(md);
				}
			} else if (GenericHelper.isZipFile(file)) {
				for (MaterialData md : makeMaterialsZipped(file)) {
					ds.add(md);
				}
			} else if (file.getName().endsWith(".json")) {
				InfiniTiC.LOGGER.info("About to parse file '" + file.getName() + "'");
				MaterialJSON m = JsonConfigHelper.dataFromJSON(file);
				if (m != null) {
					ds.add(new MaterialData(m, file.getAbsolutePath()));
					InfiniTiC.LOGGER.info("Finished parsing file '" + file.getName() + "'");
				} else {
					InfiniTiC.LOGGER.error("Could not read or parse file '" + file.getName() + "'");
				}
			}
		}

		return ds.toArray(new MaterialData[ds.size()]);
	}
    
	private MaterialData[] makeMaterialsZipped(File zipDir) {
		List<MaterialData> ds = new ArrayList<MaterialData>();

		ZipFile dir;
		try {
			dir = new ZipFile(zipDir);
		} catch (ZipException e) {
			return new MaterialData[0];
		} catch (IOException e) {
			return new MaterialData[0];
		}

		Enumeration<? extends ZipEntry> entries = dir.entries();
		while (entries.hasMoreElements()) {
			ZipEntry zfile = entries.nextElement();
			MaterialJSON m;
			try {
				InfiniTiC.LOGGER.info("About to parse file '" + zfile.getName() + "'");
				m = JsonConfigHelper.dataFromStream(dir.getInputStream(zfile));
			} catch (IOException e) {
				try {
					dir.close();
				} catch (IOException e2) {
					return new MaterialData[0];
				}
				return new MaterialData[0];
			}
			if (m != null) {
				ds.add(new MaterialData(m, zipDir.getAbsolutePath()));
				InfiniTiC.LOGGER.info("Finished parsing file '" + zfile.getName() + "'");
			} else {
				InfiniTiC.LOGGER.error("Could not read or parse file '" + zipDir.getName() + "'");
			}
		}

		try {
			dir.close();
		} catch (IOException e) {
			return new MaterialData[0];
		}

		if (ds.size() == 0) {
			InfiniTiC.LOGGER.warn("Exploration of zipfile '" + zipDir.getAbsolutePath()
					+ "' yielded no config files.  Is this really right?");
		}
		return ds.toArray(new MaterialData[ds.size()]);
	}
	
	// ========= EVENT HANDLERS ==========

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
