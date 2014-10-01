package me.johz.inifinitic.lib.data;

import me.johz.inifinitic.lib.errors.JSONValidationException;

public interface IJson {
	
	public void validate() throws JSONValidationException;
	
}
