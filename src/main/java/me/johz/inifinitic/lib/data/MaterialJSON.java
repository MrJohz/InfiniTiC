package me.johz.inifinitic.lib.data;

import java.util.HashSet;
import java.util.Set;

import me.johz.inifinitic.lib.errors.JSONValidationException;
import me.johz.inifinitic.lib.helpers.GenericHelper;
import me.johz.inifinitic.lib.helpers.NameConversionHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class MaterialJSON implements IJson {
	
	public String name;
	
	public String[][] localizations;
	
	public NameList whitelist;
	public NameList blacklist;
	
	public ToolDataJSON toolData;
	
	public String renderore;
	public int renderoreMeta = 0;
	public String renderblock;
	public int renderblockMeta = 0;
	
	@Override
	public String toString() {
		return "MaterialJSON[name='" + name + "']";
	}
	
	public ItemStack[] getIngots() {
		return getOfType("ingot");
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
			throw new JSONValidationException("Field 'name' is required and not present");
		} else if (toolData == null) {
			throw new JSONValidationException("Field 'toolData' is required and not present");
		} else if (renderblock == null) {
			throw new JSONValidationException("Field 'renderblock' is required and not present");
		}
	}
}
