package me.johz.infinitic;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import mantle.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraftforge.event.entity.player.FillBucketEvent;

public class InfiniEvents {

    @SubscribeEvent
    public void bucketFill (FillBucketEvent evt)
    {
        if (evt.current.getItem() == Items.bucket && evt.target.typeOfHit == MovingObjectType.BLOCK)
        {
            int hitX = evt.target.blockX;
            int hitY = evt.target.blockY;
            int hitZ = evt.target.blockZ;

            if (evt.entityPlayer != null && !evt.entityPlayer.canPlayerEdit(hitX, hitY, hitZ, evt.target.sideHit, evt.current))
            {
                return;
            }

            Block target = evt.world.getBlock(hitX, hitY, hitZ);
            for (int id = 0; id < InfiniTiC.MATERIALS.length; id++)
            {
                if (target == InfiniTiC.MATERIALS[id].fluidBlock)
                {
                    WorldHelper.setBlockToAir(evt.world, hitX, hitY, hitZ);
                    if (!evt.entityPlayer.capabilities.isCreativeMode)
                    {
                        evt.result = new ItemStack(InfiniTiC.MATERIALS[id].fluidBucket, 1);
                        evt.setResult(Result.ALLOW);
                        return;
                    }
                }
            }
        }
    }

    
}
