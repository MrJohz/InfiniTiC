package me.johz.infinitic.lib.data;

import me.johz.infinitic.InfiniTiC;
import me.johz.infinitic.lib.errors.JSONValidationException;
import me.johz.infinitic.lib.helpers.GenericHelper;

public class ToolDataJSON implements IJson {
		
	int harvestLevel;
	int durability;
	int extraDurability;
	int miningSpeed;
	int attack;
	float handleModifier;
	// You probably won't ever need this
	String style;
	// Four-part hex
	String color;
	
	int temperature;
	
	//Bow Parts
	float drawSpeed = 0.0f;
	float range = 0.0f;
	float bonusDamage = 0.0f;
	//Arrow Shafts
	float shaftModifier = 0.0f;
	int bonusAmmo = 0;
	//Fletchings
	float accuracy = 0.0f;	
	float fletchingModifier = 0.0f;
	//Bow Strings
	float stringModifier = 0.0f;
	
	//want to make throwing stars?
	boolean projectiles = false;
	
	String[] traits = {};

	//old, deprecated stats
	/**
	 *  @deprecated Unique material id - not needed since MC 1.7.10
	 */
	@Deprecated
	int ID = Integer.MIN_VALUE;
	/**
	 * @deprecated Reinforced is now a Trait
	 */
	@Deprecated
	int reinforced = 0;
	/**
	 * @deprecated Stonebound is now a Trait
	 */
	@Deprecated
	float stonebound = 0;
	/**
	 * @deprecated Use {@link #miningSpeed} instead
	 */	
	@Deprecated
	int miningspeed;  //typo
	/**
	 * @deprecated Use {@link #drawSpeed} instead
	 */	
	@Deprecated
	float drawspeed;  //typo
	@Deprecated
	float projectilespeed;
	@Deprecated
	float projectilemass; 
	@Deprecated
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
		
		if (reinforced != 0) {
			InfiniTiC.LOGGER.info("\"reinforced\" stat is no longer used and will be ignored!");
		}

		if (stonebound != 0) {
			InfiniTiC.LOGGER.info("\"stonebound\" stat is no longer used and will be ignored!");
		}

		if (drawspeed != 0) {
			InfiniTiC.LOGGER.info("\"drawspeed\" stat has been replaced with \"drawSpeed\"!");
			if (drawSpeed == 0) drawSpeed = drawspeed;
		}

		if (projectilespeed != 0 || projectilemass != 0 || projectilefragility != 0) {
			InfiniTiC.LOGGER.info("Projectile stats are not implemented and will be ignored!");
		}

	}
	
}
