package me.johz.infinitic.client.textures;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import me.johz.infinitic.InfiniTiC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.util.ResourceLocation;

public class InfiBucketTexture extends InfiBaseTexture {
		
	public InfiBucketTexture(Color color, String name) {
		super(color, name);				
	}
	
	@Override
	public void loadSprite(BufferedImage[] image, AnimationMetadataSection animation, boolean useAnisotropicFiltering) {

		try {
	        BufferedImage fill = image[0];

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
		
		super.loadSprite(image, animation, useAnisotropicFiltering);
		
	}
    
    @Override
    protected String getBasePath() {
        return "textures/items/";
    }

    @Override
    protected String getFilename() {
        return "bucket_fill";
    }

	
	
}
