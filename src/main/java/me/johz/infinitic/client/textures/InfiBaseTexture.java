package me.johz.infinitic.client.textures;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import me.johz.infinitic.InfiniTiC;
import me.johz.infinitic.lib.helpers.Colorize;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public abstract class InfiBaseTexture extends TextureAtlasSprite
{
    protected int red = 0;
    protected int green = 0;
    protected int blue = 0;
    
    protected InfiBaseTexture(Color color, String name) {
        super(name);
        red = color.getRed(); 
        green = color.getGreen(); 
        blue = color.getBlue();
    }
    
    ///path to the texture that needs colorizing
    abstract protected String getBasePath();
    
    ///name of the texture that needs colorizing
    abstract protected String getFilename();

    @Override
    public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location)
    {
        return true;
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public boolean load(IResourceManager manager, ResourceLocation location)
    {
        Colorize colrz = new Colorize(red, green, blue);
        int mipmapLevels = (int)Minecraft.getMinecraft().gameSettings.getOptionFloatValue(GameSettings.Options.MIPMAP_LEVELS);
        float anisotropicFiltering = Minecraft.getMinecraft().gameSettings.getOptionFloatValue(GameSettings.Options.ANISOTROPIC_FILTERING);

        BufferedImage[] abufferedimage = new BufferedImage[1 + mipmapLevels];
        AnimationMetadataSection animationmetadatasection = null;

        try {
            
            ResourceLocation resourcelocation = new ResourceLocation(InfiniTiC.MODID, getBasePath() + getFilename() + ".png");   
            
            IResource iresource = manager.getResource(resourcelocation);
            abufferedimage[0] = colrz.filter(ImageIO.read(iresource.getInputStream()));
            TextureMetadataSection texturemetadatasection = (TextureMetadataSection)iresource.getMetadata("texture");

            if (texturemetadatasection != null)
            {
                List list = texturemetadatasection.getListMipmaps();
                int l;

                if (!list.isEmpty())
                {
                    int k = abufferedimage[0].getWidth();
                    l = abufferedimage[0].getHeight();

                    if (MathHelper.roundUpToPowerOfTwo(k) != k || MathHelper.roundUpToPowerOfTwo(l) != l)
                    {
                        throw new RuntimeException("Unable to load extra miplevels, source-texture is not power of two");
                    }
                }

                Iterator iterator3 = list.iterator();

                while (iterator3.hasNext())
                {
                    l = ((Integer)iterator3.next()).intValue();

                    if (l > 0 && l < abufferedimage.length - 1 && abufferedimage[l] == null)
                    {
                        ResourceLocation resourcelocation2 = this.completeResourceLocation(resourcelocation, l);

                        try
                        {
                            abufferedimage[l] = colrz.filter(ImageIO.read(manager.getResource(resourcelocation2).getInputStream()));
                        }
                        catch (IOException ioexception)
                        {
                            InfiniTiC.LOGGER.error("Unable to load miplevel {} from: {}", new Object[] {Integer.valueOf(l), resourcelocation2, ioexception});
                        }
                    }
                }
            }

            animationmetadatasection = (AnimationMetadataSection)iresource.getMetadata("animation");

            loadSprite(abufferedimage, animationmetadatasection, (float)anisotropicFiltering > 1.0F);

            return false;
        }
        catch (IOException e)
        {
            InfiniTiC.LOGGER.error("InfiniTiC : Something went wrong colouring a liquid! : " + e.getStackTrace());
        }

        return true;
    }
    
    @Override
    public void loadSprite(BufferedImage[] image, AnimationMetadataSection animation, boolean useAnisotropicFiltering) 
    {
        super.loadSprite(image, animation, useAnisotropicFiltering);
    }
    
    private ResourceLocation completeResourceLocation(ResourceLocation resourceLocation, int mipLevel)
    {
        return mipLevel == 0 
                ? new ResourceLocation(resourceLocation.getResourceDomain(), 
                        String.format("%s/%s%s", new Object[] {getBasePath(), getFilename(), ".png"}))
                : new ResourceLocation(resourceLocation.getResourceDomain(), 
                        String.format("%s/mipmaps/%s.%d%s", new Object[] {getBasePath(), getFilename(), Integer.valueOf(mipLevel), ".png"}));
    }

}
