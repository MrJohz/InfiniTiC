package me.johz.inifinitic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.johz.inifinitic.lib.data.MaterialData;
import me.johz.inifinitic.lib.data.MaterialJSON;
import me.johz.inifinitic.lib.helpers.JsonConfigHelper;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid=InfiniTiC.MODID, version=InfiniTiC.VERSION, name=InfiniTiC.NAME)
public class InfiniTiC {
	
	/**
	 * THE BIG LONG LIST OF THINGS TO DO:
	 * 
	 * TODO: JSON validation.  Anything would be better than currently.
	 * TODO: Auto-select material ids from range
	 * TODO: Buckets
	 * TODO: Get TiC to understand how much liquid is in the smeltery
	 */
	
	
	public static final String NAME = "Infini-TiC";
    public static final String MODID = "infinitic";
    public static final String VERSION = "1.7.10-0.0.1";
    
    public static Logger LOGGER;
    public static File CONFIGDIR;
    
    public static MaterialData[] MATERIALS;
    
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
    	// Set dat shizzle up, yo
    	LOGGER = e.getModLog();
    	CONFIGDIR = new File(e.getModConfigurationDirectory(), InfiniTiC.MODID);

    	if (!CONFIGDIR.exists()) {
    		CONFIGDIR.mkdirs();
    	}
    	
    	// Read configs
    	MATERIALS = makeMaterials(CONFIGDIR);
    }
    
    @Mod.EventHandler
    public void init(FMLInitializationEvent e) { }
    
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
    	
    	for (MaterialData mat: MATERIALS) {
    		mat.init();
    	}
    	
    }
    
    
    /**************************
     * MRJOHZ'S PRIVATE PARTS *
     **************************/
    
    private MaterialData[] makeMaterials(File dir) {
    	assert dir.isDirectory() : "Asked to make materials from a non-directory, panic!";
    	
    	List<MaterialData> ds = new ArrayList<MaterialData>();
    	
    	for (File file: dir.listFiles()) {
    		if (file.isDirectory()) {
    			for (MaterialData md: makeMaterials(file)) {
    				ds.add(md);
    			}
    		}
    		
    		MaterialJSON m = JsonConfigHelper.dataFromJSON(file);
    		if (m != null) {
    			ds.add(new MaterialData(m, file.getAbsolutePath()));
    		} else {
    			LOGGER.warn("Could not read or parse file '" + file.getName() + "'");
    		}
    	}
    	
		return ds.toArray(new MaterialData[ds.size()]);
    }
}
