package me.johz.infinitic.client.textures;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import me.johz.infinitic.InfiniTiC;
import me.johz.infinitic.lib.helpers.Colorize;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.util.ResourceLocation;

public class CustomTexture extends TextureAtlasSprite {
	
	private int red = 0;
	private int green = 0;
	private int blue = 0;
	private boolean _isBucket = false;
	
	public CustomTexture(Color color, String name) {
		super(name);				
		red = color.getRed(); green = color.getGreen(); blue = color.getBlue();
	}

	public CustomTexture(Color color, String name, boolean isBucket) {
		super(name);
		_isBucket = isBucket;		
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
		
		if (_isBucket)
		{
			BufferedImage fill = image[0];
			if (fill != null) {
				try {
					// load base image
					BufferedImage bucket = ImageIO.read(Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(InfiniTiC.MODID, "textures/items/bucket_empty.png")).getInputStream());

					// create the new image
					BufferedImage combined = new BufferedImage(fill.getWidth(), fill.getHeight(), BufferedImage.TYPE_INT_ARGB);

					// paint both images, preserving the alpha channels
					Graphics g = combined.getGraphics();
					g.drawImage(bucket, 0, 0, null);
					g.drawImage(fill, 0, 0, null);

					// Save as new image
					image[0] = combined;
					
					g.dispose();
				}
				catch (IOException e)
				{
					InfiniTiC.LOGGER.error("InfiniTiC : Something went wrong drawing a bucket! : " + e.getStackTrace());
				}
			}
		}			
		
		super.loadSprite(image, animation, useAnisotropicFiltering);
		
	}
	
	
}
