package me.johz.infinitic.lib.helpers;

import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;

/**
 * @author BloodWorkXGaming, LakMoore
 */
public class ClipboardHelper {
    
    public static final String copyCommandBase = "/ct copy ";    

    /**
     * Sends the Player a Message where he can click on and copy the it
     *
     * @param player:      Player to send the message to
     * @param holeMessage: String that should be shown in chat
     * @param copyMessage: String that is being copied when the player clicks on it
     */
    public static void sendMessageWithCopy(EntityPlayer sender, String wholeMessage, String copyMessage) {
    	
        if(sender instanceof EntityPlayer) {
            copyStringPlayer((EntityPlayer) sender, copyMessage.toString());
            sender.sendMessage(new TextComponentString("Copied [\u00A76" + wholeMessage.toString() + "\u00A7r] to the clipboard"));
        } else {
            sender.sendMessage(new TextComponentString("This command can only be executed as a Player (InGame)"));
        }

        
//        player.sendMessage(SpecialMessagesChat.getCopyMessage(holeMessage, copyMessage));
    }
    
    /**
     * Called by the copy command
     * Copy command is needed to be able to copy something on clicking on a ChatMessage
     *
     * @param sender: sender that copies
     * @param args:   strings to copy
     */
    public static void copyCommandRun(ICommandSender sender, String[] args) {
        
        StringBuilder message = new StringBuilder();
        
        for(int i = 0; i < args.length; i++) {
            message.append(args[i]);
            if(i != args.length - 1)
                message.append(" ");
        }
        
        if(sender.getCommandSenderEntity() instanceof EntityPlayer) {
            copyStringPlayer((EntityPlayer) sender.getCommandSenderEntity(), message.toString());
            sender.sendMessage(new TextComponentString("Copied [\u00A76" + message.toString() + "\u00A7r] to the clipboard"));
        } else {
            sender.sendMessage(new TextComponentString("This command can only be executed as a Player (InGame)"));
        }
    }
    
    
    /**
     * Makes the player copy the sent String
     *
     * @param player: Player which should copy the string
     * @param s:      String to copy
     */
    public static void copyStringPlayer(EntityPlayer player, String s) {
        if(player instanceof EntityPlayerMP) {
            StringSelection stringselection = new StringSelection(s);
        		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringselection, (ClipboardOwner)null);
        }
    }
}