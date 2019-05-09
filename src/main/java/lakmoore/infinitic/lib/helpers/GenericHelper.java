package lakmoore.infinitic.lib.helpers;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.oredict.OreDictionary;

public class GenericHelper {
	
    public static Color decode(String nm) throws NumberFormatException {
    		if(nm.startsWith("#")) {
    	        Integer intval = Integer.parseUnsignedInt(nm.substring(1), 16);
    	        int i = intval.intValue();
    	        return new Color((i >> 16) & 0xFF, (i >> 8) & 0xFF, i & 0xFF, (i >> 24) & 0xFF);
    		}
    		else
    			throw new NumberFormatException("Hex value must begin with # symbol.");
    }
	
	public static String capitalizeFirstLetter(String original){
	    if(original.length() == 0)
	        return original;
	    return original.substring(0, 1).toUpperCase() + original.substring(1).toLowerCase();
	}
	
	public static <T> T safeFirst(T[] items) {
		return items.length > 0
			? items[0]
			: null;
	}
	
	// FileNotFoundException
	// IOException
	public static boolean isZipFile(File f) {
		RandomAccessFile r;
		try {
			r = new RandomAccessFile(f, "r");
			if (r.readInt() == 0x504b0304) {
				r.close();
				return true;
			} else {
				r.close();
				return false;
			}
		} catch (IOException e) {
			return false;
		}
	}
		    
	public static List<String> getOreDictOfItem(ItemStack stack) {
		int[] ids = OreDictionary.getOreIDs(stack);
		List<String> names = new ArrayList<>();

		for (int id : ids) {
			names.add(OreDictionary.getOreName(id));
		}

		return names;
	}

	public static RayTraceResult getPlayerLookat(EntityPlayer player, double range) {
		Vec3d eyes = player.getPositionEyes(1.0F);
		return player.getEntityWorld().rayTraceBlocks(eyes, eyes.add(new Vec3d(player.getLookVec().x * range, player.getLookVec().y * range, player.getLookVec().z * range)));
	}
	
}
