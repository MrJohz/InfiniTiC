package me.johz.infinitic.lib.helpers;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

public class Colorize {
	
	BufferedImageOp filter;
	int redColor;
	int greenColor;
	int blueColor;
	
	public Colorize( int red, int green, int blue ) {
		redColor = red;
		greenColor = green;
		blueColor = blue;
	}
	
	public BufferedImage filter(BufferedImage src) {
		BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		int[] rgbArray = new int[src.getWidth() * src.getHeight()];
		rgbArray = src.getRGB(0, 0, src.getWidth(), src.getHeight(), rgbArray, 0, src.getWidth());
		
		for (int i=0, q=rgbArray.length; i<q; i++) {
			rgbArray[i] = colorizePixel(rgbArray[i]);
		}
		
		out.setRGB(0, 0, src.getWidth(), src.getHeight(), rgbArray, 0, src.getWidth());
		return out;
	}
	
	@SuppressWarnings("unused")
	private static int[] getRGB(int rgb) {
		int[] ret = new int[3];
		ret[0] = rgb >> 16 & 0xff;
		ret[1] = rgb >> 8  & 0xff;
		ret[2] = rgb       & 0xff;
		return ret;
	}

	private int colorizePixel(int rgb) {
		int r = rgb >> 16 & 0xff;
		int g = rgb >> 8  & 0xff;
		int b = rgb       & 0xff;
		
		r = (int) ((redColor + (r * 0.3)) / 2);
		g = (int) ((greenColor + (g * 0.59)) / 2);
		b = (int) ((blueColor + (b * 0.11)) / 2);
		
		return (rgb & 0xFF000000) | (r << 16) | (g << 8) | b;
	}
	
}