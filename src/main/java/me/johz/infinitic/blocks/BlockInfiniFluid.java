package me.johz.infinitic.blocks;

import java.awt.Color;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.johz.infinitic.client.textures.InfiFluidTexture;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import tconstruct.smeltery.blocks.TConstructFluid;

public class BlockInfiniFluid extends TConstructFluid {
	
	private Color color;
	private String texture;

	public BlockInfiniFluid(Fluid fluid, Material material, String texture, Color color) {
		super(fluid, material, texture);
		this.texture = texture;
		this.color = color;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		TextureAtlasSprite still = new InfiFluidTexture(color, "tinker:" + texture, false);
		TextureAtlasSprite flow = new InfiFluidTexture(color, "tinker:" + texture, true);
		
		((TextureMap)register).setTextureEntry("tinker:" + texture, still);
		((TextureMap)register).setTextureEntry("tinker:" + texture + "_flow", flow);
		
		super.registerBlockIcons(register);		
	}
	
	@Override
	public boolean canDisplace(IBlockAccess world, int x, int y, int z) {
		if (world.getBlock(x, y, z).getMaterial().isLiquid()) {
			return false;
		}
		
		return super.canDisplace(world, x, y, z);
	}
	
	@Override
	public boolean displaceIfPossible(World world, int x, int y, int z) {
		if (canDisplace(world, x, y, z)) {
			return super.displaceIfPossible(world, x, y, z);
		}
		
		return false;
	}

}
