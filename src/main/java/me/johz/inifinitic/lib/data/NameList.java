package me.johz.inifinitic.lib.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.johz.inifinitic.lib.errors.JSONValidationException;
import me.johz.inifinitic.lib.helpers.GenericHelper;

public class NameList implements IJson {
	
	public String[] oredict;
	public String[] ingots;
	public String[] dusts;
	public String[] blocks;
	public String[] ores;
	
	public String[] getBlocks() {
		return getThings("block");
	}
	
	public String[] getIngots() {
		return getThings("ingot");
	}
	
	public String[] getDusts() {
		return getThings("dust");
	}
	
	public String[] getOres() {
		return getThings("ore");
	}
	
	protected String[] getThings(String type) {
		checkNotNull();
		String[] itemArray;
		
		if (type == "ingot") {
			itemArray = ingots;
		} else if (type == "dust") {
			itemArray = dusts;
		} else if (type == "block") {
			itemArray = blocks;
		} else if (type == "ore") {
			itemArray = ores;
		} else {
			itemArray = new String[0];
		}
		
		List<String> l = new ArrayList<String>(Arrays.asList(itemArray));
		
		for (String name : prependOredict(type)) {
			l.add("ore:".concat(name));
		}
		
		return l.toArray(new String[l.size()]);
	}
	
	private void checkNotNull() {
		if (ingots == null) {
			ingots = new String[0];
		}
		if (dusts == null) {
			dusts = new String[0];
		}
		if (blocks == null) {
			blocks = new String[0];
		}
		if (ores == null) {
			ores = new String[0];
		}
	}

	private String[] prependOredict(String prep) {
		
		if (oredict == null) {
			oredict = new String[0];
		}
		
		String[] ls = oredict.clone();
		for (int i=0; i < ls.length; i++) {
			ls[i] = prep.concat(GenericHelper.capitalizeFirstLetter(ls[i]));
		}
		
		return ls;
	}

	@Override
	public void validate() throws JSONValidationException {
		// Nothing needs validating here, afaik
	}
	
}
