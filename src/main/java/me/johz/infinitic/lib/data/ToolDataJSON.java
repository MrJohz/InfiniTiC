package me.johz.infinitic.lib.data;

import me.johz.infinitic.InfiniTiC;
import me.johz.infinitic.lib.errors.JSONValidationException;
import me.johz.infinitic.lib.helpers.GenericHelper;

public class ToolDataJSON implements IJson {
	
	// Unique material id - not needed since MC 1.7.10
	int ID = Integer.MIN_VALUE;
	
	int harvestLevel;
	int durability;
	int extraDurability;
	int miningspeed;
	int attack;
	float handleModifier;
	int reinforced;
	float stonebound;
	// You probably won't ever need this
	String style;
	// Three-part hex
	String color;
	
	int temperature;
	
	//Bow things
	int drawspeed;
	float projectilespeed;
	float projectilemass;
	float projectilefragility;
	    
	@Override
	public void validate() throws JSONValidationException {		
		try {
			GenericHelper.decode(color);
		} catch (NumberFormatException e) {
			throw new JSONValidationException("Invalid Tool color string (must be 3-part or 4-part hexadecimal, beginning with '#', e.g. \"#FF2277BB\"");
		}
		
		if (ID > Integer.MIN_VALUE) {
			InfiniTiC.LOGGER.info("Material ID is not needed and will be ignored!");
		}
		
		if(drawspeed > 0 || projectilespeed > 0 || projectilemass > 0 || projectilefragility > 0) {
			InfiniTiC.LOGGER.info("There are no Tinker's Bows in this version.  Bow stats will be ignored!");
		}

	}
	
}
