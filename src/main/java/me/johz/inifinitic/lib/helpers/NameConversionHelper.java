package me.johz.inifinitic.lib.helpers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class NameConversionHelper {
	
	private static class NamedItem {
		public String modid;
		public String itemname;
		public int metadata;
		public boolean isOreDict;
		
		NamedItem(String name) {
			if (validate(name)) {
				parse(name);
			}
		}

		private void parse(String name) {
			String[] pieces = name.split(":");
			
			if (pieces[0] == "ore") {
				// Assume that no mod would be so pretentious
				// as to have "ore" as it's modid
				isOreDict = true;
			}
			
			if (pieces.length == 3) {
				// Metadata is included
				if (pieces[2] == "*") {
					metadata = OreDictionary.WILDCARD_VALUE;
				} else {
					// This is safe because it's tested in
					// validation, right?
					metadata = Integer.parseInt(pieces[2]);
				}
			}
			
			modid = pieces[0];
			itemname = pieces[1];
		}
	}
	
	private static boolean validate(String name) {
		String[] pieces = name.split(":");
		
		if (pieces.length != 2 && pieces.length != 3) {
			return false;
		}
		
		if (pieces.length == 3) {
			if (!GenericHelper.isInteger(name) && !(name == "*")) {
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean isValid(String name) {
		return validate(name);
	}

	public static ItemStack getItem(String name) {
		if (!isValid(name)) {
			return null;
		}
		
		NamedItem itm = new NamedItem(name);
		
		if (itm.isOreDict) {
			return null;
		}
		
		Item i = (Item) Item.itemRegistry.getObject(itm.modid + ":" + itm.itemname);
		
		return new ItemStack(i, 1, itm.metadata);
	}
	
	public static String getOreName(String name) {
		if (isValid(name)) {
			return null;
		}
		
		NamedItem itm = new NamedItem(name);
		return itm.isOreDict ? itm.itemname : null; 
	}
	
	public static boolean isOreDict(String name) {
		if (isValid(name) || !(new NamedItem(name).isOreDict)) {
			return false;
		}
		
		return true;
	}
	
	public static List<ItemStack> getAllItems(String name) {
		if (!isValid(name)) return null;
		
		NamedItem item = new NamedItem(name);
		if (item.isOreDict) {
			return OreDictionary.getOres(item.itemname);
		} else {
			List<ItemStack> l = new ArrayList<ItemStack>();
			l.add(getItem(name));
			return l;
		}
	}

	public static Block getBlock(String name) {
		if (!isValid(name)) return null;
		
		NamedItem item = new NamedItem(name);
		if (item.isOreDict) {
			return null;
		} else {
			return (Block) Block.blockRegistry.getObject(item.modid + ":" + item.itemname);
		}
	}

	public static boolean isBlock(String name) {
		if (!isValid(name)) return false;
		
		NamedItem item = new NamedItem(name);
		if (item.isOreDict) {
			return false;
		} else {
			return Block.blockRegistry.containsKey(item.modid + ":" + item.itemname);
		}
	}
	
}
