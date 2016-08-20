package me.johz.infinitic.lib.helpers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.FMLLog;
import me.johz.infinitic.InfiniTiC;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class NameConversionHelper {
	
	private static class NamedItem 
	{
		public String modid;
		public String itemname;
		public int metadata;
		public boolean isOreDict;
		
		NamedItem(String name) 
		{
			if (validate(name)) 
			{
				parse(name);
				InfiniTiC.LOGGER.log(Level.INFO, InfiniTiC.MODID + ": Parsed an item: " + modid + ":" + itemname + ":" 
						+ (metadata == OreDictionary.WILDCARD_VALUE ? "*" : metadata));
			}
		}

		private void parse(String name) 
		{
			String[] pieces = name.split(":");
			
			if (pieces.length == 1) 
			{
				modid = "minecraft";
				itemname = pieces[0];
				metadata = 0;
				return;
			}
			
			if (pieces[0] == "ore") 
			{
				isOreDict = true;
			}
			
			if (pieces.length == 2) 
			{   
				if (NumberUtils.isNumber(pieces[1])) 
				{   //e.g. "stone:2"
					modid = "minecraft";
					itemname = pieces[0];
					metadata = Integer.parseInt(pieces[1]);
					return;
				}
				else if (pieces[1] == "*")
				{   //e.g. "stone:*"
					modid = "minecraft";
					itemname = pieces[0];
					metadata = OreDictionary.WILDCARD_VALUE;
					return;				
				}
				else
				{   //e.g. "mod:block"
					modid = pieces[0];
					itemname = pieces[1];
					metadata = 0;
					return;
				}
			}
			
			//here length must be greater than 2
			//e.g.  minecraft:stone:2
			//or    minecraft:stone:*
			//or    lotr:tile.lotr:oreStorage     !
			//or    lotr:tile.lotr:oreStorage:*   !!
			//or    lotr:tile.lotr:oreStorage:8   !!!
			
			int piecesLeft = pieces.length - 1;
			if (pieces[piecesLeft] == "*") 
			{
				metadata = OreDictionary.WILDCARD_VALUE;
				piecesLeft--;
			} 
			else if (NumberUtils.isNumber(pieces[piecesLeft])) 
			{
				metadata = Integer.parseInt(pieces[piecesLeft]);
				piecesLeft--;
			}
			
			modid = pieces[0];			
			itemname = pieces[1];
			for (int i = 2; i < piecesLeft; i++)
			{
				itemname += ":" + pieces[i];
			}

		}
	}
	
	private static boolean validate(String name) {
		if (name == null || name.length() == 0)
		{
			FMLLog.getLogger().log(Level.ERROR, "Name has not been specified");
			return false;
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
