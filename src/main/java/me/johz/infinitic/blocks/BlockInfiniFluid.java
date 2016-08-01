package me.johz.infinitic.blocks;

import java.awt.Color;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.johz.infinitic.InfiniTiC;
import me.johz.infinitic.client.textures.CustomTexture;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class BlockInfiniFluid extends BlockFluidClassic {
	
	protected IIcon stillIcon;
	protected IIcon flowingIcon;
	private Color color;

	public BlockInfiniFluid(Fluid fluid, Material material, Color color) {
		super(fluid, material);
		this.color = color;
		setCreativeTab(CreativeTabs.tabMisc);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		TextureAtlasSprite still = new CustomTexture(color, InfiniTiC.MODID + ":still_fluid");
		TextureAtlasSprite flow = new CustomTexture(color, InfiniTiC.MODID + ":flowing_fluid");
		
		((TextureMap)register).setTextureEntry(InfiniTiC.MODID + ":still_fluid", still);
		((TextureMap)register).setTextureEntry(InfiniTiC.MODID + ":flowing_fluid", flow);
		stillIcon = register.registerIcon(InfiniTiC.MODID + ":still_fluid");
		flowingIcon = register.registerIcon(InfiniTiC.MODID + ":flowing_fluid");
		this.getFluid().setIcons(stillIcon, flowingIcon);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon (int side, int meta) {
		return (side == 0 || side == 1) ? stillIcon : flowingIcon;
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
