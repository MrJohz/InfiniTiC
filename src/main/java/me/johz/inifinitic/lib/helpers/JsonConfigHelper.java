package me.johz.inifinitic.lib.helpers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import me.johz.inifinitic.lib.data.MaterialJSON;

public class JsonConfigHelper {
	
	private static final Gson gson = new Gson();
	
	public static MaterialJSON dataFromJSON(File f) {
		String s;
		MaterialJSON m;
		
		try {
			s = new String(Files.readAllBytes(f.toPath()));
			m = gson.fromJson(s, MaterialJSON.class);
		} catch (IOException e) {
			return null;
		} catch (JsonSyntaxException e) {
			return null;
		}
		
		return m;
	}
	
}
