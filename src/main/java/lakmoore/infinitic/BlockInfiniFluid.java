package lakmoore.infinitic;

import java.lang.reflect.Field;

import lakmoore.infinitic.lib.helpers.MapColorHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tconstruct.smeltery.block.BlockTinkerFluid;

public class BlockInfiniFluid extends BlockTinkerFluid {
	private float r;
	private float g;
	private float b;

	public BlockInfiniFluid(Fluid fluid) {
		super(fluid, Material.LAVA);

		//force the map color to something other than TNT(Lava)
		try {
			Field c = Block.class.getDeclaredField("blockMapColor");
			c.setAccessible(true);
			c.set(this, MapColorHelper.GetMapColor(fluid.getColor()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		//store these fog colours
		r = ((fluid.getColor() >> 16) & 0xFF) / 255.0f;
		g = ((fluid.getColor() >> 8) & 0xFF) / 255.0f;
		b = (fluid.getColor() & 0xFF) / 255.0f;
	}
	
    /**
     * Use this to change the fog color used when the entity is "inside" a material.
     * Vec3d is used here as "r/g/b" 0 - 1 values.
     *
     * @param world         The world.
     * @param pos           The position at the entity viewport.
     * @param state         The state at the entity viewport.
     * @param entity        the entity
     * @param originalColor The current fog color, You are not expected to use this, Return as the default if applicable.
     * @return The new fog color.
     */
	@Override
    @SideOnly (Side.CLIENT)
    public Vec3d getFogColor(World world, BlockPos pos, IBlockState state, Entity entity, Vec3d originalColor, float partialTicks)
    {		
        return new Vec3d(r, g, b);
    }

}
