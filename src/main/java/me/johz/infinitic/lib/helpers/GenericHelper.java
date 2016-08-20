package me.johz.infinitic.lib.helpers;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class GenericHelper {
	
	public static String capitalizeFirstLetter(String original){
	    if(original.length() == 0)
	        return original;
	    return original.substring(0, 1).toUpperCase() + original.substring(1);
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
}
