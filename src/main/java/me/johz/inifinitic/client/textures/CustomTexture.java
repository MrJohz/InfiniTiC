package me.johz.inifinitic.client.textures;

import java.awt.Color;
import java.awt.image.BufferedImage;

import me.johz.inifinitic.lib.helpers.Colorize;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.data.AnimationMetadataSection;

public class CustomTexture extends TextureAtlasSprite {
	
	private int red = 0;
	private int green = 0;
	private int blue = 0;
	
	public CustomTexture(Color color, String name) {
		super(name);
		
		red = color.getRed(); green = color.getGreen(); blue = color.getBlue();
	}
	
	@Override
	public void loadSprite(BufferedImage[] image, AnimationMetadataSection animation, boolean useAnisotropicFiltering) {
		Colorize colrz = new Colorize(red, green, blue);
		
		for (int i=0; i < image.length; i++) {
			if (image[i] != null) {
				image[i] = colrz.filter(image[i]);
			}
		}
		
		super.loadSprite(image, animation, useAnisotropicFiltering);
		
	}
	
}
