package me.johz.infinitic.lib.data;

import me.johz.infinitic.InfiniTiC;
import me.johz.infinitic.lib.errors.JSONValidationException;

public class LocalizationJSON implements IJson
{
    
    String locale;
    String solid;
    String liquid;
    String bucket;

    @Override
    public void validate() throws JSONValidationException {
        boolean isValid = true;
        if (locale.isEmpty())
        {
            InfiniTiC.LOGGER.error("Localization: 'locale' must be specified");
            isValid = false;
        }
        
        if (solid.isEmpty())
        {
            InfiniTiC.LOGGER.error("Localization: 'solid' must be specified");
            isValid = false;            
        }
        
        if (locale.isEmpty())
        {
            InfiniTiC.LOGGER.error("Localization: 'liquid' must be specified");
            isValid = false;
        }
        
        if (solid.isEmpty())
        {
            InfiniTiC.LOGGER.error("Localization: 'bucket' must be specified");
            isValid = false;            
        }
        
        if(!isValid)
        {
            throw new JSONValidationException("Localization failed validation");
        }
    }

}
