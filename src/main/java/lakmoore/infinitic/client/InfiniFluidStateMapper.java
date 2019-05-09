package lakmoore.infinitic.client;

import lakmoore.infinitic.InfiniTiC;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

public class InfiniFluidStateMapper extends StateMapperBase implements ItemMeshDefinition {

    public final Fluid fluid;
    public final ModelResourceLocation location;
    
    public InfiniFluidStateMapper(Fluid fluid) {
        this.fluid = fluid;

        this.location = new ModelResourceLocation(InfiniTiC.MODID + ":fluid_block", fluid.getName());
      }
    
	@Override
	public ModelResourceLocation getModelLocation(ItemStack stack) {
	      return location;
	}

	@Override
	protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
	      return location;
	}

}
