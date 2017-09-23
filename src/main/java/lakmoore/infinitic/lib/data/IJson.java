package lakmoore.infinitic.lib.data;

import lakmoore.infinitic.lib.errors.JSONValidationException;

public interface IJson {
	
	public void validate() throws JSONValidationException;
	
}
