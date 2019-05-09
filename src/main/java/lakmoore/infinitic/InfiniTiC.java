package lakmoore.infinitic;

import java.io.File;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;

import org.apache.logging.log4j.Logger;

import lakmoore.infinitic.lib.data.MaterialData;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

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
    
    @SidedProxy(clientSide = "lakmoore.infinitic.client.ClientProxy", serverSide = "lakmoore.infinitic.CommonProxy")
    public static CommonProxy proxy;
    
    //not currently used as the ones from TConstruct are nicer!
    public static ResourceLocation ICON_StillFluid = new ResourceLocation(InfiniTiC.MODID, "blocks/still_fluid");
    public static ResourceLocation ICON_FlowingFluid = new ResourceLocation(InfiniTiC.MODID, "blocks/flowing_fluid");

    public static MaterialData[] MATERIALS;
        
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

    		proxy.preInit(e);

		//Event Handler... to handle all our events!
		MinecraftForge.EVENT_BUS.register(proxy);
    }
    
	@EventHandler
	public void init(FMLInitializationEvent e) {
		proxy.init(e);
	}
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent e) { 
    		proxy.postInit(e);
    }
    
    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new Command());
    }

}
