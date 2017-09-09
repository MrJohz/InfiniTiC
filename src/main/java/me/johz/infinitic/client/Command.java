package me.johz.infinitic.client;

import java.util.ArrayList;
import java.util.List;

import me.johz.infinitic.lib.helpers.ClipboardHelper;
import me.johz.infinitic.lib.helpers.GenericHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.traits.ITrait;
import slimeknights.tconstruct.tools.TinkerTraits;

/**
 * Bulk of this class was taken from CraftTweaker where the Authors were listed as below
 * @author BloodWorkXGaming, Stan, Jared
 */
public class Command implements ICommand {

	@Override
	public int compareTo(ICommand o) {
		return 0;
	}

	@Override
	public String getName() {
		return "infinitic";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/infinitic hand | traits";
	}

	@Override
	public List<String> getAliases() {
		ArrayList<String> aliases = new ArrayList<String>();
		aliases.add("infini");
		aliases.add("it");
		return aliases;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		
		if (args.length < 1 && !("hand".equalsIgnoreCase(args[0]) || "traits".equalsIgnoreCase(args[0]))) {
			sender.sendMessage(new TextComponentString("Need to specify 'hand'."));
			sender.sendMessage(new TextComponentString(this.getUsage(sender)));
			return;
		}
		
		if ("hand".equalsIgnoreCase(args[0])) {
			if(sender.getCommandSenderEntity() instanceof EntityPlayer) {
	            // Gets player and held item
	            EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
	            ItemStack heldItem = player.getHeldItemMainhand();
	            
	            // Tries to get name of held item first
	            if(heldItem != null) {
	                List<String> oreDictNames = GenericHelper.getOreDictOfItem(heldItem);
	                
	                int meta = heldItem.getMetadata();
	                String itemName = heldItem.getItem().getRegistryName() + (meta == 0 ? "" : ":" + meta);
	                
	                String withNBT = "";
	                
	                //NBT might be useful one day, but not right now
	                if(heldItem.serializeNBT().hasKey("tag")) {
	                    String nbt = heldItem.serializeNBT().getTag("tag").toString();
	                    if(nbt.length() > 0)
	                        withNBT = ".withTag(" + nbt + ")";
	                }

	                sender.sendMessage(new TextComponentString("Item \u00A72" + itemName + "\u00A7a" + withNBT));
	                String toCopy = "\"" + itemName + "\"";

	                // adds the oredict names if it has some
	                if(oreDictNames.size() > 0) {
	                    sender.sendMessage(new TextComponentString("\u00A73OreDict Entries:"));
	                    for(String oreName : oreDictNames) {
	                        sender.sendMessage(new TextComponentString(" \u00A7e- \u00A7b" + oreName));
	                        toCopy += ", \"ore:" + oreName + "\"";
	                    }
	                } else {
	                    sender.sendMessage(new TextComponentString("\u00A73No OreDict Entries"));
	                }

	                ClipboardHelper.copyStringPlayer(player, toCopy);
	                sender.sendMessage(new TextComponentString("Copied [\u00A76" + toCopy + "\u00A7r] to the clipboard"));
	                
	            } else {
	                // if hand is empty, tries to get oreDict of block
		            	RayTraceResult rayTraceResult = GenericHelper.getPlayerLookat(player, 100);
	                
	            		if(rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
	                    BlockPos blockPos = rayTraceResult.getBlockPos();
	                    IBlockState block = sender.getEntityWorld().getBlockState(blockPos);                    
	                    
	                    int meta = block.getBlock().getMetaFromState(block);
	                    String blockName = block.getBlock().getRegistryName() + (meta == 0 ? "" : ":" + meta);
	                    String toCopy = "\"" + blockName + "\"";
	                    
	                    sender.sendMessage(new TextComponentString("Block \u00A72" + blockName + " \u00A7rat \u00A79[" + blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ() + "]\u00A7r"));
	                    
	                    // adds the oreDict names if it has some
	                    try {
	                        
	                        List<String> oreDictNames = GenericHelper.getOreDictOfItem(new ItemStack(block.getBlock(), 1, block.getBlock().getMetaFromState(block)));
	                        if(oreDictNames.size() > 0) {
	                            sender.sendMessage(new TextComponentString("\u00A73OreDict Entries:"));
	                            
								for (String oreName : oreDictNames) {
									toCopy += ", \"ore:" + oreName + "\"";
									sender.sendMessage(new TextComponentString(" \u00A7e- \u00A7b" + oreName));
								}
	                        } else {
	                            sender.sendMessage(new TextComponentString("\u00A73No OreDict Entries"));
	                        }
	                        // catches if it couldn't create a valid ItemStack for the Block
	                    } catch(IllegalArgumentException e) {
	                        sender.sendMessage(new TextComponentString("\u00A73No OreDict Entries"));
	                    }
	                    
	                    ClipboardHelper.copyStringPlayer(player, toCopy);
	                    sender.sendMessage(new TextComponentString("Copied [\u00A76" + toCopy + "\u00A7r] to the clipboard"));
	                    
	                } else {
	                    sender.sendMessage(new TextComponentString("\u00A74Please hold an Item in your hand or look at a Block."));
	                }
	            }
	        } else {
	            sender.sendMessage(new TextComponentString("This command can only be casted by a player inGame"));
	        }
		}
		else if ("traits".equalsIgnoreCase(args[0])) {
			String traitString = "";	
			for (IModifier modifier : TinkerRegistry.getAllModifiers()) {
				if (modifier instanceof Modifier && !(modifier instanceof ModifierTrait)) {
					if(traitString.length() > 0) traitString += ", ";
					traitString += "\"" + modifier.getIdentifier() + "\"";					
				}
			}
			sender.sendMessage(new TextComponentString("The following Traits are registered and have been copied to the clipboard: \u00A7e" + traitString));
            ClipboardHelper.copyStringPlayer((EntityPlayer)sender, traitString);

		}
		
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        if(sender instanceof EntityPlayer) {
            return true;
        }
        return false;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return false;
	}

}

