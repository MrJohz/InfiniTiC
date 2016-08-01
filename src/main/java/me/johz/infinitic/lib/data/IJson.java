package me.johz.infinitic.lib.data;

import me.johz.infinitic.lib.errors.JSONValidationException;

public interface IJson {
	
	public void validate() throws JSONValidationException;
	
}
