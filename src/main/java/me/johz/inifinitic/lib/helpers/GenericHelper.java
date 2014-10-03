package me.johz.inifinitic.lib.helpers;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class GenericHelper {
	
	/**
	 * Returns if a string is actually an integer.
	 * http://stackoverflow.com/questions/237159/whats-the-best-way-to-check-to-see-if-a-string-represents-an-integer-in-java
	 * Probably fast.  Probably breaks on edge cases.  *shrugs*
	 */
	public static boolean isInteger(String str) {
		if (str == null) {
			return false;
		}
		int length = str.length();
		if (length == 0) {
			return false;
		}
		int i = 0;
		if (str.charAt(0) == '-') {
			if (length == 1) {
				return false;
			}
			i = 1;
		}
		for (; i < length; i++) {
			char c = str.charAt(i);
			if (c <= '/' || c >= ':') {
				return false;
			}
		}
		return true;
	}
	
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
