package me.johz.infinitic.client.textures;

import java.awt.Color;

public class InfiFluidTexture extends InfiBaseTexture {
	
    private boolean flow;
    
	public InfiFluidTexture(Color color, String name, boolean flowing) {
		super(color, name + (flowing ? "_flow" : ""));				
		flow = flowing;
	}

    @Override
    protected String getBasePath() {
        return "textures/blocks/";
    }

    @Override
    protected String getFilename() {
        return flow ? "flowing_fluid" : "still_fluid";
    }
	
}
