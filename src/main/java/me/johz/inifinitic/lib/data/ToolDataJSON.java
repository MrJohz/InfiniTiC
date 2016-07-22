package me.johz.inifinitic.lib.data;

import java.awt.Color;

import tconstruct.library.TConstructRegistry;
import me.johz.inifinitic.lib.errors.JSONValidationException;

public class ToolDataJSON implements IJson {
	
	// Unique material id - should be unique or -1.
	int ID;
	
	int harvestLevel;
	int durability;
	int miningspeed;
	int attack;
	float handleModifier;
	int reinforced;
	float stonebound;
	// You probably won't ever need this
	String style;
	// Three-part hex
	// TODO: Fix this to allow four-part hex and other formats
	String color;
	
	int temperature;
	
	//Bow things
	int drawspeed;
	float projectilespeed;
	float projectilemass;
	float projectilefragility;
	
	public int getColor(boolean withAlpha) {
		Color col = getColorType();
		if (withAlpha) {
			return col.getAlpha() << 24 | col.getRed() << 16 | col.getGreen() << 8 | col.getBlue();
		} else {
			return col.getRed() << 16 | col.getGreen() << 8 | col.getBlue();
		}
	}
	
	public Color getColorType() {
		return Color.decode(color);
	}

	@Override
	public void validate() throws JSONValidationException {
		if (TConstructRegistry.toolMaterials.containsKey(ID)) {
			throw new JSONValidationException("ID already exists");
		}
		
		try {
			getColorType();
		} catch (NumberFormatException e) {
			throw new JSONValidationException("Invalid color string (must be 3-part hexadecimal, beginning with '#'");
		}
	}
	
}
