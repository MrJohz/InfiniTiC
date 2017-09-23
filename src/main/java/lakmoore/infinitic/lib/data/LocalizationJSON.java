package lakmoore.infinitic.lib.data;

import lakmoore.infinitic.InfiniTiC;
import lakmoore.infinitic.lib.errors.JSONValidationException;

public class LocalizationJSON implements IJson
{
    
	public String locale;
	public String solid;
	public String liquid;
	public String bucket;

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
