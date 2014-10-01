package me.johz.inifinitic.lib.data;

import java.awt.Color;

import me.johz.inifinitic.InfiniTiC;

public class ToolDataJSON {
	
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
	
	public int getColor(boolean withAlpha) {
		Color col = getColorType();
		if (withAlpha) {
			InfiniTiC.LOGGER.info(col.getAlpha());
			return col.getAlpha() << 24 | col.getRed() << 16 | col.getGreen() << 8 | col.getBlue();
		} else {
			return col.getRed() << 16 | col.getGreen() << 8 | col.getBlue();
		}
	}
	
	public Color getColorType() {
		return Color.decode(color);
	}
	
}
