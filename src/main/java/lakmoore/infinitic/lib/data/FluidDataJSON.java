package lakmoore.infinitic.lib.data;

import lakmoore.infinitic.lib.errors.JSONValidationException;
import lakmoore.infinitic.lib.helpers.GenericHelper;
import net.minecraft.item.EnumRarity;

public class FluidDataJSON implements IJson {

	public String color;
	public int density = 2000;
	public int viscosity = 10000;
	public int temperature = 1000;
	public int luminosity = 10;
	public String rarity = "COMMON";
    
    public EnumRarity getRarity() {
    		return EnumRarity.valueOf(rarity);
    }    

	@Override
	public void validate() throws JSONValidationException {
		try {
			getRarity();
		}
		catch (Exception e) {
			throw new JSONValidationException("Rarity must be valid, e.g. \"COMMON\", \"UNCOMMON\", \"RARE\" or \"EPIC\".");
		}
		try {
			GenericHelper.decode(color);
		} catch (NumberFormatException e) {
			throw new JSONValidationException("Invalid Fluid color string (must be 3-part or 4-part hexadecimal, beginning with '#', e.g. \"#FF2277BB\"");
		}
	}

}
