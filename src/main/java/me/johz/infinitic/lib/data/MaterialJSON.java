package me.johz.infinitic.lib.data;

import java.util.HashSet;
import java.util.Set;

import me.johz.infinitic.InfiniTiC;
import me.johz.infinitic.lib.errors.JSONValidationException;
import me.johz.infinitic.lib.helpers.GenericHelper;
import me.johz.infinitic.lib.helpers.NameConversionHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class MaterialJSON implements IJson {
	
	public String name;
	
	public String textColor = "#FFFFFFFF";
	
	public LocalizationJSON[] localizations;
	
	public NameList whitelist;
	public NameList blacklist;
	
	public ToolDataJSON toolData;
	public FluidDataJSON fluidData = new FluidDataJSON();
		
	/**
	 * If true then Tinkers will add the block of this material as
	 * an option when crafting a Tool Forge
	 */
	public Boolean canCraftToolForge = false;

	//Deprecated after MC 1.7.10
	@Deprecated
	public String renderore;
	@Deprecated
	public int renderoreMeta = 0;
	@Deprecated
	public String renderblock;
	@Deprecated
	public int renderblockMeta = 0;

	@Override
	public String toString() {
		return "MaterialJSON[name='" + name + "']";
	}
	
	public ItemStack[] getIngots() {
		return getOfType("ingot");
	}
	
	public ItemStack[] getNuggets() {
		return getOfType("nugget");
	}

	public ItemStack[] getGems() {
		return getOfType("gem");
	}

	public ItemStack[] getOres() {
		return getOfType("ore");
	}
	
	public ItemStack[] getDusts() {
		return getOfType("dust");
	}
	
	public ItemStack[] getBlocks() {
		return getOfType("block");
	}
	
	public boolean hasIngots() {
		return whitelist.ingots == null || whitelist.ingots.length > 0;
	}

	public boolean hasNuggets() {
		return whitelist.nuggets == null || whitelist.nuggets.length > 0;
	}

	public boolean hasDusts() {
		return whitelist.dusts == null || whitelist.dusts.length > 0;
	}

	public boolean hasGems() {
		return whitelist.gems == null || whitelist.gems.length > 0;
	}

	public boolean hasBlocks() {
		return whitelist.blocks == null || whitelist.blocks.length > 0;
	}

	public boolean hasOres() {
		return whitelist.ores == null || whitelist.ores.length > 0;
	}

    public int getTextColorInt() {
		return GenericHelper.decode(textColor).getRGB();
    }
    
    public int getToolColorInt() {
		return GenericHelper.decode(toolData.color).getRGB();
    }
	
    public int getFluidColorInt() {
		if(fluidData == null || fluidData.color == null) {
			return getToolColorInt();    			
		}
		else {
			return GenericHelper.decode(fluidData.color).getRGB();    			
		}
    }

    protected ItemStack[] getOfType(String type) {
		Set<ItemStack> items = new HashSet<ItemStack>();
		
		String itemOreDict = type + GenericHelper.capitalizeFirstLetter(name);
		items.addAll(OreDictionary.getOres(itemOreDict));
		
		if (whitelist != null) {
			for (String itemName: whitelist.getThings(type)) {
				items.addAll(NameConversionHelper.getAllItems(itemName));
			}
		}
		
		if (blacklist != null) {
			for (String itemName: blacklist.getThings(type)) {
				items.removeAll(NameConversionHelper.getAllItems(itemName));
			}
		}
				
		return items.toArray(new ItemStack[items.size()]);
	}

	@Override
	public void validate() throws JSONValidationException {
		if (name == null) {
			throw new JSONValidationException(name + ": Field 'name' is required and not present");
		} else if (toolData == null) {
			throw new JSONValidationException(name + ": Field 'toolData' is required and not present");
		}
		
		if(renderore != null || renderoreMeta > 0 || renderblock != null || renderblockMeta > 0) {
			InfiniTiC.LOGGER.info(name + ": renderore and renderblock are not needed and will be ignored!");
		}
	}
}
