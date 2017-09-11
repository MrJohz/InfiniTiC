package me.johz.infinitic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import slimeknights.tconstruct.library.fluid.FluidMolten;
import slimeknights.tconstruct.smeltery.block.BlockMolten;
import me.johz.infinitic.client.Command;
import me.johz.infinitic.client.ResourceManager;
import me.johz.infinitic.client.model.InfiniFluidStateMapper;
import me.johz.infinitic.lib.data.MaterialData;
import me.johz.infinitic.lib.data.MaterialJSON;
import me.johz.infinitic.lib.helpers.GenericHelper;
import me.johz.infinitic.lib.helpers.JsonConfigHelper;

@Mod(
	modid=InfiniTiC.MODID, 
	version=InfiniTiC.VERSION, 
	name=InfiniTiC.NAME, 
	acceptedMinecraftVersions="1.12",
	dependencies="required-after:tconstruct",
	updateJSON=InfiniTiC.UPDATE_URL
	) 
public class InfiniTiC {
		
	public static final String NAME = "Infini-TiC";
    public static final String MODID = "infinitic";
    public static final String VERSION = "${MCVERSION}-${MODVERSION}";
    public static final String UPDATE_URL = "https://www.dropbox.com/s/svrrpxsmyeqku09/infinitic.json?dl=1";
    
    public static Logger LOGGER;
    public static File CONFIGDIR;
    
    //not currently used as the ones from TConstruct are nicer!
    public static ResourceLocation ICON_StillFluid = new ResourceLocation(InfiniTiC.MODID, "blocks/still_fluid");
    public static ResourceLocation ICON_FlowingFluid = new ResourceLocation(InfiniTiC.MODID, "blocks/flowing_fluid");
    
    public static MaterialData[] MATERIALS;
    public static ResourceManager resourceManager;
    
    static {
        FluidRegistry.enableUniversalBucket();
    	}
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
	    	LOGGER = e.getModLog();
	    	CONFIGDIR = new File(e.getModConfigurationDirectory(), InfiniTiC.MODID);
	
	    	if (!CONFIGDIR.exists()) {
	    		CONFIGDIR.mkdirs();
	    	}
	    		    	
	    	// Read configs
	    	MATERIALS = makeMaterials(CONFIGDIR);
	    	
	    	//we need one unused but registered fluid to duplicate later
	    	makeInfiniFluid();

	    	//make the materials and register them with TConstruct and the game
	    	for (MaterialData mat: MATERIALS) {
	    		mat.preInit(e.getSide());
	    	}
	    	
	    	//Event Handler... to handle all our events!
	    	resourceManager = new ResourceManager();
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(resourceManager);
	    	MinecraftForge.EVENT_BUS.register(new InfiniEvents());
    }

	private void makeInfiniFluid() {
		String name = "infinifluid";
		FluidMolten fluid = new FluidMolten(name, 0xFFFF0000); // , InfiniTiC.ICON_StillFluid, InfiniTiC.ICON_FlowingFluid);
		fluid.setUnlocalizedName("unused");
		FluidRegistry.registerFluid(fluid);
		BlockMolten block = new BlockMolten(fluid);
		block.setUnlocalizedName(InfiniTiC.MODID + "." + name);
		ResourceLocation regName = new ResourceLocation(InfiniTiC.MODID, name);
		block.setRegistryName(regName);
		ForgeRegistries.BLOCKS.register(block);
		Item item = new ItemBlock(block).setRegistryName(regName);
		ForgeRegistries.ITEMS.register(item);

		InfiniFluidStateMapper mapper = new InfiniFluidStateMapper(fluid);
		// item-model
		ModelLoader.registerItemVariants(item);
		ModelLoader.setCustomMeshDefinition(item, mapper);
		// block-model
		ModelLoader.setCustomStateMapper(block, mapper);
	}
    
	@EventHandler
	public void init(FMLInitializationEvent e) {
		for (MaterialData mat : MATERIALS) {
			mat.init(e.getSide());
		}
	}
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent e) { 
		for (MaterialData mat : MATERIALS) {
			mat.postInit(e.getSide());
		}
    }
    
    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new Command());
    }
    
	/**************************
	 * MRJOHZ'S PRIVATE PARTS *
	 **************************/

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
				MaterialJSON m = JsonConfigHelper.dataFromJSON(file);
				if (m != null) {
					ds.add(new MaterialData(m, file.getAbsolutePath()));
				} else {
					LOGGER.error("Could not read or parse file '" + file.getName() + "'");
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
			} else {
				LOGGER.error("Could not read or parse file '" + zipDir.getName() + "'");
			}
		}

		try {
			dir.close();
		} catch (IOException e) {
			return new MaterialData[0];
		}

		if (ds.size() == 0) {
			LOGGER.warn("Exploration of zipfile '" + zipDir.getAbsolutePath()
					+ "' yielded no config files.  Is this really right?");
		}
		return ds.toArray(new MaterialData[ds.size()]);
	}
}
