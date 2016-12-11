package me.johz.infinitic.items;

import java.awt.Color;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.johz.infinitic.InfiniTiC;
import me.johz.infinitic.client.textures.InfiBucketTexture;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;

public class InfiniBucket extends ItemBucket {
	private String _name;
	private Color _color;
    
	public InfiniBucket(Block contents, String name, Color color) {
        super(contents);
        
        _name = name;
        _color = color;
        
        this.setUnlocalizedName("infinitic.bucket." + _name);
        this.setContainerItem(Items.bucket);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IIconRegister iconRegister)
    {		
		String texture = InfiniTiC.MODID + _name + "_bucket";
        TextureAtlasSprite bucket = new InfiBucketTexture(_color, texture);
		((TextureMap)iconRegister).setTextureEntry(texture, bucket);		
        this.setTextureName(texture);
        
        super.registerIcons(iconRegister);
    }

}
