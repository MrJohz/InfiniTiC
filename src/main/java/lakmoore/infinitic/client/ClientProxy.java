package lakmoore.infinitic.client;

import com.google.common.base.Function;

import lakmoore.infinitic.CommonProxy;
import lakmoore.infinitic.InfiniTiC;
import lakmoore.infinitic.lib.data.MaterialData;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelFluid;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.library.client.MaterialRenderInfo;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent e) {
		super.preInit(e);

		for (MaterialData material : InfiniTiC.MATERIALS) {
		    if (material.isValid())
		    {
		    		//setup the block model for the fluids
				if (material.fluid != null) {
				    InfiniFluidStateMapper mapper = new InfiniFluidStateMapper(material.fluid);
				    // item-model
				    ModelLoader.registerItemVariants(material.fluidItem);
				    ModelLoader.setCustomMeshDefinition(material.fluidItem, mapper);
				    // block-model
				    ModelLoader.setCustomStateMapper(material.block, mapper);		
				}
				
				//set the render details for the parts
			    int color = material.json.getToolColorInt();
			    float shinyness = 0.25f;
			    float brightness = 0.5f;
			    float hueshift = -0.1f;	    
			    material.material.setRenderInfo(new MaterialRenderInfo.Metal(color, shinyness, brightness, hueshift));
		    }
		}

		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager())
				.registerReloadListener(new ResourceManager());
	}

	@Override
	public void init(FMLInitializationEvent e) {
		super.init(e);
	}

	@Override
	public void postInit(FMLPostInitializationEvent e) {
		super.postInit(e);
	}

	@Override
	protected void makeInfiniFluid() {
		super.makeInfiniFluid();

		InfiniFluidStateMapper mapper = new InfiniFluidStateMapper(infinifluid);
		// item-model
		ModelLoader.registerItemVariants(infinifluidItem);
		ModelLoader.setCustomMeshDefinition(infinifluidItem, mapper);
		// block-model
		ModelLoader.setCustomStateMapper(infinifluidBlock, mapper);
	}
	
	
	// ========= EVENT HANDLERS ==========
	
	public static Function<ResourceLocation, TextureAtlasSprite> textureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
		@Override
		public TextureAtlasSprite apply(ResourceLocation location) {
			return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
		}
	};
		  
	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event) {
		ModelResourceLocation location;
		IModel model;
		IBakedModel baked;
	    	for (MaterialData mat : InfiniTiC.MATERIALS) {
	    		if (mat.fluid != null) {
		    		model = new ModelFluid(mat.fluid);
		    		baked = model.bake(model.getDefaultState(), Attributes.DEFAULT_BAKED_FORMAT, textureGetter);
		    		location = new ModelResourceLocation(InfiniTiC.MODID + ":fluid_block", mat.fluid.getName());
		    		event.getModelRegistry().putObject(location, baked);	    			
	    		}
	    	}
	}
	
	@SubscribeEvent(priority = EventPriority.LOW)
	public void materialTooltip(ItemTooltipEvent event) {
		Block block = Block.getBlockFromItem(event.getItemStack().getItem());
		if (block == null) {
			return;
		}
		// check if the item belongs to a material
		for (MaterialData material : InfiniTiC.MATERIALS) {
			if (block.equals(material.block)) {
				event.getToolTip().add("");
				event.getToolTip().add(TextFormatting.GRAY + "Fluid added by Infini-TiC");
			}
		}
	}

}
